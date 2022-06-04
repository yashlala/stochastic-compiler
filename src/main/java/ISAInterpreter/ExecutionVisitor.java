package ISAInterpreter;

import ISA.InstructionNodes.*;
import ISA.Labels.Label;
import ISA.Visitors.ISAVisitor;
import ISAInterpreter.Registers.BinaryRegister;
import ISAInterpreter.Registers.Register;
import ISAInterpreter.Registers.StochasticRegister;
import lombok.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Although we use the "visitor" pattern here, this isn't a true visitor
// in the sense of top down recursion. To support jump instructions in a
// non-recursive manner (no stack overflows), we explicitly make a FSM.
public class ExecutionVisitor implements ISAVisitor {
    private final RegisterFile regFile = new RegisterFile();
    private final MemoryBank memoryBank = new MemoryBank();
    private Map<Label, Integer> labelMap = new HashMap<>();
    private int programCounter;


    public void executeProgram(List<InstructionNode> instructions, Map<Label, Integer> labelIndex) {
        regFile.clear();
        memoryBank.clear();
        labelMap = new HashMap<>(labelIndex);
        programCounter = 0;

        while (programCounter < instructions.size()) {
            if (programCounter < 0) {
                // TODO: Create a proper set of exceptions
                throw new RuntimeException("Invalid PC address when executing program");
            }

            // Evaluate the next instruction.
            instructions.get(programCounter).accept(this);
        }
    }

    // TODO: Probably should replace this w/ stub method or remove from visitor interface
    // entirely eh
    @Override
    public void visitAllInstructions(List<InstructionNode> instructions) {

    }

    @Override
    public void visit(BinaryAdd binaryAdd) {
        BinaryRegister src1 = regFile.getBinaryReg(binaryAdd.getSrc1());
        BinaryRegister src2 = regFile.getBinaryReg(binaryAdd.getSrc2());
        String destName = binaryAdd.getDest().getName();
        int destValue = src1.getValue() + src2.getValue();
        regFile.putReg(new BinaryRegister(destName, destValue));
    }

    @Override
    public void visit(BinarySub binarySub) {
        BinaryRegister src1 = regFile.getBinaryReg(binarySub.getSrc1());
        BinaryRegister src2 = regFile.getBinaryReg(binarySub.getSrc2());
        String destName = binarySub.getDest().getName();
        int destValue = src1.getValue() - src2.getValue();
        regFile.putReg(new BinaryRegister(destName, destValue));
    }

    @Override
    public void visit(BinaryMul binaryMul) {
        BinaryRegister src1 = regFile.getBinaryReg(binaryMul.getSrc1());
        BinaryRegister src2 = regFile.getBinaryReg(binaryMul.getSrc2());
        String destName = binaryMul.getDest().getName();
        int destValue = src1.getValue() * src2.getValue();
        regFile.putReg(new BinaryRegister(destName, destValue));
    }

    @Override
    public void visit(BinaryDiv binaryDiv) {
        BinaryRegister src1 = regFile.getBinaryReg(binaryDiv.getSrc1());
        BinaryRegister src2 = regFile.getBinaryReg(binaryDiv.getSrc2());
        String destName = binaryDiv.getDest().getName();
        int destValue = src1.getValue() / src2.getValue();
        regFile.putReg(new BinaryRegister(destName, destValue));
    }

    @Override
    public void visit(LessThan lessThan) {
        // TODO: This needs to take both
    }

    @Override
    public void visit(BinaryEq binaryEq) {
        BinaryRegister src1 = regFile.getBinaryReg(binaryEq.getSrc1());
        BinaryRegister src2 = regFile.getBinaryReg(binaryEq.getSrc2());
        String destName = binaryEq.getDest().getName();
        int destValue;
        if (src1.getValue() == src2.getValue()) {
            destValue = 1;
        } else {
            destValue = 0;
        }
        regFile.putReg(new BinaryRegister(destName, destValue));
    }

    @Override
    public void visit(LabelNode labelNode) {
    }

    @Override
    public void visit(BinaryJz binaryJz) {
        BinaryRegister conditionReg = regFile.getBinaryReg(binaryJz.getConditionReg());
        if (conditionReg.getValue() == 0) {
            // The PC is incremented after every instruction, so when we jump we should
            // set it to one _before_ the target label.
            this.programCounter = labelMap.get(binaryJz.getLabel()) - 1;
        }
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
        Register register = toNativeRegister(loadIns.getRegister());
        memoryBank.load(loadIns.getAddress(), register);
        regFile.putReg(register);
    }

    @Override
    public void visit(StoreIns storeIns) {
        Register register = regFile.getReg(storeIns.getRegister().getName());
        memoryBank.store(storeIns.getAddress(), register);
    }

    @Override
    public void visit(LoadLiteralIns loadLiteralIns) {
        ISA.Registers.@NonNull Register regName = loadLiteralIns.getRegister();
        // TODO WTF
    }

    @Override
    public void visit(BinaryToStochasticIns binaryToStochasticIns) {

    }

    @Override
    public void visit(StochasticToBinaryIns stochasticToBinaryIns) {

    }

    // Convert the ISA language's register types to our local ISAInterpreter's native registers.
    // All new ISA register types can register a conversion mechanism here.
    private Register toNativeRegister(ISA.Registers.Register register) {
        if (register instanceof ISA.Registers.BinaryRegister) {
            return new BinaryRegister((ISA.Registers.BinaryRegister) register);
        } else if (register instanceof ISA.Registers.StochasticRegister) {
            return new StochasticRegister((ISA.Registers.StochasticRegister) register);
        } else {
            throw new RuntimeException("Unknown Register type encountered");
        }
    }
}
