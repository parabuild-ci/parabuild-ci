//$Id: NoArgSQLFunction.java,v 1.5 2004/06/04 01:27:38 steveebersole Exp $
package net.sf.hibernate.dialect;

import net.sf.hibernate.QueryException;
import net.sf.hibernate.engine.Mapping;
import net.sf.hibernate.type.Type;


/**
 * @author Michi
 */
public class NoArgSQLFunction implements SQLFunction {
    private Type returnType;
    private boolean hasParenthesesIfNoArguments;

    public NoArgSQLFunction(Type returnType) {
        this(returnType, true);
    }
    
    public NoArgSQLFunction(Type returnType, boolean hasParenthesesIfNoArguments) {
        this.returnType = returnType;
        this.hasParenthesesIfNoArguments = hasParenthesesIfNoArguments;
    }
    
    public Type getReturnType(Type columnType, Mapping mapping) throws QueryException {
        return returnType;
    }

    public boolean hasArguments() {
        return false;
    }

    public boolean hasParenthesesIfNoArguments() {
        return hasParenthesesIfNoArguments;
    }
}
