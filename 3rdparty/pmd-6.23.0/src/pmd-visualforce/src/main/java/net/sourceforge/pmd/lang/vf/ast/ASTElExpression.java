/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTElExpression extends AbstractVFNode {
    @Deprecated
    @InternalApi
    public ASTElExpression(int id) {
        super(id);
    }

    @Deprecated
    @InternalApi
    public ASTElExpression(VfParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(VfParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
