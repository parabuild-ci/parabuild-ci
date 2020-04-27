/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.dcd.graph;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;

import org.objectweb.asm.signature.SignatureReader;

import net.sourceforge.pmd.dcd.ClassLoaderUtil;
import net.sourceforge.pmd.dcd.DCD;
import net.sourceforge.pmd.dcd.asm.TypeSignatureVisitor;

/**
 * Represents a Class Constructor in a UsageGraph.
 * @deprecated See {@link DCD}
 */
@Deprecated
public class ConstructorNode extends MemberNode<ConstructorNode, Constructor<?>> {

    private WeakReference<Constructor<?>> constructorReference;

    public ConstructorNode(ClassNode classNode, String name, String desc) {
        super(classNode, name, desc);
        // getMember();
    }

    public boolean isStaticInitializer() {
        return ClassLoaderUtil.CLINIT.equals(name);
    }

    public boolean isInstanceInitializer() {
        return ClassLoaderUtil.INIT.equals(name);
    }

    @Override
    public Constructor<?> getMember() {
        if (ClassLoaderUtil.CLINIT.equals(name)) {
            return null;
        } else {
            Constructor<?> constructor = constructorReference == null ? null : constructorReference.get();
            if (constructor == null) {
                SignatureReader signatureReader = new SignatureReader(desc);
                TypeSignatureVisitor visitor = new TypeSignatureVisitor();
                signatureReader.accept(visitor);
                constructor = ClassLoaderUtil.getConstructor(super.getClassNode().getType(), name,
                        visitor.getMethodParameterTypes());
                constructorReference = new WeakReference<Constructor<?>>(constructor);
            }
            return constructor;
        }
    }

    @Override
    public String toStringLong() {
        if (ClassLoaderUtil.CLINIT.equals(name)) {
            return name;
        } else {
            return super.toStringLong();
        }
    }

    @Override
    public int compareTo(ConstructorNode that) {
        // Order by name
        int cmp = this.getName().compareTo(that.getName());
        if (cmp == 0) {
            // Order by parameter list length
            cmp = this.getMember().getParameterTypes().length - that.getMember().getParameterTypes().length;
            if (cmp == 0) {
                // Order by parameter class name
                for (int i = 0; i < this.getMember().getParameterTypes().length; i++) {
                    cmp = this.getMember().getParameterTypes()[i].getName()
                            .compareTo(that.getMember().getParameterTypes()[i].getName());
                    if (cmp != 0) {
                        break;
                    }
                }
            }
        }
        return cmp;
    }
}
