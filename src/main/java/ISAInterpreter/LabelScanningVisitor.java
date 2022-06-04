package ISAInterpreter;

import ISA.InstructionNodes.*;
import ISA.Labels.Label;
import ISA.Visitors.ISAVisitor;

import java.util.List;
import java.util.Map;

public class LabelScanningVisitor implements ISAVisitor {
    private Map<Label, Integer> labelIndex;
    private int index;

    public Map<Label, Integer> buildLabelIndex(List<InstructionNode> instructions) {
        labelIndex.clear();
        for (index = 0; index < instructions.size(); index++) {
            instructions.get(index).accept(this);
        }
        return labelIndex;
    }

    @Override
    public void visitAllInstructions(List<InstructionNode> instructions) {
    }

    @Override
    public void visit(LabelNode labelNode) {
        labelIndex.put(labelNode.getLabel(), index);
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

    @Override
    public void visit(LoadLiteralIns loadLiteralIns) {

    }

    @Override
    public void visit(BinaryToStochasticIns binaryToStochasticIns) {

    }

    @Override
    public void visit(StochasticToBinaryIns stochasticToBinaryIns) {

    }
}
