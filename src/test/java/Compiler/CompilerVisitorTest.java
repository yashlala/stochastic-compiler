package Compiler;

import IR.IRNodes.*;
import IR.Labels.Label;
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
        Label start = new Label("Start");
        testIR.add(new LabelNode(start));
        testIR.add(new Goto(start));

        List<IRNode> loopContents = new LinkedList<>();
        Variable stoch = new Variable("stoch");
        Variable bin = new Variable("bin");
        Variable loopStart = new Variable("loopStart");
        Variable loopEnd = new Variable("loopEnd");
        Variable loopCond = new Variable("loopCond");
        loopContents.add(new Print(stoch));
        loopContents.add(new Print(bin));
        testIR.add(new ForLoop(loopCond, loopStart, loopEnd, loopContents));

        PrintVisitor irPrint = new PrintVisitor();
        System.out.println("IR TESTCASE\n");
        irPrint.visitAllInstructions(testIR);
        System.out.println("\nISA TRANSLATION\n");
        ImmutableSet<Variable> stochVars = ImmutableSet.of(stoch);
        CompilerVisitor compiler = new CompilerVisitor(stochVars, 16, RegisterPolarity.BIPOLAR);
        List<InstructionNode> compiled_ins = compiler.visitAllInstructions(testIR);
        ISA.Visitors.PrintVisitor isaPrint = new ISA.Visitors.PrintVisitor();
        isaPrint.visitAllInstructions(compiled_ins);
    }
}
