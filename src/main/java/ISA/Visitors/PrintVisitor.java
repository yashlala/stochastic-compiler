package ISA.Visitors;

import ISA.InstructionNodes.*;

import java.util.List;

public class PrintVisitor implements ISAVisitor {


    @Override
    public void visitAllInstructions(List<InstructionNode> instructions) {
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
    public void visit(LessThan binaryLessThan) {
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
    public void visit(BinaryJz binaryJz) {
        System.out.println(binaryJz);
    }

    @Override
    public void visit(StochasticAdd stochasticAdd) {
        System.out.println(stochasticAdd);
    }

    @Override
    public void visit(StochasticSub stochasticSub) {
        System.out.println(stochasticSub);
    }

    @Override
    public void visit(StochasticMul stochasticMul) {
        System.out.println(stochasticMul);
    }

    @Override
    public void visit(StochasticDiv stochasticDiv) {
        System.out.println(stochasticDiv);
    }

    @Override
    public void visit(StochasticExp stochasticExp) {
        System.out.println(stochasticExp);
    }

    @Override
    public void visit(StochasticTanh stochasticTanh) {
        System.out.println(stochasticTanh);
    }

    @Override
    public void visit(PrintIns printIns) {
        System.out.println(printIns);
    }

    @Override
    public void visit(LoadIns loadIns) {
        System.out.println(loadIns);
    }

    @Override
    public void visit(StoreIns storeIns) {
        System.out.println(storeIns);
    }

    @Override
    public void visit(LoadLiteralIns loadLiteralIns) {
        System.out.println(loadLiteralIns);
    }

    public void visit(BinaryToStochasticIns binaryToStochasticIns) {
        System.out.println(binaryToStochasticIns);
    }

    public void visit(StochasticToBinaryIns stochasticToBinaryIns) {
        System.out.println(stochasticToBinaryIns);
    }
}
