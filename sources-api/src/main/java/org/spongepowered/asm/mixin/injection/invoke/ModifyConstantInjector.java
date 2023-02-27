/*
 * This file is part of Mixin, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.asm.mixin.injection.invoke;

import org.spongepowered.asm.logging.Level;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.spongepowered.asm.mixin.MixinEnvironment.Option;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.code.Injector;
import org.spongepowered.asm.mixin.injection.invoke.util.InsnFinder;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.InjectionNodes.InjectionNode;
import org.spongepowered.asm.mixin.injection.struct.Target;
import org.spongepowered.asm.mixin.injection.struct.Target.Extension;
import org.spongepowered.asm.mixin.injection.throwables.InvalidInjectionException;
import org.spongepowered.asm.util.Bytecode;
import org.spongepowered.asm.util.Locals;
import org.spongepowered.asm.util.SignaturePrinter;

/**
 * A bytecode injector which allows a specific constant value to be identified
 * and replaced with a callback. For details see javadoc for
 * {@link ModifyConstant &#64;ModifyConstant}.
 */
public class ModifyConstantInjector extends RedirectInjector {
    
    /**
     * Offset between "implicit zero" opcodes and "explicit int comparison"
     * opcodes
     */
    private static final int OPCODE_OFFSET = Opcodes.IF_ICMPLT - Opcodes.IFLT;

    /**
     * @param info Injection info
     */
    public ModifyConstantInjector(InjectionInfo info) {
        super(info, "@ModifyConstant");
    }
    
    @Override
    protected void inject(Target target, InjectionNode node) {
        if (!this.preInject(node)) {
            return;
        }
            
        if (node.isReplaced()) {
            throw new UnsupportedOperationException("Target failure for " + this.info);
        }
        
        AbstractInsnNode targetNode = node.getCurrentTarget();
        if (targetNode instanceof TypeInsnNode) {
            this.checkTargetModifiers(target, false);
            this.injectTypeConstantModifier(target, (TypeInsnNode)targetNode);
            return;
        }
        
        if (targetNode instanceof JumpInsnNode) {
            this.checkTargetModifiers(target, false);
            this.injectExpandedConstantModifier(target, (JumpInsnNode)targetNode);
            return;
        }
        
        if (Bytecode.isConstant(targetNode)) {
            this.checkTargetModifiers(target, false);
            this.injectConstantModifier(target, targetNode);
            return;
        }
        
        throw new InvalidInjectionException(this.info, String.format("%s annotation is targetting an invalid insn in %s in %s",
                this.annotationType, target, this));
    }
    
    private void injectTypeConstantModifier(Target target, TypeInsnNode typeNode) {
        int opcode = typeNode.getOpcode();
        if (opcode != Opcodes.INSTANCEOF) {
            throw new InvalidInjectionException(this.info, String.format("%s annotation does not support %s insn in %s in %s",
                    this.annotationType, Bytecode.getOpcodeName(opcode), target, this));
        }
        this.injectAtInstanceOf(target, typeNode);
    }

    /**
     * Injects a constant modifier at an implied-zero
     * 
     * @param target target method
     * @param jumpNode jump instruction (must be IFLT, IFGE, IFGT or IFLE)
     */
    private void injectExpandedConstantModifier(Target target, JumpInsnNode jumpNode) {
        int opcode = jumpNode.getOpcode();
        if (opcode < Opcodes.IFLT || opcode > Opcodes.IFLE) {
            throw new InvalidInjectionException(this.info, String.format("%s annotation selected an invalid opcode %s in %s in %s",
                    this.annotationType, Bytecode.getOpcodeName(opcode), target, this)); 
        }
        
        Extension extraStack = target.extendStack();
        final InsnList insns = new InsnList();
        insns.add(new InsnNode(Opcodes.ICONST_0));
        AbstractInsnNode invoke = this.invokeConstantHandler(Type.getType("I"), target, extraStack, insns, insns);
        insns.add(new JumpInsnNode(opcode + ModifyConstantInjector.OPCODE_OFFSET, jumpNode.label));
        extraStack.add(1).apply();
        target.replaceNode(jumpNode, invoke, insns);
    }

