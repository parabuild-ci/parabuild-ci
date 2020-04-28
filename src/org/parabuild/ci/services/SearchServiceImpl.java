/*
 * Parabuild CI licenses this file to You under the LGPL 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parabuild.ci.services;

import EDU.oswego.cs.dl.util.concurrent.BoundedLinkedQueue;
import EDU.oswego.cs.dl.util.concurrent.QueuedExecutor;
import EDU.oswego.cs.dl.util.concurrent.ThreadFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.parabuild.ci.configuration.ConfigurationConstants;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.util.DirectoryTraverserCallback;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.util.ThreadUtils;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Enumeration;

/**
 * Search service - provides access to build results search and
 * indexing capabilities.
 */
public final class SearchServiceImpl implements SearchService {


  /**
   * Defines Lucene write timeout.
   */
  private static final int LUCENE_WRITE_LOCK_TIMEOUT = 6000000;

  private static final Log log = LogFactory.getLog(SearchServiceImpl.class);

  private byte status = SERVICE_STATUS_NOT_STARTED;
  private final QueuedExecutor indexerQueue;


  public SearchServiceImpl() {
    indexerQueue = new QueuedExecutor(new BoundedLinkedQueue(10));
    indexerQueue.setThreadFactory(new ThreadFactory() {
      public Thread newThread(final Runnable runnable) {
        if (log.isDebugEnabled()) log.debug("Creating indexer thread");
        return ThreadUtils.makeDaemonThread(runnable, "Indexer");
      }
    });
  }


  public void startupService() {
    // clean up lock files possibly left in the temp dir (see #849)
    final String tempDirProperty = System.getProperty("java.io.tmpdir");
    if (!StringUtils.isBlank(tempDirProperty)) {
      final File tempDir = new File(tempDirProperty);
      if (tempDir.exists()) {
        try {
          IoUtils.traverseDir(tempDir, new DirectoryTraverserCallback() {
            public boolean callback(final File file) {
              final String fileString = file.toString();
              if (fileString.endsWith("write.lock")
                || fileString.endsWith("commit.lock")) {
                file.delete();
              }
              return true;
            }
          });
        } catch (final Exception e) {
          log.warn("Error while traversing temp dir: " + StringUtils.toString(e), e);
        }
      }
    }

    IndexWriter indexWriter = null;
    try {
      System.setProperty("org.apache.lucene.writeLockTimeout", Integer.toString(LUCENE_WRITE_LOCK_TIMEOUT));
      final boolean create = ConfigurationConstants.INDEX_HOME.list().length == 0;
//      if (log.isDebugEnabled()) log.debug("create: " + create);
      indexWriter = new IndexWriter(ConfigurationConstants.INDEX_HOME, makeAnalyzer(), create);
      status = SERVICE_STATUS_STARTED;
    } catch (final Exception e) {
      final Error error = new Error("Failed to start search service: " + StringUtils.toString(e));
      error.setSendEmail(true);
      error.setDetails(e);
      error.setSubsystemName(Error.ERROR_SUBSYSTEM_SEARCH);
      error.setErrorLevel(Error.ERROR_LEVEL_FATAL);
      ErrorManagerFactory.getErrorManager().reportSystemError(error);
      status = SERVICE_STATUS_FAILED;
    } finally {
      closeHard(indexWriter);
    }
  }


  public void shutdownService() {
    indexerQueue.shutdownAfterProcessingCurrentTask();
    status = SERVICE_STATUS_NOT_STARTED;
  }


  public ServiceName serviceName() {
    return ServiceName.SEARCH_SERVICE;
  }


  public byte getServiceStatus() {
    return status;
  }


  /**
   * Adds a request to perform indexing.
   *
   * @param indexRequest
   */
  public void queueIndexRequest(final IndexRequest indexRequest) {
    try {
      final IndexerQueueRequest queueRequest = new IndexerQueueRequest(indexRequest);
      indexerQueue.execute(queueRequest);
    } catch (final InterruptedException e) {
      IoUtils.ignoreExpectedException(e);
    }
  }


