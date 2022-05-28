package ISA.Visitors;

import ISA.InstructionNodes.*;

import java.util.Collection;

public class PrintVisitor implements ISAVisitor{


    @Override
    public void visitAllInstructions(Collection<InstructionNode> instructions) {
        instructions.forEach(ins -> ins.accept(this));
    }

    @Override
    public void visit(BinaryAdd binaryAdd) {
        System.out.println(binaryAdd);
    }

    @Override
    public void visit(BinarySub binarySub) {
        System.out.println(binarySub);
    }

    @Override
    public void visit(BinaryMul binaryMul) {
        System.out.println(binaryMul);
    }

    @Override
    public void visit(BinaryDiv binaryDiv) {
        System.out.println(binaryDiv);
    }

    @Override
    public void visit(BinaryLessThan binaryLessThan) {
        System.out.println(binaryLessThan);
    }

    @Override
    public void visit(BinaryEq binaryEq) {
        System.out.println(binaryEq);
    }

    @Override
    public void visit(LabelNode labelNode) {
        System.out.println(labelNode);
    }

    @Override
    public void visit(BinaryJump binaryJump) {
        System.out.println(binaryJump);
    }
}
