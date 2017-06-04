package ise.antelope.tasks;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * A task to put hostname or IP in a property. The default property is
 * 'hostname'. <p>
 *
 * I updated this task in Apr 2005 to be able to show all hostnames and IP's for
 * the local machine, which is sometimes handy for machines with dual cards,
 * like maybe an ethernet card and a wireless card. <p>
 *
 * Dale Anson, May 2001
 *
 * @version   $Revision: 151 $
 */
public class HostnameTask extends Task {

    private String property = "hostname";
    private boolean useIp = false;
    private String host = null;
    private String nIC = null;
    private boolean failOnError = false;
    private boolean showAll = false;
    private boolean canonical = true;
    private int outputType = 0;
    public static final int HOST = 0;
    public static final int IP = 1;
    public static final int CANON = 2;

    /**
     * @param p  The name of the property to hold the hostname or IP address.
     */
    public void setProperty(String p) {
        property = p;
    }
    
    /**
     * Should this task get the IP address of the local host instead of the
     * hostname? Default is no, get the hostname, not the IP address.
     *
     * @param b  The new ip value
     */
    public void setShowip(boolean b) {
        useIp = b;
        outputType = IP;
    }

    /**
     * Should the build fail if this task fails? Default is no.
     *
     * @param b  The new failonerror value
     */
    public void setFailonerror(boolean b) {
        failOnError = b;
    }

    /**
     * Set a specific network interface to get info for.
     *
     * @param n  The new nic value
     */
    public void setNic(String n) {
        nIC = n;
    }

    /**
     * Show all network interfaces, hostnames and IPs for the local host.
     * Default is false. If used, output will be something like:<br>
     * lo:127.0.0.1, eth0:mycomputer.somewhere.com, eth1:wireless.somwhere.com
     *
     * @param b  if true, show all hostnames and IPs
     */
    public void setShowall(boolean b) {
        showAll = b;
    }
    
    public void setShowcanonical(boolean b) {
        canonical = b;
        outputType = CANON;
    }   

    /** Description of the Method */
    public void execute() {
        try {
            if (showAll) {
                StringBuffer hostnames = new StringBuffer();
                Enumeration nics = NetworkInterface.getNetworkInterfaces();
                while (nics.hasMoreElements()) {
                    NetworkInterface nic = (NetworkInterface) nics.nextElement();
                    hostnames.append(nic.getName() + ":");
                    Enumeration addrs = nic.getInetAddresses();
                    while (addrs.hasMoreElements()) {
                        InetAddress addr = (InetAddress) addrs.nextElement();
                        String hostname = getAddress(addr);
                        if (hostname != null && hostname.trim().length() > 0)
                            hostnames.append(hostname);
                        if (addrs.hasMoreElements())
                            hostnames.append(", ");
                    }
                    if (nics.hasMoreElements())
                        hostnames.append(", ");
                }
                getProject().setProperty(property, hostnames.toString());
            }
            else if (nIC != null) {
                StringBuffer hostnames = new StringBuffer();
                Enumeration nics = NetworkInterface.getNetworkInterfaces();
                while (nics.hasMoreElements()) {
                    NetworkInterface nic = (NetworkInterface) nics.nextElement();
                    if (nIC.equals(nic.getName())) {
                        hostnames.append(nic.getName() + ":");
                        Enumeration addrs = nic.getInetAddresses();
                        while (addrs.hasMoreElements()) {
                            InetAddress addr = (InetAddress) addrs.nextElement();
                            String hostname = getAddress(addr);
                            if (hostname != null && hostname.trim().length() > 0)
                                hostnames.append(hostname);
                            if (addrs.hasMoreElements())
                                hostnames.append(", ");
                        }
                    }
                }
                getProject().setProperty(property, hostnames.toString());
            }
            else {
                InetAddress addr = InetAddress.getLocalHost();
                String hostname = getAddress(addr);
                getProject().setProperty(property, hostname);
            }
        }
        catch (IOException e) {
            if (failOnError)
                throw new BuildException(e.getMessage());
            else
                log(e.getMessage());
        }
    }
    
    private String getAddress(InetAddress addr) {
        String hostname = "";
        switch (outputType) {
            case IP:
                hostname = addr.getHostAddress();
                break;
            case CANON:
                hostname = addr.getCanonicalHostName();
                break;
            default:
                hostname = addr.getHostName();
        }
        return hostname;
    }
}

