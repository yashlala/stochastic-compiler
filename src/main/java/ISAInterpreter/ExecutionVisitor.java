package ISAInterpreter;

import ISA.InstructionNodes.*;
import ISA.Visitors.ISAVisitor;

import java.util.List;

public class ExecutionVisitor implements ISAVisitor {
    private final RegisterFile registerFile = new RegisterFile();
    private final ExecutionEngine executionEngine = new ExecutionEngine();

    @Override
    public void visitAllInstructions(List<InstructionNode> instructions) {
        registerFile.clear();
        for (InstructionNode i : instructions) {
            i.accept(this);
        }
    }

    @Override
    public void visit(BinaryAdd binaryAdd) {

    }

    @Override
    public void visit(BinarySub binarySub) {

    }

    @Override
    public void visit(BinaryMul binaryMul) {

    }

    @Override
    public void visit(BinaryDiv binaryDiv) {

    }

    @Override
    public void visit(LessThan binaryLessThan) {

    }

    @Override
    public void visit(BinaryEq binaryEq) {

    }

    @Override
    public void visit(LabelNode labelNode) {

    }

    @Override
    public void visit(BinaryJz binaryJz) {

    }

    @Override
    public void visit(StochasticAdd stochasticAdd) {

    }

    @Override
    public void visit(StochasticSub stochasticSub) {

    }

    @Override
    public void visit(StochasticMul stochasticMul) {

    }

    @Override
    public void visit(StochasticDiv stochasticDiv) {

    }

    @Override
    public void visit(StochasticExp stochasticExp) {

    }

    @Override
    public void visit(StochasticTanh stochasticTanh) {

    }

    @Override
    public void visit(PrintIns printIns) {

    }

    @Override
    public void visit(LoadIns loadIns) {

    }

    @Override
    public void visit(StoreIns storeIns) {

    }
}
