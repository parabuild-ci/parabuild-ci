//$Id: StandardSQLFunction.java,v 1.5 2004/06/04 01:27:38 steveebersole Exp $
package net.sf.hibernate.dialect;

import net.sf.hibernate.engine.Mapping;
import net.sf.hibernate.type.Type;

/**
 * Provides a standard implementation that supports the majority of the HQL 
 * functions that are translated to SQL. The Dialect and its sub-classes use 
 * this class to provide details required for processing of the associated 
 * function.
 * 
 * @author David Channon
 */
public class StandardSQLFunction implements SQLFunction {
	private Type returnType = null;
	public StandardSQLFunction() {
	}
	public StandardSQLFunction(Type typeValue) {
		returnType = typeValue;
	}	
	public Type getReturnType(Type columnType, Mapping mapping) {
		if (returnType == null)
			return columnType;
		return returnType;		
	}
	public boolean hasArguments() {
		return true;
	}
	public boolean hasParenthesesIfNoArguments() {
		return true;
	}
}
	
