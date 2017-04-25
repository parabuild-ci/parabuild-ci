package net.sf.hibernate.jca;

import java.sql.SQLException;

import javax.resource.ResourceException;
import javax.resource.spi.ManagedConnectionMetaData;

/**
 *
 * Implementation delegates to the database connection meta data object
 *
 */
public class MetaDataImpl implements ManagedConnectionMetaData {
	
	private ManagedConnectionImpl mc;
	
	public MetaDataImpl(ManagedConnectionImpl mc) {
		this.mc = mc;
	}
	
	public String getEISProductName() throws ResourceException {
		String result = null;
		try {
			result = mc.getSqlConnection().getMetaData().getUserName();
		}
		catch (SQLException e) {
			final String message = "Exception getting product name";
			final ResourceException re = new ResourceException(message);
			re.setLinkedException(e);
			throw re;
		}
		return result;
		
	}
	
	public String getEISProductVersion() throws ResourceException {
		String result = null;
		try {
			result = mc.getSqlConnection().getMetaData().getDriverVersion();
		}
		catch (SQLException e) {
			final String message = "Exception getting product version";
			final ResourceException re = new ResourceException(message);
			re.setLinkedException(e);
			throw re;
		}
		return result;
		
	}
	
	public int getMaxConnections() throws ResourceException {
		int result = 0;
		try {
			result = mc.getSqlConnection().getMetaData().getMaxConnections();
		}
		catch (SQLException e) {
			final String message = "Exception getting max connections";
			final ResourceException re = new ResourceException(message);
			re.setLinkedException(e);
			throw re;
		}
		return result;
		
	}
	
	public String getUserName() throws ResourceException {
		String result = null;
		try {
			result = mc.getSqlConnection().getMetaData().getUserName();
		}
		catch (SQLException e) {
			final String message = "Exception getting connection user name";
			final ResourceException re = new ResourceException(message);
			re.setLinkedException(e);
			throw re;
		}
		return result;
	}
}

