package Workloads;

import IR.IRNodes.*;
import IR.Literals.Literal;
import IR.Variables.Variable;

import java.util.ArrayList;
import java.util.List;

public class Workloads {
    public static List<IRNode> getDotProductIR(List<Double> x, List<Double> y, Variable output) {
        if (x.size() != y.size()) {
            throw new RuntimeException("Unequal list lengths");
        }

        Variable acc = new Variable("acc");
        Variable tmp = new Variable("tmp");

        List<IRNode> prg = new ArrayList<>();
        for (int i=0; i < x.size(); i++) {
            prg.add(new SetLiteral(new Variable("x_" + i), new Literal(x.get(i))));
            prg.add(new SetLiteral(new Variable("y_" + i), new Literal(y.get(i))));
        }

        prg.add(new SetLiteral(acc, new Literal(0)));
        for (int i=0; i < x.size(); i++) {
            prg.add(new Multiply(tmp, new Variable("x_" + i), new Variable("y_" + i)));
            prg.add(new Add(acc, acc, tmp));
        }

        prg.add(new SetLiteral(tmp, new Literal(0)));
        prg.add(new Add(output, acc, tmp));

        return prg;
    }
}