    private void injectConstantModifier(Target target, AbstractInsnNode constNode) {
        final Type constantType = Bytecode.getConstantType(constNode);
        
        if (constantType.getSort() <= Type.INT && this.info.getMixin().getOption(Option.DEBUG_VERBOSE)) {
            this.checkNarrowing(target, constNode, constantType);
        }
        
        Extension extraStack = target.extendStack();
        final InsnList before = new InsnList();
        final InsnList after = new InsnList();
        AbstractInsnNode invoke = this.invokeConstantHandler(constantType, target, extraStack, before, after);
        extraStack.apply();
        target.wrapNode(constNode, invoke, before, after);
    }

    private AbstractInsnNode invokeConstantHandler(Type constantType, Target target, Extension extraStack, InsnList before, InsnList after) {
        InjectorData handler = new InjectorData(target, "constant modifier");
        this.validateParams(handler, constantType, constantType);

        if (!this.isStatic) {
            before.insert(new VarInsnNode(Opcodes.ALOAD, 0));
            extraStack.add();
        }
        
        if (handler.captureTargetArgs > 0) {
            this.pushArgs(target.arguments, after, target.getArgIndices(), 0, handler.captureTargetArgs, extraStack);
        }
        
        return this.invokeHandler(after);
    }

    private void checkNarrowing(Target target, AbstractInsnNode constNode, Type constantType) {
        AbstractInsnNode pop = new InsnFinder().findPopInsn(target, constNode);

        if (pop == null) { // Not found, give up early
            return;
        } else if (pop instanceof FieldInsnNode) { // Integer return, check for narrowing conversion
            FieldInsnNode fieldNode = (FieldInsnNode)pop;
            Type fieldType = Type.getType(fieldNode.desc);
            this.checkNarrowing(target, constNode, constantType, fieldType, target.indexOf(pop), String.format("%s %s %s.%s",
                    Bytecode.getOpcodeName(pop), SignaturePrinter.getTypeName(fieldType, false), fieldNode.owner.replace('/', '.'), fieldNode.name));
        } else if (pop.getOpcode() == Opcodes.IRETURN) { // Integer return, check for narrowing conversion
            this.checkNarrowing(target, constNode, constantType, target.returnType, target.indexOf(pop), "RETURN "
                    + SignaturePrinter.getTypeName(target.returnType, false));
        } else if (pop.getOpcode() == Opcodes.ISTORE) { // Integer store, attempt to get the relevant local type
            int var = ((VarInsnNode)pop).var;
            LocalVariableNode localVar = Locals.getLocalVariableAt(target.classNode, target.method, pop, var);

            // Frankly this will not work in 90% of cases, it basically only works if the variable being assigned is actually
            // a method argument, and is pretty much never going to work for any other type of local variable
            if (localVar != null && localVar.desc != null) {
                String name = localVar.name != null ? localVar.name : "unnamed";
                Type localType = Type.getType(localVar.desc);
                this.checkNarrowing(target, constNode, constantType, localType, target.indexOf(pop), String.format("ISTORE[var=%d] %s %s", var, 
                        SignaturePrinter.getTypeName(localType, false), name));
            }
        }
    }

    private void checkNarrowing(Target target, AbstractInsnNode constNode, Type constantType, Type type, int index, String description) {
        int fromSort = constantType.getSort();
        int toSort = type.getSort();
        if (toSort < fromSort) {
            String fromType = SignaturePrinter.getTypeName(constantType, false);
            String toType = SignaturePrinter.getTypeName(type, false);
            String message = toSort == Type.BOOLEAN ? ". Implicit conversion to <boolean> can cause nondeterministic (JVM-specific) behaviour!" : "";
            Level level = toSort == Type.BOOLEAN ? Level.ERROR : Level.WARN;
            Injector.logger.log(level, "Narrowing conversion of <{}> to <{}> in {} target {} at opcode {} ({}){}", fromType, toType, this.info,
                    target, index, description, message);
        }
    }

}
