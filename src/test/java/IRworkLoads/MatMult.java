package IRworkLoads;

import IR.IRNodes.IRNode;
import IR.Variables.Variable;
import Workloads.Workloads;
import IR.IRNodes.Print;
import IR.Visitors.CompilerVisitor;
import com.google.common.collect.ImmutableSet;
import RegSelector.Selector;
import ISA.Registers.RegisterPolarity;
import java.util.concurrent.ThreadLocalRandom;
import ISA.InstructionNodes.InstructionNode;
import ISAInterpreter.ISAInterpreter;
import IR.Visitors.RegisterCollectorVisitor;
import java.util.*;

public
class MatMult {

    public static
    List<Double> calculateMatMult( Integer N, Integer M , int filterLevel, int frameSize, int scaleFactor, double noiseCoefficient){
        DotProduct dotProduct = new DotProduct();
        Double [][] reference= new Double[N][M];
        List<IRNode> results = new LinkedList <>();

        //calculate the product
        for(int i=0; i<N; i++){
            for(int j=0; j<M; j++){
                int start1 = ThreadLocalRandom.current().nextInt(0, 9);
                int start2 = ThreadLocalRandom.current().nextInt(0, 9);

                List<Double> x =getNumberOfDoubles(start1,M,0.2);
                List<Double> y =getNumberOfDoubles(start2,M,0.2);

                Variable out = new Variable("output_"+i+"_"+j);
                results= Workloads.getDotProductIR(x,y,out);
                reference[i][j] = Workloads.dotProduct(x,y);
                results.add(new Print(out));

            }
        }
//            Set<Variable> stochasticVars = Selector.returnBinaryRegisters(results,true,true,filterLevel);
            // Compile the code to ISA
            Set<Variable> stochasticVars = getAllVariables(results);
            CompilerVisitor compilerVisitor = new CompilerVisitor(ImmutableSet.copyOf(stochasticVars),
                    frameSize, RegisterPolarity.BIPOLAR, scaleFactor);
            List<InstructionNode> isaPrg = compilerVisitor.visitAllInstructions(results);

            // Execute the ISA Program
            List<Double> testOut = toDoubles(ISAInterpreter.getProgramOutput(isaPrg, noiseCoefficient));

            //compute the avg delta
            double test_sum=0;
            for(Double i: testOut){
               test_sum+=i;
            }
            double ref_sum=0;
            for(int i=0; i<N; i++){
                for(int j=0; j<N; j++){
                    ref_sum+=reference[i][j];
                }
            }
            double delta= ref_sum - test_sum;
            List<Double> returnvals = new ArrayList<>();
            returnvals.add(ref_sum);
            returnvals.add(test_sum);
            returnvals.add(delta);
            return returnvals;

    }
    private static List<Double> getNumberOfDoubles(double start, int total, double step) {
        List<Double> ret = new ArrayList<>();
        int count = 0;
        if (step == 0) {
            return ret;
        }
        while(count<total){
            ret.add(start);
            start+=step;
            count++;
        }

        return ret;
    }
    private static List<Double> toDoubles(List<String> output) {
        List<Double> ret = new ArrayList<>();
        for (String s : output) {
            String[] parts = s.split(" ");
            ret.add(Double.parseDouble(parts[2]));
        }
        return ret;
    }
    private static Set<Variable> getAllVariables(List<IRNode> prg) {
        RegisterCollectorVisitor rcv = new RegisterCollectorVisitor();
        return new HashSet<>(rcv.visitAllInstructions(prg));
    }

    // uncomment for local testing
//    public static void main(String[] args){
//        List <Variable> res = new LinkedList <>();
//        for(int i=0; i<2; i++)res.add(new Variable("res_"+i));
//        for(Double i: new MatMult().calculateMatMult(5,5,0,10000,100,0.01)){
//            System.out.println(i);
//        }
//
//    }
}
