package IR.Visitors;

import IR.IRNodes.LabelNode;
import IR.IRNodes.LessThan;
import IR.IRNodes.*;
import IR.Variables.Variable;
import ISA.InstructionNodes.*;
import ISA.Labels.Label;
import ISA.Literals.Literal;
import ISA.Registers.BinaryRegister;
import ISA.Registers.Register;
import ISA.Registers.RegisterPolarity;
import ISA.Registers.StochasticRegister;
import com.google.common.collect.ImmutableSet;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CompilerVisitor implements IRReturnVisitor<List<InstructionNode>> {
    private final BinaryRegister zeroReg = new BinaryRegister("zero");
    private final Variable addS = new Variable("$addScale");
    private final Variable scaleFactorVariable = new Variable("$scaleFactor");
    private boolean scaleFactorInitialized = false;
    private boolean addSInitialized = false;

    @NonNull
    private final ImmutableSet<Variable> stochasticVariables;
    private final int bitstreamWidth;
    private final RegisterPolarity polarity;
    private final int scaleFactor;

    //two key sets are mutually exclusive
    private final Map<Variable, BinaryRegister> binaryRegisterMap = new HashMap<>();
    private final Map<Variable, StochasticRegister> stochasticRegisterMap = new HashMap<>();
    private int counter = 0;

    private String getNextRegisterName() {
        return "t" + counter++;
    }

    private String getNextLabelName() {
        return "L" + counter++;
    }

    private BinaryRegister getBinaryRegisterForVar(Variable variable) {
        if (binaryRegisterMap.containsKey(variable)) {
            return binaryRegisterMap.get(variable);
        } else {
            BinaryRegister nextReg = new BinaryRegister(getNextRegisterName());
            binaryRegisterMap.put(variable, nextReg);
            return nextReg;
        }
    }

    private StochasticRegister getStochasticRegisterForVar(Variable variable) {
        if (stochasticRegisterMap.containsKey(variable)) {
            return stochasticRegisterMap.get(variable);
        } else {
            StochasticRegister nextReg = new StochasticRegister(getNextRegisterName(), polarity, bitstreamWidth);
            stochasticRegisterMap.put(variable, nextReg);
            return nextReg;
        }
    }

    @Override
    public List<InstructionNode> visitAllInstructions(List<IRNode> instructions) {
        List<InstructionNode> allIns = new LinkedList<>();
        for (IRNode instruction : instructions) {
            allIns.addAll(instruction.accept(this));
        }
        return allIns;
    }

    @Override
    public List<InstructionNode> visit(ForLoop forLoop) {
        List<InstructionNode> forLoopIns = new LinkedList<>();
        //invariant: loop condition must be binary, new, and unused in loop body
        BinaryRegister loopVar = getBinaryRegisterForVar(forLoop.getLoopIterVar());
        BinaryRegister loopStart = getBinaryRegisterForVar(forLoop.getLoopRangeStart());
        //init reg for one
        BinaryRegister one = new BinaryRegister(getNextRegisterName());
        forLoopIns.add(new LoadLiteralIns(one, new Literal(1)));
        //init loop var
        forLoopIns.add(new BinaryAdd(loopVar, loopStart, zeroReg));

        Label loopStartLabel = new Label(getNextLabelName());
        Label loopEndLabel = new Label(getNextLabelName());
        forLoopIns.add(new ISA.InstructionNodes.LabelNode(loopStartLabel));

        //less than check
        BinaryRegister loopEnd = getBinaryRegisterForVar(forLoop.getLoopRangeEnd());
        BinaryRegister tempCondCheck = new BinaryRegister(getNextRegisterName());
        forLoopIns.add(new ISA.InstructionNodes.LessThan(tempCondCheck, loopVar, loopEnd));
        //jump if not less than
        forLoopIns.add(new BinaryJz(loopEndLabel, tempCondCheck));

        //loop body
        forLoop.getContents().forEach(irNode -> forLoopIns.addAll(irNode.accept(this)));

        //increment loop var
        forLoopIns.add(new BinaryAdd(loopVar, loopVar, one));
        //jump to loop start
        forLoopIns.add(new BinaryJz(loopStartLabel, zeroReg));
        //end label
        forLoopIns.add(new ISA.InstructionNodes.LabelNode(loopEndLabel));
        return forLoopIns;
    }

    @Override
    public List<InstructionNode> visit(Add add) {
        List<InstructionNode> ISAadd = new LinkedList<>();
        boolean stochDest = stochasticVariables.contains(add.getDest());
        boolean stocharg1 = stochasticVariables.contains(add.getSrc1());
        boolean stocharg2 = stochasticVariables.contains(add.getSrc2());

        if (!(stochDest || stocharg1 || stocharg2)) {
            //if all arguments are binary, perform binary addition
            BinaryRegister dest = getBinaryRegisterForVar(add.getDest());
            BinaryRegister src1 = getBinaryRegisterForVar(add.getSrc1());
            BinaryRegister src2 = getBinaryRegisterForVar(add.getSrc2());
            ISAadd.add(new BinaryAdd(dest, src1, src2));
        } else if (stochDest && stocharg1 && stocharg2) {
            //if all arguments are stochastic, perform stochastic addition
            StochasticRegister dest = getStochasticRegisterForVar(add.getDest());
            StochasticRegister src1 = getStochasticRegisterForVar(add.getSrc1());
            StochasticRegister src2 = getStochasticRegisterForVar(add.getSrc2());
            //TODO: rescale by 2
            BinaryRegister addSReg = getBinaryRegisterForVar(addS);
            initAddSIfNeeded(ISAadd);
            ISAadd.add(new StochasticAdd(dest, src1, src2, addSReg));
        } else if (stocharg1 == stocharg2) {
            //if srcs agree on type, perform relevant operation then cast to dest type
            Register tempDest;
            if (stocharg1) {
                StochasticRegister src1 = getStochasticRegisterForVar(add.getSrc1());
                StochasticRegister src2 = getStochasticRegisterForVar(add.getSrc1());
                tempDest = new StochasticRegister(getNextRegisterName(), polarity, bitstreamWidth);
                //TODO: rescale by 2
                BinaryRegister addSReg = getBinaryRegisterForVar(addS);
                initAddSIfNeeded(ISAadd);
                ISAadd.add(new StochasticAdd((StochasticRegister) tempDest, src1, src2, addSReg));
            } else {
                BinaryRegister src1 = getBinaryRegisterForVar(add.getSrc1());
                BinaryRegister src2 = getBinaryRegisterForVar(add.getSrc1());
                tempDest = new BinaryRegister(getNextRegisterName());
                ISAadd.add(new BinaryAdd((BinaryRegister) tempDest, src1, src2));
            }
            //invariant, dest reg is opposite of tempDest
            if (stochDest) {
                StochasticRegister dest = getStochasticRegisterForVar(add.getDest());
                ISAadd.addAll(convertBinarytoStochastic((BinaryRegister) tempDest, dest));
            } else {
                BinaryRegister dest = getBinaryRegisterForVar(add.getDest());
                ISAadd.addAll(convertStochasticToBinary((StochasticRegister) tempDest, dest));
            }
        } else {
            //src disagree on type, so convert to dest type
            if (stochDest) {
                StochasticRegister dest = getStochasticRegisterForVar(add.getDest());
                StochasticRegister src1;
                StochasticRegister src2;
                if (stocharg1) {
                    src1 = getStochasticRegisterForVar(add.getSrc1());
                    //guaranteed src2 is binary
                    src2 = new StochasticRegister(getNextRegisterName(), polarity, bitstreamWidth);
                    BinaryRegister tempSrc2 = getBinaryRegisterForVar(add.getSrc2());
                    ISAadd.addAll(convertBinarytoStochastic(tempSrc2, src2));
                } else {
                    src2 = getStochasticRegisterForVar(add.getSrc2());
                    //guaranteed src1 is binary
                    src1 = new StochasticRegister(getNextRegisterName(), polarity, bitstreamWidth);
                    BinaryRegister tempSrc1 = getBinaryRegisterForVar(add.getSrc1());
                    ISAadd.addAll(convertBinarytoStochastic(tempSrc1, src1));
                }
                //TODO: rescale by 2
                BinaryRegister addSReg = getBinaryRegisterForVar(addS);
                initAddSIfNeeded(ISAadd);
                ISAadd.add(new StochasticAdd(dest, src1, src2, addSReg));
            } else {
                BinaryRegister dest = getBinaryRegisterForVar(add.getDest());
                BinaryRegister src1;
                BinaryRegister src2;
                if (!stocharg1) {
                    src1 = getBinaryRegisterForVar(add.getSrc1());
                    //guaranteed src2 is stoch
                    src2 = new BinaryRegister(getNextRegisterName());
                    StochasticRegister tempSrc2 = getStochasticRegisterForVar(add.getSrc2());
                    ISAadd.addAll(convertStochasticToBinary(tempSrc2, src2));
                } else {
                    src2 = getBinaryRegisterForVar(add.getSrc2());
                    //guaranteed src1 is stoch
                    src1 = new BinaryRegister(getNextRegisterName());
                    StochasticRegister tempSrc1 = getStochasticRegisterForVar(add.getSrc1());
                    ISAadd.addAll(convertStochasticToBinary(tempSrc1, src1));
                }
                ISAadd.add(new BinaryAdd(dest, src1, src2));
            }
        }
        return ISAadd;
    }

    private void initAddSIfNeeded(List<InstructionNode> ins) {
        if (!addSInitialized) {
            //creates mapping for scaleFactorReg
            BinaryRegister scaleFactorReg = getBinaryRegisterForVar(addS);
            addSInitialized = true;
            ins.add(new LoadLiteralIns(scaleFactorReg, new Literal(0.5)));
        }
    }

    private void initScaleFactorIfNeeded(List<InstructionNode> ins) {
        if (!scaleFactorInitialized) {
            BinaryRegister scaleFactorReg = getBinaryRegisterForVar(scaleFactorVariable);
            scaleFactorInitialized = true;
            ins.add(new LoadLiteralIns(scaleFactorReg, new Literal(scaleFactor)));
        }
    }

    private List<InstructionNode> convertBinarytoStochastic(BinaryRegister src, StochasticRegister converted) {
        List<InstructionNode> conversionIns = new LinkedList<>();
        BinaryRegister scaleFactorReg = getBinaryRegisterForVar(scaleFactorVariable);
        initScaleFactorIfNeeded(conversionIns);
        //scale down the binary value into -1,1 range
        BinaryRegister scaledDownBin = new BinaryRegister(getNextRegisterName());
        conversionIns.add(new BinaryDiv(scaledDownBin, src, scaleFactorReg));
        //convert to stochastic register
        conversionIns.add(new BinaryToStochasticIns(converted, src));
        return conversionIns;
    }

    private List<InstructionNode> convertStochasticToBinary(StochasticRegister src, BinaryRegister converted) {
        List<InstructionNode> conversionIns = new LinkedList<>();
        BinaryRegister tempScaledDownBin = new BinaryRegister(getNextRegisterName());
        conversionIns.add(new StochasticToBinaryIns(tempScaledDownBin, src));
        //upscales back to binary values range -f, f
        BinaryRegister scaleFactorReg = getBinaryRegisterForVar(scaleFactorVariable);
        conversionIns.add(new BinaryMul(converted, tempScaledDownBin, scaleFactorReg));
        return conversionIns;
    }

    @Override
    public List<InstructionNode> visit(Subtract subtract) {
        return null;
    }

    @Override
    public List<InstructionNode> visit(Multiply multiply) {
        return null;
    }

    @Override
    public List<InstructionNode> visit(Divide divide) {
        return null;
    }

    @Override
    public List<InstructionNode> visit(Equals equals) {
        return null;
    }

    @Override
    public List<InstructionNode> visit(LessThan lessThan) {
        return null;
    }

    @Override
    public List<InstructionNode> visit(IfZero ifZero) {
        return null;
    }

    @Override
    public List<InstructionNode> visit(LabelNode labelNode) {
        List<InstructionNode> ISAins = new LinkedList<>();
        ISAins.add(new ISA.InstructionNodes.LabelNode(new Label(labelNode.getLabel().getName())));
        return ISAins;
    }

    @Override
    public List<InstructionNode> visit(IfNotEquals ifNotEquals) {
        return null;
    }

    @Override
    public List<InstructionNode> visit(Goto aGoto) {
        List<InstructionNode> ISAgoto = new LinkedList<>();
        ISAgoto.add(new BinaryJz(new Label(aGoto.getLabel().getName()), zeroReg));
        return ISAgoto;
    }

    @Override
    public List<InstructionNode> visit(Print print) {
        List<InstructionNode> ISAprint = new LinkedList<>();
        Register toPrint;
        if (stochasticVariables.contains(print.getVar())) {
            toPrint = getStochasticRegisterForVar(print.getVar());
        } else {
            toPrint = getBinaryRegisterForVar(print.getVar());
        }
        ISAprint.add(new PrintIns(toPrint));
        return ISAprint;
    }

    @Override
    public List<InstructionNode> visit(Load load) {
        return null;
    }

    @Override
    public List<InstructionNode> visit(Store store) {
        return null;
    }

    @Override
    public List<InstructionNode> visit(SetLiteral setLiteral) {
        return null;
    }
}
