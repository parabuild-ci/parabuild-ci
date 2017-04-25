//$Id: SQLFunction.java,v 1.5 2004/06/04 01:27:38 steveebersole Exp $
package net.sf.hibernate.dialect;

import net.sf.hibernate.QueryException;
import net.sf.hibernate.engine.Mapping;
import net.sf.hibernate.type.Type;

/**
 * Provides support routines for the HQL functions as used
 * in the various SQL Dialects
 * 
 * Provides an interface for supporting various HQL functions that are
 * translated to SQL. The Dialect and its sub-classes use this interface to
 * provide details required for processing of the function.
 * 
 * @author David Channon
 */
public interface SQLFunction {
	public Type getReturnType(Type columnType, Mapping mapping) throws QueryException;
	public boolean hasArguments();
	public boolean hasParenthesesIfNoArguments();
}
