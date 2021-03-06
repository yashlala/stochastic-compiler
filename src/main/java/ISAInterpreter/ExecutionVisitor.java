package ISAInterpreter;

import ISA.InstructionNodes.*;
import ISA.Labels.Label;
import ISA.Visitors.ISAVisitor;
import ISAInterpreter.Registers.BinaryRegister;
import ISAInterpreter.Registers.Register;
import ISAInterpreter.Registers.StochasticRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Although we use the "visitor" pattern here, this isn't a true visitor
// in the sense of top-down recursion. To support jump instructions in a
// non-recursive manner (no stack overflows), we explicitly make a FSM.
public class ExecutionVisitor implements ISAVisitor {
    private final RegisterFile regFile = new RegisterFile();
    private final MemoryBank memoryBank = new MemoryBank();
    private Map<Label, Integer> labelMap = new HashMap<>();
    private final List<String> consoleOutput = new ArrayList<>();
    private int programCounter;
    private double noiseCoefficient;


    public List<String> executeProgram(List<InstructionNode> instructions,
                                       Map<Label, Integer> labelIndex, double noiseCoefficient) {
        regFile.clear();
        memoryBank.clear();
        consoleOutput.clear();
        labelMap = new HashMap<>(labelIndex);
        this.noiseCoefficient = noiseCoefficient;
        programCounter = 0;


        while (programCounter != instructions.size()) {
            if (programCounter < 0 || programCounter > instructions.size()) {
                // TODO: Create a proper set of exceptions. Preferably with PC counts.
                throw new RuntimeException("Invalid PC address when executing program");
            }

            // Evaluate the next instruction.
            instructions.get(programCounter).accept(this);
            programCounter++;
        }

        return consoleOutput;
    }

    @Override
    public void visitAllInstructions(List<InstructionNode> instructions) {
        // Should this really be in the interface?
    }

    @Override
    public void visit(BinaryAdd binaryAdd) {
        BinaryRegister src1 = regFile.getBinaryReg(binaryAdd.getSrc1());
        BinaryRegister src2 = regFile.getBinaryReg(binaryAdd.getSrc2());
        String destName = binaryAdd.getDest().getName();
        double destValue = src1.getValue() + src2.getValue();
        regFile.putReg(new BinaryRegister(destName, destValue));
    }

    @Override
    public void visit(BinarySub binarySub) {
        BinaryRegister src1 = regFile.getBinaryReg(binarySub.getSrc1());
        BinaryRegister src2 = regFile.getBinaryReg(binarySub.getSrc2());
        String destName = binarySub.getDest().getName();
        double destValue = src1.getValue() - src2.getValue();
        regFile.putReg(new BinaryRegister(destName, destValue));
    }

    @Override
    public void visit(BinaryMul binaryMul) {
        BinaryRegister src1 = regFile.getBinaryReg(binaryMul.getSrc1());
        BinaryRegister src2 = regFile.getBinaryReg(binaryMul.getSrc2());
        String destName = binaryMul.getDest().getName();
        double destValue = src1.getValue() * src2.getValue();
        regFile.putReg(new BinaryRegister(destName, destValue));
    }

    @Override
    public void visit(BinaryDiv binaryDiv) {
        BinaryRegister src1 = regFile.getBinaryReg(binaryDiv.getSrc1());
        BinaryRegister src2 = regFile.getBinaryReg(binaryDiv.getSrc2());
        String destName = binaryDiv.getDest().getName();
        double destValue = src1.getValue() / src2.getValue();
        regFile.putReg(new BinaryRegister(destName, destValue));
    }

    @Override
    public void visit(LessThan lessThan) {
        Register lhs = regFile.getReg(lessThan.getSrc1());
        Register rhs = regFile.getReg(lessThan.getSrc2());
        String destName = lessThan.getDest().getName();
        int destValue = lhs.toDouble() < rhs.toDouble() ? 1 : 0;
        regFile.putReg(new BinaryRegister(destName, destValue));
    }

    @Override
    public void visit(BinaryEq binaryEq) {
        BinaryRegister src1 = regFile.getBinaryReg(binaryEq.getSrc1());
        BinaryRegister src2 = regFile.getBinaryReg(binaryEq.getSrc2());
        String destName = binaryEq.getDest().getName();
        int destValue = src1.getValue() == src2.getValue() ? 1 : 0;
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
        StochasticRegister src1 = regFile.getStochasticReg(stochasticAdd.getSrc1());
        StochasticRegister src2 = regFile.getStochasticReg(stochasticAdd.getSrc2());
        Register scale = regFile.getReg(stochasticAdd.getScale());
        StochasticRegister dest = new StochasticRegister(stochasticAdd.getDest());
        StochasticRegister.add(dest, src1, src2, scale, noiseCoefficient);
        regFile.putReg(dest);
    }

