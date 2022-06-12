import IR.IRNodes.*;
import IR.Literals.Literal;
import IR.Variables.Variable;
import IR.Visitors.CompilerVisitor;
import ISA.InstructionNodes.InstructionNode;
import ISA.Registers.RegisterPolarity;
import ISAInterpreter.ISAInterpreter;
import Workloads.Workloads;

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
        // TODO: Use Salekh's code here
        Set<IRNode> stochasticVars = new HashSet<>();

        // Compile the code to ISA
        CompilerVisitor compilerVisitor = new CompilerVisitor(ImmutableSet.copyOf(new HashSet<>()),
                1000, RegisterPolarity.BIPOLAR, 20);
        List<InstructionNode> isaPrg = compilerVisitor.visitAllInstructions(irPrg);

        // Execute the ISA Program
        List<String> testOut = ISAInterpreter.getProgramOutput(isaPrg, 0);

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
}
