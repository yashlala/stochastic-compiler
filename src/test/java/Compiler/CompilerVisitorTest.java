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

        testIR.add(new LabelNode(new Label("All Bin")));
        testIR.add(new Divide(binDest, binArg1, binArg2));
        testIR.add(new LabelNode(new Label("All Stoch")));
        testIR.add(new Divide(stochDest, stochArg1, stochArg2));
        testIR.add(new LabelNode(new Label("Bin Args, Stoch Dest")));
        testIR.add(new Divide(stochDest, binArg1, binArg2));
        testIR.add(new LabelNode(new Label("Stoch Args, Bin Dest")));
        testIR.add(new Divide(binDest, stochArg1, stochArg2));
        testIR.add(new LabelNode(new Label("Bin Dest, Stoch Arg1")));
        testIR.add(new Divide(binDest, stochArg1, binArg2));
        testIR.add(new LabelNode(new Label("Bin Dest, Stoch Arg2")));
        testIR.add(new Divide(binDest, binArg1, stochArg2));
        testIR.add(new LabelNode(new Label("Stoch Dest, Bin Arg1")));
        testIR.add(new Divide(stochDest, binArg1, stochArg2));
        testIR.add(new LabelNode(new Label("Stoch Dest, Bin Arg2")));
        testIR.add(new Divide(stochDest, stochArg1, binArg2));

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
