package ISA.Visitors;

import ISA.InstructionNodes.*;

import java.util.Collection;

public interface ISAVisitor {
    void visitAllInstructions(Collection<InstructionNode> instructions);

    void visit(BinaryAdd binaryAdd);

    void visit(BinarySub binarySub);

    void visit(BinaryMul binaryMul);

    void visit(BinaryDiv binaryDiv);

    void visit(BinaryLessThan binaryLessThan);

    void visit(BinaryEq binaryEq);

    void visit(LabelNode labelNode);

    void visit(BinaryJump binaryJump);

    void visit(StochasticAdd stochasticAdd);

    void visit(StochasticSub stochasticSub);

    void visit(StochasticMul stochasticMul);

    void visit(StochasticDiv stochasticDiv);

    void visit(StochasticExp stochasticExp);

    void visit(StochasticTanh stochasticTanh);
}
