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

import java.util.*;

@RequiredArgsConstructor
public class CompilerVisitor implements IRReturnVisitor<List<InstructionNode>> {
    private final BinaryRegister zeroReg = new BinaryRegister("zero");
    private final Variable addS = new Variable("$addScale");
    private final Variable scaleFactorVariable = new Variable("$scaleFactor");
    private final Variable inverseScaleFactorVariable = new Variable("$inverseScaleFactor");
    private final Variable additionUpscaleVariable = new Variable("$additionUpscale");
    private final Variable stochasticOneVariable = new Variable("$one");
    private boolean scaleFactorInitialized = false;
    private boolean addSInitialized = false;
    private boolean additionUpscaleInitialized = false;
    private boolean inverseScaleInitialized = false;
    private boolean stochasticOneInitialized = false;

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
            ISAadd.addAll(performStochasticAddition(dest, src1, src2));
        } else if (stocharg1 == stocharg2) {
            //if srcs agree on type, perform relevant operation then cast to dest type
            Register tempDest;
            if (stocharg1) {
                StochasticRegister src1 = getStochasticRegisterForVar(add.getSrc1());
                StochasticRegister src2 = getStochasticRegisterForVar(add.getSrc2());
                tempDest = new StochasticRegister(getNextRegisterName(), polarity, bitstreamWidth);
                ISAadd.addAll(performStochasticAddition((StochasticRegister) tempDest, src1, src2));
            } else {
                BinaryRegister src1 = getBinaryRegisterForVar(add.getSrc1());
                BinaryRegister src2 = getBinaryRegisterForVar(add.getSrc2());
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
                ISAadd.addAll(performStochasticAddition(dest, src1, src2));
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

    @Override
    public List<InstructionNode> visit(Subtract subtract) {
        List<InstructionNode> subIns = new LinkedList<>();
        boolean stochDest = stochasticVariables.contains(subtract.getDest());
        boolean stocharg1 = stochasticVariables.contains(subtract.getSrc1());
        boolean stocharg2 = stochasticVariables.contains(subtract.getSrc2());

        if (!(stochDest || stocharg1 || stocharg2)) {
            //if all arguments are binary, perform binary subtraction
            BinaryRegister dest = getBinaryRegisterForVar(subtract.getDest());
            BinaryRegister src1 = getBinaryRegisterForVar(subtract.getSrc1());
            BinaryRegister src2 = getBinaryRegisterForVar(subtract.getSrc2());
            subIns.add(new BinarySub(dest, src1, src2));
        } else if (stochDest && stocharg1 && stocharg2) {
            //if all arguments are stochastic, perform stochastic subtraction
            StochasticRegister dest = getStochasticRegisterForVar(subtract.getDest());
            StochasticRegister src1 = getStochasticRegisterForVar(subtract.getSrc1());
            StochasticRegister src2 = getStochasticRegisterForVar(subtract.getSrc2());
            subIns.addAll(performStochasticSubtraction(dest, src1, src2));
        } else if (stocharg1 == stocharg2) {
            //if srcs agree on type, perform relevant operation then cast to dest type
            Register tempDest;
            if (stocharg1) {
                StochasticRegister src1 = getStochasticRegisterForVar(subtract.getSrc1());
                StochasticRegister src2 = getStochasticRegisterForVar(subtract.getSrc2());
                tempDest = new StochasticRegister(getNextRegisterName(), polarity, bitstreamWidth);
                subIns.addAll(performStochasticSubtraction((StochasticRegister) tempDest, src1, src2));
            } else {
                BinaryRegister src1 = getBinaryRegisterForVar(subtract.getSrc1());
                BinaryRegister src2 = getBinaryRegisterForVar(subtract.getSrc2());
                tempDest = new BinaryRegister(getNextRegisterName());
                subIns.add(new BinarySub((BinaryRegister) tempDest, src1, src2));
            }
            //invariant, dest reg is opposite of tempDest
            if (stochDest) {
                StochasticRegister dest = getStochasticRegisterForVar(subtract.getDest());
                subIns.addAll(convertBinarytoStochastic((BinaryRegister) tempDest, dest));
            } else {
                BinaryRegister dest = getBinaryRegisterForVar(subtract.getDest());
                subIns.addAll(convertStochasticToBinary((StochasticRegister) tempDest, dest));
            }
        } else {
            //src disagree on type, so convert to dest type
            if (stochDest) {
                StochasticRegister dest = getStochasticRegisterForVar(subtract.getDest());
                StochasticRegister src1;
                StochasticRegister src2;
                if (stocharg1) {
                    src1 = getStochasticRegisterForVar(subtract.getSrc1());
                    //guaranteed src2 is binary
                    src2 = new StochasticRegister(getNextRegisterName(), polarity, bitstreamWidth);
                    BinaryRegister tempSrc2 = getBinaryRegisterForVar(subtract.getSrc2());
                    subIns.addAll(convertBinarytoStochastic(tempSrc2, src2));
                } else {
                    src2 = getStochasticRegisterForVar(subtract.getSrc2());
                    //guaranteed src1 is binary
                    src1 = new StochasticRegister(getNextRegisterName(), polarity, bitstreamWidth);
                    BinaryRegister tempSrc1 = getBinaryRegisterForVar(subtract.getSrc1());
                    subIns.addAll(convertBinarytoStochastic(tempSrc1, src1));
                }
                subIns.addAll(performStochasticSubtraction(dest, src1, src2));
            } else {
                BinaryRegister dest = getBinaryRegisterForVar(subtract.getDest());
                BinaryRegister src1;
                BinaryRegister src2;
                if (!stocharg1) {
                    src1 = getBinaryRegisterForVar(subtract.getSrc1());
                    //guaranteed src2 is stoch
                    src2 = new BinaryRegister(getNextRegisterName());
                    StochasticRegister tempSrc2 = getStochasticRegisterForVar(subtract.getSrc2());
                    subIns.addAll(convertStochasticToBinary(tempSrc2, src2));
                } else {
                    src2 = getBinaryRegisterForVar(subtract.getSrc2());
                    //guaranteed src1 is stoch
                    src1 = new BinaryRegister(getNextRegisterName());
                    StochasticRegister tempSrc1 = getStochasticRegisterForVar(subtract.getSrc1());
                    subIns.addAll(convertStochasticToBinary(tempSrc1, src1));
                }
                subIns.add(new BinarySub(dest, src1, src2));
            }
        }
        return subIns;
    }

    @Override
    public List<InstructionNode> visit(Multiply multiply) {
        List<InstructionNode> mulIns = new LinkedList<>();
        boolean stochDest = stochasticVariables.contains(multiply.getDest());
        boolean stocharg1 = stochasticVariables.contains(multiply.getSrc1());
        boolean stocharg2 = stochasticVariables.contains(multiply.getSrc2());

        if (!(stochDest || stocharg1 || stocharg2)) {
            //if all arguments are binary, perform binary subtraction
            BinaryRegister dest = getBinaryRegisterForVar(multiply.getDest());
            BinaryRegister src1 = getBinaryRegisterForVar(multiply.getSrc1());
            BinaryRegister src2 = getBinaryRegisterForVar(multiply.getSrc2());
            mulIns.add(new BinaryMul(dest, src1, src2));
        } else if (stochDest && stocharg1 && stocharg2) {
            //if all arguments are stochastic, perform stochastic subtraction
            StochasticRegister dest = getStochasticRegisterForVar(multiply.getDest());
            StochasticRegister src1 = getStochasticRegisterForVar(multiply.getSrc1());
            StochasticRegister src2 = getStochasticRegisterForVar(multiply.getSrc2());
            mulIns.addAll(performStochasticMultiplication(dest, src1, src2));
        } else if (stocharg1 == stocharg2) {
            //if srcs agree on type, perform relevant operation then cast to dest type
            Register tempDest;
            if (stocharg1) {
                StochasticRegister src1 = getStochasticRegisterForVar(multiply.getSrc1());
                StochasticRegister src2 = getStochasticRegisterForVar(multiply.getSrc2());
                tempDest = new StochasticRegister(getNextRegisterName(), polarity, bitstreamWidth);
                mulIns.addAll(performStochasticMultiplication((StochasticRegister) tempDest, src1, src2));
            } else {
                BinaryRegister src1 = getBinaryRegisterForVar(multiply.getSrc1());
                BinaryRegister src2 = getBinaryRegisterForVar(multiply.getSrc2());
                tempDest = new BinaryRegister(getNextRegisterName());
                mulIns.add(new BinaryMul((BinaryRegister) tempDest, src1, src2));
            }
            //invariant, dest reg is opposite of tempDest
            if (stochDest) {
                StochasticRegister dest = getStochasticRegisterForVar(multiply.getDest());
                mulIns.addAll(convertBinarytoStochastic((BinaryRegister) tempDest, dest));
            } else {
                BinaryRegister dest = getBinaryRegisterForVar(multiply.getDest());
                mulIns.addAll(convertStochasticToBinary((StochasticRegister) tempDest, dest));
            }
        } else {
            //src disagree on type, so convert to dest type
            if (stochDest) {
                StochasticRegister dest = getStochasticRegisterForVar(multiply.getDest());
                StochasticRegister src1;
                StochasticRegister src2;
                if (stocharg1) {
                    src1 = getStochasticRegisterForVar(multiply.getSrc1());
                    //guaranteed src2 is binary
                    src2 = new StochasticRegister(getNextRegisterName(), polarity, bitstreamWidth);
                    BinaryRegister tempSrc2 = getBinaryRegisterForVar(multiply.getSrc2());
                    mulIns.addAll(convertBinarytoStochastic(tempSrc2, src2));
                } else {
                    src2 = getStochasticRegisterForVar(multiply.getSrc2());
                    //guaranteed src1 is binary
                    src1 = new StochasticRegister(getNextRegisterName(), polarity, bitstreamWidth);
                    BinaryRegister tempSrc1 = getBinaryRegisterForVar(multiply.getSrc1());
                    mulIns.addAll(convertBinarytoStochastic(tempSrc1, src1));
                }
                mulIns.addAll(performStochasticMultiplication(dest, src1, src2));
            } else {
                BinaryRegister dest = getBinaryRegisterForVar(multiply.getDest());
                BinaryRegister src1;
                BinaryRegister src2;
                if (!stocharg1) {
                    src1 = getBinaryRegisterForVar(multiply.getSrc1());
                    //guaranteed src2 is stoch
                    src2 = new BinaryRegister(getNextRegisterName());
                    StochasticRegister tempSrc2 = getStochasticRegisterForVar(multiply.getSrc2());
                    mulIns.addAll(convertStochasticToBinary(tempSrc2, src2));
                } else {
                    src2 = getBinaryRegisterForVar(multiply.getSrc2());
                    //guaranteed src1 is stoch
                    src1 = new BinaryRegister(getNextRegisterName());
                    StochasticRegister tempSrc1 = getStochasticRegisterForVar(multiply.getSrc1());
                    mulIns.addAll(convertStochasticToBinary(tempSrc1, src1));
                }
                mulIns.add(new BinaryMul(dest, src1, src2));
            }
        }
        return mulIns;
    }

    @Override
    public List<InstructionNode> visit(Divide divide) {
        List<InstructionNode> divIns = new LinkedList<>();
        boolean stochDest = stochasticVariables.contains(divide.getDest());
        boolean stocharg1 = stochasticVariables.contains(divide.getSrc1());
        boolean stocharg2 = stochasticVariables.contains(divide.getSrc2());

        if (!(stochDest || stocharg1 || stocharg2)) {
            //if all arguments are binary, perform binary subtraction
            BinaryRegister dest = getBinaryRegisterForVar(divide.getDest());
            BinaryRegister src1 = getBinaryRegisterForVar(divide.getSrc1());
            BinaryRegister src2 = getBinaryRegisterForVar(divide.getSrc2());
            divIns.add(new BinaryDiv(dest, src1, src2));
        } else if (stochDest && stocharg1 && stocharg2) {
            //if all arguments are stochastic, perform stochastic subtraction
            StochasticRegister dest = getStochasticRegisterForVar(divide.getDest());
            StochasticRegister src1 = getStochasticRegisterForVar(divide.getSrc1());
            StochasticRegister src2 = getStochasticRegisterForVar(divide.getSrc2());
            divIns.addAll(performStochasticDivision(dest, src1, src2));
        } else if (stocharg1 == stocharg2) {
            //if srcs agree on type, perform relevant operation then cast to dest type
            Register tempDest;
            if (stocharg1) {
                StochasticRegister src1 = getStochasticRegisterForVar(divide.getSrc1());
                StochasticRegister src2 = getStochasticRegisterForVar(divide.getSrc2());
                tempDest = new StochasticRegister(getNextRegisterName(), polarity, bitstreamWidth);
                divIns.addAll(performStochasticDivision((StochasticRegister) tempDest, src1, src2));
            } else {
                BinaryRegister src1 = getBinaryRegisterForVar(divide.getSrc1());
                BinaryRegister src2 = getBinaryRegisterForVar(divide.getSrc2());
                tempDest = new BinaryRegister(getNextRegisterName());
                divIns.add(new BinaryDiv((BinaryRegister) tempDest, src1, src2));
            }
            //invariant, dest reg is opposite of tempDest
            if (stochDest) {
                StochasticRegister dest = getStochasticRegisterForVar(divide.getDest());
                divIns.addAll(convertBinarytoStochastic((BinaryRegister) tempDest, dest));
            } else {
                BinaryRegister dest = getBinaryRegisterForVar(divide.getDest());
                divIns.addAll(convertStochasticToBinary((StochasticRegister) tempDest, dest));
            }
        } else {
            //src disagree on type, so convert to dest type
            if (stochDest) {
                StochasticRegister dest = getStochasticRegisterForVar(divide.getDest());
                StochasticRegister src1;
                StochasticRegister src2;
                if (stocharg1) {
                    src1 = getStochasticRegisterForVar(divide.getSrc1());
                    //guaranteed src2 is binary
                    src2 = new StochasticRegister(getNextRegisterName(), polarity, bitstreamWidth);
                    BinaryRegister tempSrc2 = getBinaryRegisterForVar(divide.getSrc2());
                    divIns.addAll(convertBinarytoStochastic(tempSrc2, src2));
                } else {
                    src2 = getStochasticRegisterForVar(divide.getSrc2());
                    //guaranteed src1 is binary
                    src1 = new StochasticRegister(getNextRegisterName(), polarity, bitstreamWidth);
                    BinaryRegister tempSrc1 = getBinaryRegisterForVar(divide.getSrc1());
                    divIns.addAll(convertBinarytoStochastic(tempSrc1, src1));
                }
                divIns.addAll(performStochasticDivision(dest, src1, src2));
            } else {
                BinaryRegister dest = getBinaryRegisterForVar(divide.getDest());
                BinaryRegister src1;
                BinaryRegister src2;
                if (!stocharg1) {
                    src1 = getBinaryRegisterForVar(divide.getSrc1());
                    //guaranteed src2 is stoch
                    src2 = new BinaryRegister(getNextRegisterName());
                    StochasticRegister tempSrc2 = getStochasticRegisterForVar(divide.getSrc2());
                    divIns.addAll(convertStochasticToBinary(tempSrc2, src2));
                } else {
                    src2 = getBinaryRegisterForVar(divide.getSrc2());
                    //guaranteed src1 is stoch
                    src1 = new BinaryRegister(getNextRegisterName());
                    StochasticRegister tempSrc1 = getStochasticRegisterForVar(divide.getSrc1());
                    divIns.addAll(convertStochasticToBinary(tempSrc1, src1));
                }
                divIns.add(new BinaryDiv(dest, src1, src2));
            }
        }
        return divIns;
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
        List<InstructionNode> ifz = new LinkedList<>();
        //jump must have binary cond
        BinaryRegister arg1;
        if (!stochasticVariables.contains(ifZero.getCondition())) {
            arg1 = getBinaryRegisterForVar(ifZero.getCondition());
        } else {
            arg1 = new BinaryRegister(getNextRegisterName());
            StochasticRegister tempArg1 = getStochasticRegisterForVar(ifZero.getCondition());
            ifz.addAll(convertStochasticToBinary(tempArg1, arg1));
        }
        ifz.add(new BinaryJz(new Label(ifZero.getLabel().getName()), arg1));
        return ifz;
    }

    @Override
    public List<InstructionNode> visit(LabelNode labelNode) {
        List<InstructionNode> ISAins = new LinkedList<>();
        ISAins.add(new ISA.InstructionNodes.LabelNode(new Label(labelNode.getLabel().getName())));
        return ISAins;
    }

    @Override
    public List<InstructionNode> visit(IfNotEquals ifNotEquals) {
        List<InstructionNode> ifne = new LinkedList<>();
        //equality check must be performed in binary registers
        BinaryRegister equalCond = new BinaryRegister(getNextRegisterName());
        BinaryRegister arg1;
        BinaryRegister arg2;
        if (!stochasticVariables.contains(ifNotEquals.getCond1())) {
            arg1 = getBinaryRegisterForVar(ifNotEquals.getCond1());
        } else {
            arg1 = new BinaryRegister(getNextRegisterName());
            StochasticRegister tempArg1 = getStochasticRegisterForVar(ifNotEquals.getCond1());
            ifne.addAll(convertStochasticToBinary(tempArg1, arg1));
        }
        if (!stochasticVariables.contains(ifNotEquals.getCond2())) {
            arg2 = getBinaryRegisterForVar(ifNotEquals.getCond2());
        } else {
            arg2 = new BinaryRegister(getNextRegisterName());
            StochasticRegister tempArg2 = getStochasticRegisterForVar(ifNotEquals.getCond2());
            ifne.addAll(convertStochasticToBinary(tempArg2, arg2));
        }
        ifne.add(new BinaryEq(equalCond, arg1, arg2));
        //if the conds are not equal, equalCond contains 0, and thus we can use it in the jump
        ifne.add(new BinaryJz(new Label(ifNotEquals.getLabel().getName()), equalCond));
        return ifne;
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
        List<InstructionNode> loadIns = new LinkedList<>();
        //addresses must be in binary registers
        BinaryRegister addressReg;
        if (stochasticVariables.contains(load.getAddress())) {
            addressReg = new BinaryRegister(getNextRegisterName());
            StochasticRegister tempAddressReg = getStochasticRegisterForVar(load.getAddress());
            loadIns.addAll(convertStochasticToBinary(tempAddressReg, addressReg));
        } else {
            addressReg = getBinaryRegisterForVar(load.getAddress());
        }
        //if binary, we can immediately load value
        if (!stochasticVariables.contains(load.getDest())) {
            BinaryRegister destReg = getBinaryRegisterForVar(load.getDest());
            loadIns.add(new LoadIns(destReg, addressReg));
        } else {
            //load into temp then convert to stochastic
            BinaryRegister tempReg = new BinaryRegister(getNextRegisterName());
            loadIns.add(new LoadIns(tempReg, addressReg));
            StochasticRegister destReg = getStochasticRegisterForVar(load.getDest());
            loadIns.addAll(convertBinarytoStochastic(tempReg, destReg));
        }
        return loadIns;
    }

    @Override
    public List<InstructionNode> visit(Store store) {
        List<InstructionNode> storeIns = new LinkedList<>();
        //addresses must be in binary registers
        BinaryRegister addressReg;
        if (stochasticVariables.contains(store.getAddress())) {
            addressReg = new BinaryRegister(getNextRegisterName());
            StochasticRegister tempAddressReg = getStochasticRegisterForVar(store.getAddress());
            storeIns.addAll(convertStochasticToBinary(tempAddressReg, addressReg));
        } else {
            addressReg = getBinaryRegisterForVar(store.getAddress());
        }
        //if binary, we can immediately store value
        if (!stochasticVariables.contains(store.getSrc())) {
            BinaryRegister srcReg = getBinaryRegisterForVar(store.getSrc());
            storeIns.add(new StoreIns(srcReg, addressReg));
        } else {
            //convert to binary, then store
            StochasticRegister srcStochReg = getStochasticRegisterForVar(store.getSrc());
            BinaryRegister tempReg = new BinaryRegister(getNextRegisterName());
            storeIns.addAll(convertStochasticToBinary(srcStochReg, tempReg));
            storeIns.add(new StoreIns(tempReg, addressReg));
        }
        return storeIns;
    }

    @Override
    public List<InstructionNode> visit(SetLiteral setLiteral) {
        //since literal is an IR literal, we assume that the literal is a binary constant
        List<InstructionNode> literalIns = new LinkedList<>();
        if (!stochasticVariables.contains(setLiteral.getVariable())) {
            //load directly
            BinaryRegister varReg = getBinaryRegisterForVar(setLiteral.getVariable());
            literalIns.add(new LoadLiteralIns(varReg, new Literal(setLiteral.getLiteral().getValue())));
        } else {
            //load into temp then convert
            BinaryRegister tempReg = new BinaryRegister(getNextRegisterName());
            literalIns.add(new LoadLiteralIns(tempReg, new Literal(setLiteral.getLiteral().getValue())));
            StochasticRegister destReg = getStochasticRegisterForVar(setLiteral.getVariable());
            literalIns.addAll(convertBinarytoStochastic(tempReg, destReg));
        }
        return literalIns;
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

    private void initUpscaleIfNeeded(List<InstructionNode> ins) {
        if (!additionUpscaleInitialized) {
            additionUpscaleInitialized = true;
            StochasticRegister upscaleReg = getStochasticRegisterForVar(additionUpscaleVariable);
            ins.add(new LoadLiteralIns(upscaleReg, new Literal(0.5)));
        }
    }

    private void initStochasticOneIfNeeded(List<InstructionNode> ins) {
        if (!stochasticOneInitialized) {
            stochasticOneInitialized = true;
            StochasticRegister oneReg = getStochasticRegisterForVar(stochasticOneVariable);
            ins.add(new LoadLiteralIns(oneReg, new Literal(1)));
        }
    }

    private void initInverseScaleIfNeeded(List<InstructionNode> ins) {
        if (!inverseScaleInitialized) {
            inverseScaleInitialized = true;
            StochasticRegister inverseScale = getStochasticRegisterForVar(inverseScaleFactorVariable);
            ins.add(new LoadLiteralIns(inverseScale, new Literal( 1/ ((double) scaleFactor))));
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
        initScaleFactorIfNeeded(conversionIns);
        conversionIns.add(new BinaryMul(converted, tempScaledDownBin, scaleFactorReg));
        return conversionIns;
    }

    private List<InstructionNode> performStochasticAddition(StochasticRegister dest, StochasticRegister src1, StochasticRegister src2) {
        List<InstructionNode> additionList = new LinkedList<>();
        BinaryRegister addSReg = getBinaryRegisterForVar(addS);
        initAddSIfNeeded(additionList);
        //addition results in 1/2 of desired value
        additionList.add(new StochasticAdd(dest, src1, src2, addSReg));
        //need to divide by 1/2 to get desired value
        StochasticRegister upscaleFactor = getStochasticRegisterForVar(additionUpscaleVariable);
        initUpscaleIfNeeded(additionList);
        StochasticRegister one = getStochasticRegisterForVar(stochasticOneVariable);
        initStochasticOneIfNeeded(additionList);
        additionList.add(new StochasticDiv(dest, dest, upscaleFactor, one));
        return additionList;
    }

    private List<InstructionNode> performStochasticSubtraction(StochasticRegister dest, StochasticRegister src1, StochasticRegister src2) {
        List<InstructionNode> subList = new LinkedList<>();
        BinaryRegister addSReg = getBinaryRegisterForVar(addS);
        initAddSIfNeeded(subList);
        //subtraction results in 1/2 of desired value
        subList.add(new StochasticSub(dest, src1, src2, addSReg));
        //need to divide by 1/2 to get desired value
        StochasticRegister upscaleFactor = getStochasticRegisterForVar(additionUpscaleVariable);
        initUpscaleIfNeeded(subList);
        StochasticRegister one = getStochasticRegisterForVar(stochasticOneVariable);
        initStochasticOneIfNeeded(subList);
        subList.add(new StochasticDiv(dest, dest, upscaleFactor, one));
        return subList;
    }

    private List<InstructionNode> performStochasticMultiplication(StochasticRegister dest, StochasticRegister src1, StochasticRegister src2) {
        List<InstructionNode> mulList = new LinkedList<>();
        //multiplication results in 1/f of desired value
        mulList.add(new StochasticMul(dest, src1, src2));
        StochasticRegister inverseScaleFactor = getStochasticRegisterForVar(inverseScaleFactorVariable);
        initInverseScaleIfNeeded(mulList);
        StochasticRegister one = getStochasticRegisterForVar(stochasticOneVariable);
        initStochasticOneIfNeeded(mulList);
        mulList.add(new StochasticDiv(dest, dest, inverseScaleFactor, one));
        return mulList;
    }

    private List<InstructionNode> performStochasticDivision(StochasticRegister dest, StochasticRegister src1, StochasticRegister src2) {
        List<InstructionNode> divList = new LinkedList<>();
        //division results in f * the desired value
        StochasticRegister inverseScaleFactor = getStochasticRegisterForVar(inverseScaleFactorVariable);
        initInverseScaleIfNeeded(divList);
        divList.add(new StochasticDiv(dest, src1, src2, inverseScaleFactor));
        return divList;
    }
}
