/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ForLoop;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTForLoop extends AbstractEcmascriptNode<ForLoop> {
    @Deprecated
    @InternalApi
    public ASTForLoop(ForLoop forLoop) {
        super(forLoop);
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public EcmascriptNode<?> getInitializer() {
        return (EcmascriptNode<?>) getChild(0);
    }

    public EcmascriptNode<?> getCondition() {
        return (EcmascriptNode<?>) getChild(1);
    }

    public EcmascriptNode<?> getIncrement() {
        return (EcmascriptNode<?>) getChild(2);
    }

    public EcmascriptNode<?> getBody() {
        return (EcmascriptNode<?>) getChild(3);
    }
}
