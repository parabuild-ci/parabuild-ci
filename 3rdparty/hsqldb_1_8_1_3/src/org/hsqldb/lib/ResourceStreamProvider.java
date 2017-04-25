/* Copyright (c) 2001-2008, The HSQL Development Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the HSQL Development Group nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL HSQL DEVELOPMENT GROUP, HSQLDB.ORG,
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package org.hsqldb.lib;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.hsqldb.persist.Logger;

/**
 * An abstraction over the standard java.lang.Class.getResource[AsStream]() that
 * allows a superceding class loader to be injected at the top of the precedence
 * chain, for instance when the runtime must perform embedded resource loading
 * through a custom or non-standard protocol.
 *
 * @author boucherb@users
 * @version 1.8.1.3
 * @since 1.8.1.3
 */
public class ResourceStreamProvider {

    private static ClassLoader loader;
    private static HashSet     forbiddenProtocols = new HashSet();

    static {
        forbiddenProtocols.add("file");
    }

    private ResourceStreamProvider() {}

    public static synchronized void setLoader(ClassLoader loader) {
        ResourceStreamProvider.loader = loader;
    }

    public static synchronized ClassLoader getLoader() {
        return ResourceStreamProvider.loader;
    }

    public static Set forbiddenProtocols() {
        return forbiddenProtocols;
    }

    public static boolean exists(String resource) {

        ClassLoader loader = ResourceStreamProvider.getLoader();
        URL         url    = null;

        if (loader == null) {
            url = Logger.class.getResource(resource);
        } else {
            url = loader.getResource(resource);

            if (url == null) {
                url = Logger.class.getResource(resource);
            }
        }

        return url != null
               && !forbiddenProtocols().contains(url.getProtocol());
    }

    public static InputStream getResourceAsStream(String resource)
    throws IOException {

        ClassLoader loader = ResourceStreamProvider.getLoader();
        URL         url    = null;

        if (loader == null) {
            url = Logger.class.getResource(resource);
        } else {
            url = loader.getResource(resource);

            if (url == null) {
                url = Logger.class.getResource(resource);
            }
        }

        if (url == null) {
            throw new IOException("Missing resource: " + resource);
        }

        String protocol = url.getProtocol();

        if (forbiddenProtocols.contains(protocol)) {
            throw new IOException("Wrong protocol [" + protocol
                                  + "] for resource : " + resource);
        }

        return url.openStream();
    }
}
