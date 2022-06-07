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
        Literal literal = new Literal(12.3);

        testIR.add(new LabelNode(new Label("Bin Set Literal")));
        testIR.add(new SetLiteral(binDest, literal));
        testIR.add(new LabelNode(new Label("Stoch Set Literal")));
        testIR.add(new SetLiteral(stochDest, literal));
        testIR.add(new LabelNode(new Label("Bin Store, Bin Addr")));
        testIR.add(new Store(binDest, binArg1));
        testIR.add(new LabelNode(new Label("Stoch Store, Stoch Addr")));
        testIR.add(new Store(stochDest, stochArg1));
        testIR.add(new LabelNode(new Label("Stoch Store, Bin Addr")));
        testIR.add(new Store(stochDest, binArg1));
        testIR.add(new LabelNode(new Label("Bin Store, Stoch Addr")));
        testIR.add(new Store(binDest, stochArg1));
        testIR.add(new LabelNode(new Label("Bin Load, Bin Addr")));
        testIR.add(new Load(binDest, binArg1));
        testIR.add(new LabelNode(new Label("Stoch Load, Stoch Addr")));
        testIR.add(new Load(stochDest, stochArg1));
        testIR.add(new LabelNode(new Label("Stoch Load, Bin Addr")));
        testIR.add(new Load(stochDest, binArg1));
        testIR.add(new LabelNode(new Label("Bin Load, Stoch Addr")));
        testIR.add(new Load(binDest, stochArg1));

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
