package Compiler;

import IR.IRNodes.*;
import IR.Labels.Label;
import IR.Literals.Literal;
import IR.Variables.Variable;
import IR.Visitors.CompilerVisitor;
import ISA.InstructionNodes.InstructionNode;
import ISA.Registers.RegisterPolarity;
import ISAInterpreter.ISAInterpreter;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompilerCorrectnessTests {
    private static final int WIDTH = 10000;
    private static final RegisterPolarity POLARITY = RegisterPolarity.BIPOLAR;
    private static final int F = 10;
    private static final double delta = 0.001;
    @Test
    void additionTest() {
        List<IRNode> addProgram = new LinkedList<>();
        Variable a = new Variable("a");
        Variable b = new Variable("b");
        Variable c = new Variable("c");
        addProgram.add(new SetLiteral(a, new Literal(1.5)));
        addProgram.add(new SetLiteral(b, new Literal(2)));
        addProgram.add(new Add(c, a, b));
        addProgram.add(new Print(c));
        List<List<Double>> res = getAllStochPerms(addProgram, a, b, c);
        for (int i = 1; i < 8; i++) {
            if (i/ 4 == 1) {
                assertEquals(res.get(0).get(0), F * res.get(i).get(0), delta);
            } else {
                assertEquals(res.get(0).get(0), res.get(i).get(0), delta);
            }
        }
    }

    @Test
    void divideTest() {
        List<IRNode> addProgram = new LinkedList<>();
        Variable a = new Variable("a");
        Variable b = new Variable("b");
        Variable c = new Variable("c");
        addProgram.add(new SetLiteral(a, new Literal(2.5)));
        addProgram.add(new SetLiteral(b, new Literal(3.25)));
        addProgram.add(new Divide(c, a, b));
        addProgram.add(new Print(c));
        List<List<Double>> res = getAllStochPerms(addProgram, a, b, c);
        for (int i = 1; i < 8; i++) {
            if (i/ 4 == 1) {
                assertEquals(res.get(0).get(0), F * res.get(i).get(0), delta);
            } else {
                assertEquals(res.get(0).get(0), res.get(i).get(0), delta);
            }
        }
    }

    @Test
    void equalsTest() {
        List<IRNode> addProgram = new LinkedList<>();
        Variable a = new Variable("a");
        Variable b = new Variable("b");
        Variable c = new Variable("c");
        addProgram.add(new SetLiteral(a, new Literal(2.25)));
        addProgram.add(new SetLiteral(b, new Literal(2.25)));
        addProgram.add(new Equals(c, a, b));
        addProgram.add(new Print(c));
        List<List<Double>> res = getAllStochPerms(addProgram, a, b, c);
        for (int i = 1; i < 8; i++) {
            if (i/ 4 == 1) {
                assertEquals(res.get(0).get(0), F * res.get(i).get(0), delta);
            } else {
                assertEquals(res.get(0).get(0), res.get(i).get(0), delta);
            }
        }
    }

    @Test
    void forLoopTest() {
        List<IRNode> addProgram = new LinkedList<>();
        Variable a = new Variable("a");
        Variable b = new Variable("b");
        Variable c = new Variable("c");
        Variable d = new Variable("d");
        Variable e = new Variable("e");
        addProgram.add(new SetLiteral(a, new Literal(0)));
        addProgram.add(new SetLiteral(b, new Literal(5)));
        addProgram.add(new SetLiteral(d, new Literal(37)));
        addProgram.add(new Print(d));
        addProgram.add(new SetLiteral(e, new Literal(1)));
        addProgram.add(new SetLiteral(d, new Literal(6)));
        List<IRNode> contents = new LinkedList<>();
        contents.add(new Subtract(d, d, e));
        contents.add(new Print(d));
        addProgram.add(new ForLoop(c, a, b, contents));
        addProgram.add(new SetLiteral(d, new Literal(17)));
        addProgram.add(new Print(d));
        CompilerVisitor vis = new CompilerVisitor(ImmutableSet.of(), WIDTH , POLARITY, F);
        List<InstructionNode> isa = vis.visitAllInstructions(addProgram);
        List<Double> res = toDoubles(ISAInterpreter.getProgramOutput(isa, 0));
        assertEquals(7, res.size());
        for (int i = 0; i < res.size(); i++) {
            if (i == 0) {
                assertEquals(37, res.get(i));
            } else if (i == 6) {
                assertEquals(17, res.get(i));
            } else {
                assertEquals(6-i, res.get(i));
            }
        }
    }

    @Test
    void gotoTest() {
        List<IRNode> program = new LinkedList<>();
        Variable a = new Variable("a");
        program.add(new SetLiteral(a, new Literal(3)));
        Label label = new Label("jumpHere");
        program.add(new Goto(label));
        program.add(new Print(a));
        program.add(new LabelNode(label));
        CompilerVisitor vis = new CompilerVisitor(ImmutableSet.of(), WIDTH , POLARITY, F);
        List<InstructionNode> isa = vis.visitAllInstructions(program);
        List<Double> res = toDoubles(ISAInterpreter.getProgramOutput(isa, 0));
        assertEquals(0, res.size());
    }

    @Test
    void multiplicationTest() {
        List<IRNode> addProgram = new LinkedList<>();
        Variable a = new Variable("a");
        Variable b = new Variable("b");
        Variable c = new Variable("c");
        addProgram.add(new SetLiteral(a, new Literal(2.5)));
        addProgram.add(new SetLiteral(b, new Literal(3.25)));
        addProgram.add(new Multiply(c, a, b));
        addProgram.add(new Print(c));
        List<List<Double>> res = getAllStochPerms(addProgram, a, b, c);
        for (int i = 1; i < 8; i++) {
            if (i/ 4 == 1) {
                assertEquals(res.get(0).get(0), F * res.get(i).get(0), delta);
            } else {
                assertEquals(res.get(0).get(0), res.get(i).get(0), delta);
            }
        }
    }

    @Test
    void subtractionTest() {
        List<IRNode> addProgram = new LinkedList<>();
        Variable a = new Variable("a");
        Variable b = new Variable("b");
        Variable c = new Variable("c");
        addProgram.add(new SetLiteral(a, new Literal(1.5)));
        addProgram.add(new SetLiteral(b, new Literal(2)));
        addProgram.add(new Subtract(c, a, b));
        addProgram.add(new Print(c));
        List<List<Double>> res = getAllStochPerms(addProgram, a, b, c);
        for (int i = 1; i < 8; i++) {
            if (i/ 4 == 1) {
                assertEquals(res.get(0).get(0), F * res.get(i).get(0), delta);
            } else {
                assertEquals(res.get(0).get(0), res.get(i).get(0), delta);
            }
        }
    }



    private List<List<Double>> getAllStochPerms(List<IRNode> program, Variable a, Variable b, Variable c) {
        List<List<Double>> out = new ArrayList<>();
        boolean[] flags = {false, true};
        for (boolean cflag : flags) {
            for (boolean aflag: flags) {
                for (boolean bflag: flags) {
                    Set<Variable> vars = new HashSet<>();
                    if (aflag) vars.add(a);
                    if (bflag) vars.add(b);
                    if (cflag) vars.add(c);
                    out.add(get3OpVisitorOut(program, vars));
                }
            }
        }
        return out;
    }

    private List<Double> get3OpVisitorOut(List<IRNode> program, Set<Variable> stochasticVar) {
        CompilerVisitor vis = new CompilerVisitor(ImmutableSet.copyOf(stochasticVar), WIDTH , POLARITY, F);
        List<InstructionNode> isa = vis.visitAllInstructions(program);
        return toDoubles(ISAInterpreter.getProgramOutput(isa, 0));
    }

    private List<Double> toDoubles(List<String> output) {
        List<Double> ret = new ArrayList<>();
        for (String s : output) {
            String[] parts = s.split(" ");
            ret.add(Double.parseDouble(parts[2]));
        }
        return ret;
    }
}
