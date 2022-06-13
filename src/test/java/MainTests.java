import IR.IRNodes.*;
import IR.Literals.Literal;
import IR.Variables.Variable;
import IR.Visitors.CompilerVisitor;
import IR.Visitors.RegisterCollectorVisitor;
import IRworkLoads.MatMult;
import IRworkLoads.RunTests;
import ISA.InstructionNodes.InstructionNode;
import ISA.Registers.RegisterPolarity;
import ISAInterpreter.ISAInterpreter;
import Workloads.Workloads;
import com.google.common.collect.ImmutableSet;
import lombok.RequiredArgsConstructor;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import RegSelector.Selector;
public class MainTests {
    @RequiredArgsConstructor
    static class TestOutput {
        final double expected;
        final double observed;
        final double delta;
    }

    public static void main(String[] args) {
        try {
            FileWriter fw = new FileWriter("/home/lala/io/data.csv", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("frame_size,scale_factor,noise_coefficient,avg_relative_abs_err\n");
            for (int frameSize = 20; frameSize < 1000; frameSize += 10) {
                for (int scaleFactor = 10; scaleFactor < frameSize; scaleFactor += 10) {
                    double errorAccumulator = 0;
                    for (int trial=0; trial < 3; trial++) {
                        TestOutput out = testDotProduct(frameSize, scaleFactor, 0.05);
                        errorAccumulator += Math.abs((double) out.delta / out.expected);
                    }
                    double avgErrorMagnitude = errorAccumulator / 3;
                    bw.write("" + frameSize + "," + scaleFactor + "," + 0.05 + "," + avgErrorMagnitude);
                    bw.newLine();
                }
            }
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
      //Runs the matrix multiple tests (Salekh)
      runMatMatMultTest();
    }

    private static TestOutput testDotProduct(int frameSize, int scaleFactor, double noiseCoefficient) {
        // Generate the IR code for the dot product
        Variable out = new Variable("out");
        List<Double> x = getRandomDoubles(5, 4);
        List<Double> y = getRandomDoubles(5, 4);
        List<IRNode> irPrg = Workloads.getDotProductIR(x, y, out);
        irPrg.add(new Print(out));
        Integer advanced = 1;
        // Decide which registers should be stochastic
        // TODO: Salekh, if you get around to it, then put your heuristics here.
//        Set<Variable> stochasticVars = getAllVariables(irPrg);
        Set<Variable>stochasticVars = RegSelector.Selector.returnBinaryRegisters(irPrg,true,false,advanced);
        // Output value must be binary for now due to a compiler<->interpreter interaction bug.
        // Fix this if we have time; but it isn't as critical as everything else.
        stochasticVars.remove(out);

        // Compile the code to ISA
        CompilerVisitor compilerVisitor = new CompilerVisitor(ImmutableSet.copyOf(stochasticVars),
                frameSize, RegisterPolarity.BIPOLAR, scaleFactor);
        List<InstructionNode> isaPrg = compilerVisitor.visitAllInstructions(irPrg);

        // Execute the ISA Program
        List<Double> testOut = toDoubles(ISAInterpreter.getProgramOutput(isaPrg, noiseCoefficient));

        return new TestOutput(dotProduct(x, y), testOut.get(0), dotProduct(x, y) - testOut.get(0));
    }
    private static void runMatMatMultTest( )  {
        int N = 100;
        int M = 100;
        int stride = 5;
        int filterLevel = 5;

        for(int i=stride; i<N; i+=stride){
            for(int j=stride; j<M; j+=stride){
                testMatMult(i,j,filterLevel);
            }
        }
//        testMatMult(5,5,5);




    }
//
//    private static TestOutput testMatVectMult(int frameSize, int scaleFactor, double noiseCoefficient) {
//
//
//    }
//    private static TestOutput test3D_Conv_Mult(int frameSize, int scaleFactor, double noiseCoefficient) {
//
//
//    }
    private static void testMatMult(int N, int M, int filterLevel){
        try {

            FileWriter fw = new FileWriter("/Users/salekhparkhati/ucla/code/cs259/stochastic-compiler/src/mat_mult.csv", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("frame_size,scale_factor,noise_coefficient,N,M,filterLevel,relative_err\n");
            for (int frameSize = 2000; frameSize < 10000; frameSize += 1000) {
//                int frameSize = 2000;
                int scaleFactor = frameSize / 5;
//                double noiseCoefficient = 0.01;
                for (double noiseCoefficient = 0; noiseCoefficient < 0.05; noiseCoefficient += 0.01) {
                    List<Double> result =  MatMult.calculateMatMult(N,M,filterLevel,frameSize,scaleFactor,noiseCoefficient);

                    double err = (double) result.get(2) / result.get(0);

                    bw.write("" + frameSize + "," + scaleFactor + "," + noiseCoefficient +","+N+","+M+ ","+filterLevel+"," + err);
                    bw.newLine();

                }
            }
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    private static List<Double> toDoubles(List<String> output) {
        List<Double> ret = new ArrayList<>();
        for (String s : output) {
            String[] parts = s.split(" ");
            ret.add(Double.parseDouble(parts[2]));
        }
        return ret;
    }
}
