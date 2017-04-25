//$Id: RelationalModel.java,v 1.8 2005/01/22 16:55:28 gloeglm Exp $
package net.sf.hibernate.mapping;

import net.sf.hibernate.engine.Mapping;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.dialect.Dialect;

/**
 * A relational object which may be created using DDL
 * @author Gavin King
 */
public interface RelationalModel {
	public String sqlCreateString(Dialect dialect, Mapping p, String defaultSchema) throws HibernateException;
	public String sqlDropString(Dialect dialect, String defaultSchema);
}







