/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import scala.meta.Mod;

/**
 * The ASTModVarParam node implementation.
 */
public class ASTModVarParam extends AbstractScalaNode<Mod.VarParam> {

    @Deprecated
    @InternalApi
    public ASTModVarParam(Mod.VarParam scalaNode) {
        super(scalaNode);
    }

    @Override
    public <D, R> R accept(ScalaParserVisitor<D, R> visitor, D data) {
        return visitor.visit(this, data);
    }
}
