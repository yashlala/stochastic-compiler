package Compiler;

import IR.IRNodes.*;
import IR.Labels.Label;
import IR.Literals.Literal;
import IR.Variables.Variable;
import IR.Visitors.CompilerVisitor;
import IR.Visitors.PrintVisitor;
import ISA.InstructionNodes.InstructionNode;
import ISA.Registers.RegisterPolarity;
import com.google.common.collect.ImmutableSet;

import java.util.LinkedList;
import java.util.List;

public class CompilerVisitorTest {
    public static void main(String[] args) {
        List<IRNode> testIR = new LinkedList<>();
        Variable stochDest = new Variable("stochDest");
        Variable stochArg1 = new Variable("stochArg1");
        Variable stochArg2 = new Variable("stochArg2");
        Variable binDest = new Variable("binDest");
        Variable binArg1 = new Variable("binArg1");
        Variable binArg2 = new Variable("binArg2");
        Label label = new Label("JumpHERE");

        testIR.add(new LabelNode(new Label("Bin Cond")));
        testIR.add(new IfZero(binDest, label));
        testIR.add(new LabelNode(new Label("Stoch Cond")));
        testIR.add(new IfZero(stochDest, label));
        testIR.add(new LabelNode(new Label("Bin Cond1, Bin Cond2")));
        testIR.add(new IfNotEquals(binDest, binArg1, label));
        testIR.add(new LabelNode(new Label("Stoch Cond1, Stoch Cond2")));
        testIR.add(new IfNotEquals(stochDest, stochArg1, label));
        testIR.add(new LabelNode(new Label("Stoch Cond1, Bin Cond2")));
        testIR.add(new IfNotEquals(stochDest, binArg1, label));
        testIR.add(new LabelNode(new Label("Bin Cond1, Stoch Cond2")));
        testIR.add(new IfNotEquals(binDest, stochArg1, label));
        testIR.add(new LabelNode(label));

        PrintVisitor irPrint = new PrintVisitor();
        System.out.println("IR TESTCASE\n");
        irPrint.visitAllInstructions(testIR);
        System.out.println("\nISA TRANSLATION\n");
        ImmutableSet<Variable> stochVars = ImmutableSet.of(stochDest, stochArg1, stochArg2);
        CompilerVisitor compiler = new CompilerVisitor(stochVars, 16, RegisterPolarity.BIPOLAR, 10);
        List<InstructionNode> compiled_ins = compiler.visitAllInstructions(testIR);
        ISA.Visitors.PrintVisitor isaPrint = new ISA.Visitors.PrintVisitor();
        isaPrint.visitAllInstructions(compiled_ins);
    }
}