    @Override
    public void visit(StochasticSub stochasticSub) {
        StochasticRegister src1 = regFile.getStochasticReg(stochasticSub.getSrc1());
        StochasticRegister src2 = regFile.getStochasticReg(stochasticSub.getSrc2());
        Register scale = regFile.getReg(stochasticSub.getScale());
        StochasticRegister dest = new StochasticRegister(stochasticSub.getDest());
        StochasticRegister.subtract(dest, src1, src2, scale, noiseCoefficient);
        regFile.putReg(dest);
    }

    @Override
    public void visit(StochasticMul stochasticMul) {
        StochasticRegister src1 = regFile.getStochasticReg(stochasticMul.getSrc1());
        StochasticRegister src2 = regFile.getStochasticReg(stochasticMul.getSrc2());
        StochasticRegister dest = new StochasticRegister(stochasticMul.getDest());
        StochasticRegister.multiply(dest, src1, src2, noiseCoefficient);
        regFile.putReg(dest);
    }

    @Override
    public void visit(StochasticDiv stochasticDiv) {
        StochasticRegister src1 = regFile.getStochasticReg(stochasticDiv.getSrc1());
        StochasticRegister src2 = regFile.getStochasticReg(stochasticDiv.getSrc2());
        Register scale = regFile.getReg(stochasticDiv.getScale());
        StochasticRegister dest = new StochasticRegister(stochasticDiv.getDest());
//        System.out.println("\n\n\n offending instructions \n\n");
//        System.out.println(dest);
//        System.out.println(src1);
//        System.out.println(src2);
//        System.out.println("\n\n\n scale \n\n");

        System.out.println(scale);
        StochasticRegister.divide(dest, src1, src2, scale, noiseCoefficient);
//        System.out.println("\n\n\n no problem \n\n");
        regFile.putReg(dest);
    }

    @Override
    public void visit(StochasticExp stochasticExp) {
        StochasticRegister src = regFile.getStochasticReg(stochasticExp.getSrc1());
        StochasticRegister dest = new StochasticRegister(stochasticExp.getDest());
        StochasticRegister.exp(dest, src, noiseCoefficient);
        regFile.putReg(dest);
    }

    @Override
    public void visit(StochasticTanh stochasticTanh) {
        StochasticRegister src = regFile.getStochasticReg(stochasticTanh.getSrc1());
        StochasticRegister dest = new StochasticRegister(stochasticTanh.getDest());
        StochasticRegister.tanh(dest, src, noiseCoefficient);
        regFile.putReg(dest);
    }

    @Override
    public void visit(PrintIns printIns) {
        consoleOutput.add(regFile.getReg(printIns.getRegister()).toString());
    }

    @Override
    public void visit(LoadIns loadIns) {
        BinaryRegister register = new BinaryRegister(loadIns.getRegister());
        BinaryRegister address = regFile.getBinaryReg(loadIns.getAddress());
        memoryBank.load((int) Math.round(address.toDouble()), register);
        regFile.putReg(register);
    }

    @Override
    public void visit(StoreIns storeIns) {
        BinaryRegister register = regFile.getBinaryReg(storeIns.getRegister());
        BinaryRegister address = regFile.getBinaryReg(storeIns.getAddress());
        memoryBank.store((int) Math.round(address.toDouble()), register);
    }

    @Override
    public void visit(LoadLiteralIns loadLiteralIns) {
        Register register = toNativeRegister(loadLiteralIns.getRegister());
        register.fromDouble(loadLiteralIns.getValue().getValue());
        regFile.putReg(register);
    }

    @Override
    public void visit(BinaryToStochasticIns binaryToStochasticIns) {
        BinaryRegister src = regFile.getBinaryReg(binaryToStochasticIns.getSrc());
        StochasticRegister dest = new StochasticRegister(binaryToStochasticIns.getDest());
        dest.fromDouble(src.toDouble());
        regFile.putReg(dest);
    }

    @Override
    public void visit(StochasticToBinaryIns stochasticToBinaryIns) {
        StochasticRegister src = regFile.getStochasticReg(stochasticToBinaryIns.getSrc());
        BinaryRegister dest = new BinaryRegister(stochasticToBinaryIns.getDest());
        dest.fromDouble(src.toDouble());
        regFile.putReg(dest);
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
