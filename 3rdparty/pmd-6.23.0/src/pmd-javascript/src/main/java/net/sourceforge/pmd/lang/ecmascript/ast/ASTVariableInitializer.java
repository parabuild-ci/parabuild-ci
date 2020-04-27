/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.VariableInitializer;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTVariableInitializer extends AbstractEcmascriptNode<VariableInitializer> implements DestructuringNode {
    @Deprecated
    @InternalApi
    public ASTVariableInitializer(VariableInitializer variableInitializer) {
        super(variableInitializer);
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public EcmascriptNode<?> getTarget() {
        return (EcmascriptNode<?>) getChild(0);
    }

    public EcmascriptNode<?> getInitializer() {
        if (getNumChildren() > 0) {
            return (EcmascriptNode<?>) getChild(1);
        } else {
            return null;
        }
    }

    @Override
    public boolean isDestructuring() {
        return node.isDestructuring();
    }
}
