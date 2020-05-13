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
package org.parabuild.ci.remote.internal;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.remote.services.RemoteBuilderWebService;
import org.parabuild.ci.remote.services.RemoteFileDescriptor;
import org.parabuild.ci.remote.services.WebServiceConstants;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Gets a file from remote agent
 */
public final class RemoteFileGetter {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(RemoteFileGetter.class); // NOPMD

  private final ParabuildHessianProxyFactory proxyFactory;
  private final WebServiceLocator webServiceLocator;


  public RemoteFileGetter(final WebServiceLocator webServiceLocator) {
    this.proxyFactory = webServiceLocator.getProxyFactory();
    this.webServiceLocator = webServiceLocator;
  }


  public void copy(final String remoteFileName, final OutputStream outputTo) throws IOException {
    InputStream responceIS = null;
    OutputStream connectionOS = null;
    InputStream connectionIS = null;
    try {
      // call backend
      final URLConnection conn = proxyFactory.openConnection(new URL(webServiceLocator.getURL()));
      conn.addRequestProperty(WebServiceConstants.REQUEST_HEADER_CUSTOM_PROTOCOL, Boolean.toString(true));
      conn.connect();
      connectionOS = conn.getOutputStream();
      final HessianOutput ho = proxyFactory.getHessianOutput(connectionOS);
      ho.startCall(WebServiceConstants.METHOD_GET_FILE);
      ho.writeString(remoteFileName);
      ho.completeCall();
      connectionOS.close();

      // read response
      connectionIS = conn.getInputStream();
      final HessianInput hin = new HessianInput(connectionIS);
      try {
        hin.startReply();
      } catch (final Throwable throwable) { // NOPMD - because startReply, thanks to caucho, declares Throwable
        if (throwable instanceof IOException) throw (IOException)throwable; // NOPMD - we still have to know what is the exception
        throw new IOException(StringUtils.toString(throwable), throwable);
      }
      responceIS = hin.readInputStream();
      IoUtils.copyInputToOuputStream(responceIS, outputTo);
      hin.completeReply();
    } finally {
      IoUtils.closeHard(connectionIS);
      IoUtils.closeHard(connectionOS);
      IoUtils.closeHard(responceIS);
    }
  }


  public void copy(final String remoteFileName, final File outputToLocalFile) throws IOException {
    if (outputToLocalFile == null) return;
    OutputStream outputTo = null;
    boolean exception = false;
    try {
      outputTo = new BufferedOutputStream(new FileOutputStream(outputToLocalFile));
      copy(remoteFileName, outputTo);
    } catch (final IOException | RuntimeException e) {
      exception = true;
      throw e;
    } finally {
      IoUtils.closeHard(outputTo);
      if (exception) {
        IoUtils.deleteFileHard(outputToLocalFile);
      }
      setLastModifiedTime(remoteFileName, outputToLocalFile);
    }
  }


  /**
   * This helper method sets destination's last modofied time to
   * that one of the source.
   */
  private void setLastModifiedTime(final String remoteSource, final File localDestination) {
    try {
      final RemoteBuilderWebService webService = webServiceLocator.getWebService();
      final RemoteFileDescriptor fileDescriptor = webService.getFileDescriptor(remoteSource);
      localDestination.setLastModified(fileDescriptor.lastModified());
    } catch (final Exception e) {
      IoUtils.ignoreExpectedException(e);
    }
  }
}