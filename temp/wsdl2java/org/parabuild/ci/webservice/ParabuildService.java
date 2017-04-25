/**
 * ParabuildService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.
 */

package org.parabuild.ci.webservice;

public interface ParabuildService extends javax.xml.rpc.Service {
    public java.lang.String getParabuildAddress();

    public org.parabuild.ci.webservice.Parabuild getParabuild() throws javax.xml.rpc.ServiceException;

    public org.parabuild.ci.webservice.Parabuild getParabuild(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
