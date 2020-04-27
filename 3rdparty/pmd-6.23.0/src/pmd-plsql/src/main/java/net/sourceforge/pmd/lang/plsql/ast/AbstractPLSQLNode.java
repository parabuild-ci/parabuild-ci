/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.impl.javacc.AbstractJjtreeNode;
import net.sourceforge.pmd.lang.symboltable.Scope;

@Deprecated
@InternalApi
public abstract class AbstractPLSQLNode extends AbstractJjtreeNode<PLSQLNode> implements PLSQLNode {
    protected Object value;
    protected PLSQLParser parser;
    protected Scope scope;

    public AbstractPLSQLNode(int i) {
        super(i);
    }

    public AbstractPLSQLNode(PLSQLParser p, int i) {
        this(i);
        parser = p;
    }

    @Override
    public void jjtOpen() {
        if (beginLine == -1 && parser.token.next != null) {
            beginLine = parser.token.next.beginLine;
            beginColumn = parser.token.next.beginColumn;
        }
    }

    @Override
    public void jjtClose() {
        if (beginLine == -1 && (children == null || children.length == 0)) {
            beginColumn = parser.token.beginColumn;
        }
        if (beginLine == -1) {
            beginLine = parser.token.beginLine;
        }
        endLine = parser.token.endLine;
        endColumn = parser.token.endColumn;
    }

    public void jjtSetValue(Object value) {
        this.value = value;
    }

    public Object jjtGetValue() {
        return value;
    }

    @Override
    public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public Object childrenAccept(PLSQLParserVisitor visitor, Object data) {
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                ((PLSQLNode) children[i]).jjtAccept(visitor, data);
            }
        }
        return data;
    }


    @Override
    public String getXPathNodeName() {
        return PLSQLParserTreeConstants.jjtNodeName[id];
    }

    /*
     * You can override these two methods in subclasses of SimpleNode to
     * customize the way the node appears when the tree is dumped. If your
     * output uses more than one line you should override toString(String),
     * otherwise overriding toString() is probably all you need to do.
     */



    public String toString(String prefix) {
        return prefix + toString();
    }

    /*
     * Override this method if you want to customize how the node dumps out its
     * children.
     */

    public void dump(String prefix) {
        System.out.println(toString(prefix));
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                AbstractPLSQLNode n = (AbstractPLSQLNode) children[i];
                if (n != null) {
                    n.dump(prefix + " ");
                }
            }
        }
    }

    /**
     * Return node image converted to the normal Oracle form.
     *
     * <p>
     * Normally this is uppercase, unless the names is quoted ("name").
     * </p>
     */
    public String getCanonicalImage() {
        return PLSQLParser.canonicalName(this.getImage());
    }

    /**
     * Convert arbitrary String to normal Oracle format, under assumption that
     * the passed image is an Oracle name.
     *
     * <p>
     * This a helper method for PLSQL classes dependent on SimpleNode, that
     * would otherwise have to import PLSQParser.
     * </p>
     *
     * @param image
     * @return
     */
    public static String getCanonicalImage(String image) {
        return PLSQLParser.canonicalName(image);
    }

    @Override
    public Scope getScope() {
        if (scope == null) {
            return ((PLSQLNode) parent).getScope();
        }
        return scope;
    }

    @Override
    public void setScope(Scope scope) {
        this.scope = scope;
    }
}
