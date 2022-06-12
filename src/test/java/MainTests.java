import IR.IRNodes.*;
import IR.Literals.Literal;
import IR.Variables.Variable;
import IR.Visitors.CompilerVisitor;
import IR.Visitors.RegisterCollectorVisitor;
import ISA.InstructionNodes.InstructionNode;
import ISA.Registers.RegisterPolarity;
import ISAInterpreter.ISAInterpreter;
import Workloads.Workloads;
import com.google.common.collect.ImmutableSet;


import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MainTests {
    public static void main(String[] args) {
        // Generate the IR code for the dot product
        Variable out = new Variable("out");
        List<Double> x = getRangeOfDoubles(1, 3,0.5);
        List<Double> y = getRangeOfDoubles(1, 3, 0.5);
        List<IRNode> irPrg = Workloads.getDotProductIR(x, y, out);
        irPrg.add(new Print(out));

        // Decide which registers should be stochastic
        // TODO: Salekh, if you get around to it, then put your heuristics here.
        Set<Variable> stochasticVars = getAllVariables(irPrg);
        // Output value must be binary for now due to a compiler<->interpreter interaction bug.
        // Fix this if we have time; but it isn't as critical as everything else.
        stochasticVars.remove(out);

        // Compile the code to ISA
        CompilerVisitor compilerVisitor = new CompilerVisitor(ImmutableSet.copyOf(stochasticVars),
                1000, RegisterPolarity.BIPOLAR, 20);
        List<InstructionNode> isaPrg = compilerVisitor.visitAllInstructions(irPrg);

        for (InstructionNode i : isaPrg) {
            System.out.println(i);
        }

        // Execute the ISA Program
        List<String> testOut = ISAInterpreter.getProgramOutput(isaPrg, 0.001);

        // Print the output
        System.out.println(testOut.get(0));
        System.out.println(dotProduct(x, y));
    }

    private static List<Double> getRandomDoubles(int count, double range) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        List<Double> ret = new ArrayList<>();
        for (int i=0; i < count; i++) {
            ret.add((rand.nextDouble() - 0.5) * 2 * range );
        }
        return ret;
    }

    private static List<Double> getRangeOfDoubles(double start, double end, double step) {
        List<Double> ret = new ArrayList<>();

        if (step == 0) {
            return ret;
        }
        if (step > 0) {
            while (start < end) {
                ret.add(start);
                start += step;
            }
        } else {
            while (start > end) {
                ret.add(start);
                start += step;
            }
        }
        return ret;
    }

    private static double dotProduct(List<Double> a, List<Double> b){
        double sum = 0;
        for (int i = 0; i < a.size(); i++)
        {
            sum += a.get(i) * b.get(i);
        }
        return sum;
    }

    private static Set<Variable> getAllVariables(List<IRNode> prg) {
        RegisterCollectorVisitor rcv = new RegisterCollectorVisitor();
        return new HashSet<>(rcv.visitAllInstructions(prg));
    }

    private static Set<Variable> getNoVariables(List<IRNode> prg) {
        return new HashSet<>();
    }
}
