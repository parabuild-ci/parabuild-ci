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
package org.parabuild.ci.archive.internal;

import java.io.*;
import java.util.zip.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.common.*;

/**
 * Zipped log input stream is a lazily initalized input stream that
 * reads from a zippped archive file.
 *
 * @see ArchiveManagerImpl#getArchivedLogInputStream
 */
public final class PackedLogInputStream extends InputStream {

  private static final Log log = LogFactory.getLog(PackedLogInputStream.class);

  private File zippedLogFile;
  private String entryName;
  private ZipFile file = null;
  private InputStream delegate = null;
  private boolean closed = false;


  /**
   * Constructor.
   *
   * Creates PackedLogInputStream from a given zippedLogFile. Input
   * stream will read from an entry defined by entryName.
   */
  public PackedLogInputStream(final File zippedLogFile, final String entryName) {
    ArgumentValidator.validateArgumentNotNull(zippedLogFile, "log file");
    ArgumentValidator.validateArgumentNotBlank(entryName, "entry name");
    if (!zippedLogFile.exists()) throw new IllegalArgumentException("Log file does not exist");
    if (!zippedLogFile.getName().endsWith(".zip")) throw new IllegalArgumentException("Log file is not a zip file");
    this.entryName = entryName;
    this.zippedLogFile = zippedLogFile;
  }


  /**
   * Lazily initializes wrapped ZipInputStream.
   *
   * @return ZipInputStream wrapped in an InputStream.
   */
  private InputStream getDelegate() throws IOException {
    checkNotClosed();
    if (delegate == null) {
      // init stuff
      file = new ZipFile(zippedLogFile);
      final ZipEntry zipEntry = file.getEntry(entryName);
      delegate = file.getInputStream(zipEntry);
    }
    return delegate;
  }


  private void checkNotClosed() throws IOException {
    if (closed) throw new IOException("Stream was already closed");
  }


  public int read() throws IOException {
    return getDelegate().read();
  }


  public int read(final byte[] b) throws IOException {
    return getDelegate().read(b);
  }


  public int read(final byte[] b, final int off, final int len) throws IOException {
    return getDelegate().read(b, off, len);
  }


  public long skip(final long n) throws IOException {
    return getDelegate().skip(n);
  }


  public int available() throws IOException {
    return getDelegate().available();
  }


  public void close() throws IOException {
    checkNotClosed();
    IoUtils.closeHard(delegate);
    IoUtils.closeHard(file);
    delegate = null;
    file = null;
    closed = true;
  }


  public synchronized void mark(final int readlimit) {
    try {
      getDelegate().mark(readlimit);
    } catch (final IOException e) {
      throw IoUtils.makeIllegalStateException(e);
    }
  }


  public synchronized void reset() throws IOException {
    getDelegate().reset();
  }


  public boolean markSupported() {
    try {
      return getDelegate().markSupported();
    } catch (final IOException e) {
      throw IoUtils.makeIllegalStateException(e);
    }
  }


  public boolean equals(final Object o) {
    if (this == o) return true;
    if (!(o instanceof PackedLogInputStream)) return false;

    final PackedLogInputStream zippedLogInputStream = (PackedLogInputStream)o;

    if (delegate != null ? !delegate.equals(zippedLogInputStream.delegate) : zippedLogInputStream.delegate != null) return false;
    if (!entryName.equals(zippedLogInputStream.entryName)) return false;
    if (file != null ? !file.equals(zippedLogInputStream.file) : zippedLogInputStream.file != null) return false;
    return zippedLogFile.equals(zippedLogInputStream.zippedLogFile);
  }


  public int hashCode() {
    int result = zippedLogFile.hashCode();
    result = 29 * result + entryName.hashCode();
    result = 29 * result + (file != null ? file.hashCode() : 0);
    result = 29 * result + (delegate != null ? delegate.hashCode() : 0);
    return result;
  }


  protected void finalize() throws Throwable {
    if (!closed && (delegate != null || file != null)) {
      IoUtils.closeHard(this);
      if (log.isWarnEnabled()) log.warn("PackedLogInputStream was closed at finalize");
    }
    super.finalize(); // NOPMD
  }


  public String toString() {
    return "PackedLogInputStream{" +
      "zippedLogFile=" + zippedLogFile +
      ", entryName='" + entryName + '\'' +
      ", file=" + file +
      ", delegate=" + delegate +
      ", closed=" + closed +
      '}';
  }
}
