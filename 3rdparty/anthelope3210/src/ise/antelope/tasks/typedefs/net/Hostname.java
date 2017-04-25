package ise.antelope.tasks.typedefs.net;

import java.net.*;

/**
 * Copyright 2003
 *
 * @version   $Revision$
 */
public class Hostname implements NetOp {

    private String host = null;
    private boolean showIp = false;

    /**
     * Gets the defaultProperty attribute of the Hostname object
     *
     * @return   The defaultProperty value
     */
    public String getDefaultProperty() {
        return "hostname";
    }

    /**
     * Sets the showip attribute of the Hostname object
     *
     * @param b  The new showip value
     */
    public void setShowip(boolean b) {
        showIp = b;
    }

    /**
     * Sets the host attribute of the Hostname object
     *
     * @param h  The new host value
     */
    public void setHost(String h) {
        host = h;
    }

    /**
     * Description of the Method
     *
     * @return   Description of the Returned Value
     */
    public String execute() {
        try {
            InetAddress addr = null;
            if (host == null) {
                // localhost
                addr = InetAddress.getLocalHost();
            }
            else {
                // remote host
                addr = InetAddress.getByName(host);
            }
            String hostname = showIp ? addr.getHostAddress() : addr.getHostName();
            return hostname;
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