  /**
   * Performs search.
   *
   * @param queryString
   * @param defaultField
   */
  public Hits search(final String queryString, final String defaultField) throws IOException, ParseException {
    final Searcher searcher = new IndexSearcher(ConfigurationConstants.INDEX_HOME.getAbsolutePath());
    final Analyzer analyzer = makeAnalyzer();
    final QueryParser queryParser = new QueryParser(defaultField, analyzer);
    queryParser.setOperator(QueryParser.DEFAULT_OPERATOR_AND);
    final Query query = queryParser.parse(queryString);
    if (log.isDebugEnabled()) log.debug("Searching for: " + query.toString("contents"));
    final Hits hits = searcher.search(query);
    // REVIEWME: simeshev@parabuilci.org -> how do we close searcher?
    if (log.isDebugEnabled()) log.debug(hits.length() + " total matching documents");
    return hits;
  }


  /**
   * Makes our analyzer. The returned analyzer should remain the
   * same for indexing and for search.
   *
   * @return Analyzer
   */
  private static Analyzer makeAnalyzer() {
    return new StandardAnalyzer();
  }


  /**
   * Helper method to close index writer hard.
   * 
   * @param indexWriter
   */
  private static void closeHard(final IndexWriter indexWriter) {// close index writer hard
    if (indexWriter != null) {
      try {
        indexWriter.close();
      } catch (final Exception ex) {
        log.warn("Error while closing index writer: " + StringUtils.toString(ex), ex);
      }
    }
  }


  /**
   * Request sent to the indexer queue.
   */
  private static final class IndexerQueueRequest implements Runnable {

    private final IndexRequest indexRequest;


    /**
     * Constructor. Create indexer queue request from index
     * request.
     *
     * @param indexRequest to process.
     */
    IndexerQueueRequest(final IndexRequest indexRequest) {
      this.indexRequest = indexRequest;
    }


    /**
     * This method must catch and report all the exceptions,
     * otherwise a queue thread will die.
     *
     * @see Runnable#run()
     * @see Thread#run()
     */
    public void run() {
      IndexWriter indexWriter = null;
      Document documentToIndex = null;
      try {
//        if (log.isDebugEnabled()) log.debug("Indexing");
        final Analyzer analyzer = makeAnalyzer();
//        if (log.isDebugEnabled()) log.debug("Create index writer");
        final boolean create = ConfigurationConstants.INDEX_HOME.list().length == 0;
        indexWriter = new IndexWriter(ConfigurationConstants.INDEX_HOME, analyzer, create);
//        if (log.isDebugEnabled()) log.debug("Add doc");
        documentToIndex = indexRequest.getDocumentToIndex();
        indexWriter.addDocument(documentToIndex);
//        if (log.isDebugEnabled()) log.debug("Close writer");
      } catch (final Exception e) {
        if (log.isDebugEnabled()) log.debug(e);
        final Error error = new Error("Indexing error: " + StringUtils.toString(e));
        error.setDetails(e);
        error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
        error.setSubsystemName(Error.ERROR_SUBSYSTEM_SEARCH);
        error.setSendEmail(true);
        ErrorManagerFactory.getErrorManager().reportSystemError(error);
      } finally {
        // close index writer
        closeHard(indexWriter);
        // close open readers in fields if any
        if (documentToIndex != null) {
          final Enumeration fields = documentToIndex.fields();
          while (fields.hasMoreElements()) {
            final Reader reader = ((Field)fields.nextElement()).readerValue();
            if (reader != null) IoUtils.closeHard(reader);
          }
        }
      }
    }


    public String toString() {
      return "IndexerQueueRequest{" +
        "indexRequest=" + indexRequest +
        '}';
    }
  }


  public String toString() {
    return "SearchServiceImpl{" +
      "status=" + status +
      ", indexerQueue=" + indexerQueue +
      '}';
  }
}
