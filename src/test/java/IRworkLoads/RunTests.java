package IRworkLoads;

import IR.IRNodes.IRNode;
import IR.IRNodes.Print;
import IR.Variables.Variable;
import IR.Visitors.CompilerVisitor;
import ISA.InstructionNodes.InstructionNode;
import ISA.Registers.RegisterPolarity;
import ISAInterpreter.ISAInterpreter;
import RegSelector.Selector;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.Var;

import java.util.*;

public
class RunTests {
    public void runDotProduct(int N, Variable v,  int bitWidth,int scalingFactor ,int noiseCoefficient){
        List< IRNode > instructions = new LinkedList<>();
        DotProduct dotProduct = new DotProduct();
        instructions.addAll( dotProduct.calculateDotProduct(N,v,0));
        instructions.add(new Print(v));
        Selector selector = new Selector();
        ImmutableSet<Variable> stochVars = ImmutableSet.copyOf(selector.collectAllRegisters(instructions));
        List< InstructionNode > isaInstructions= this.generateISAcode(instructions,scalingFactor,bitWidth, stochVars);
        this.executeISAcode(isaInstructions,noiseCoefficient);
    }
    public  void runMatVectMult(Integer N,Integer M, List<Variable> output,int bitWidth,int scalingFactor,int noiseCoefficient){
        List< IRNode > instructions = new LinkedList<>();
        MatVectMult matVectMult = new MatVectMult();
        instructions = matVectMult.calculateMatVectMult(N,M,output);

        Selector selector = new Selector();
        ImmutableSet<Variable> stochVars = ImmutableSet.copyOf(selector.collectAllRegisters(instructions));
        List< InstructionNode > isaInstructions= this.generateISAcode(instructions,scalingFactor,bitWidth, stochVars);
        this.executeISAcode(isaInstructions,noiseCoefficient);
    }
    public void runMatMatMult(Integer N, Integer M, List<Variable> output,int bitWidth, int scalingFactor,double noiseCoefficient){
        List< IRNode > instructions = new LinkedList<>();
        MatMult matMult = new MatMult();
        Selector selector = new Selector();
        ImmutableSet<Variable> stochVars = ImmutableSet.copyOf(selector.collectAllRegisters(instructions));
        List< InstructionNode > isaInstructions= this.generateISAcode(instructions,scalingFactor,bitWidth, stochVars);
        this.executeISAcode(isaInstructions,noiseCoefficient);
    }
    public void runConv(Integer N, Integer M,Integer L, int kernelDim,Variable[][][]output,int bitWidth, int scalingFactor,int noiseCoefficient){
        List< IRNode > instructions = new LinkedList<>();
        Conv_3d conv_3d = new Conv_3d();
        instructions = conv_3d.fullConvolution(N,M,L,kernelDim,output);

        Selector selector = new Selector();
        ImmutableSet<Variable> stochVars = ImmutableSet.copyOf(selector.collectAllRegisters(instructions));
        List< InstructionNode > isaInstructions= this.generateISAcode(instructions,scalingFactor,bitWidth, stochVars);
        this.executeISAcode(isaInstructions,noiseCoefficient);
    }
    public List< InstructionNode > generateISAcode(List<IRNode> instructions, int scalingFactor, int bitWidth, ImmutableSet <Variable> stochVars){
        List< InstructionNode > isaInstructions = new ArrayList <>();
        CompilerVisitor cv = new CompilerVisitor(stochVars, bitWidth, RegisterPolarity.BIPOLAR, scalingFactor);

        isaInstructions.addAll(cv.visitAllInstructions(instructions));
        return isaInstructions;
    }
    public void executeISAcode(List< InstructionNode > instructions,double noiseCoefficient){
         ISAInterpreter isaIP = new ISAInterpreter();



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
    public static void main(String[] args){
        List< IRNode > instructions = new LinkedList<>();
        RunTests runTests = new RunTests();
        //Specify the test parameters
        int noiseCoefficient = 10;
        int scalingFactor = 6000;
        int bitWidth = 40000;
        int N = 50;
        int M = 50;
        int L = 2;
        int kernelDim = 2;
        //Uncomment to run the test for the dotProduct
//        Specify the register name for the final result to be stored
//        Variable output = new Variable("output");
//        runTests.runDotProduct(N,output,bitWidth,scalingFactor,noiseCoefficient);


        //Uncomment to run the test for matrix marix multiply
        //Specify the register names for the final results to be stored
        List<Variable> output = new LinkedList <>();
        for(int i=0; i<N;i++){
            output.add(new Variable("output_"+i));
        }
        runTests.runMatMatMult(N,M,output,bitWidth,scalingFactor,noiseCoefficient);


        //Uncomment to run the test for matrix vect multiply
        //Specify the register names for the final results to be stored
//        List<Variable> output = new LinkedList <>();
//        for(int i=0; i<N;i++){
//            output.add(new Variable("output_"+i));
//        }
//        runTests.runMatVectMult(N,M,output,bitWidth,scalingFactor,noiseCoefficient);

        //Uncomment to run the test for 3d_convolution
        //Specify the register names for the final results to be stored
//        Variable[][][] output = new Variable[N][M][L];
//        for(int i=0; i+kernelDim<=N; i++){
//            for(int j=0; j+kernelDim<=M; j++){
//                for(int k=0; k+kernelDim<=L; k++){
//                    output[i][j][k] = new Variable("final_out_"+i+"_"+j+"_"+k);
//                }
//            }
//        }
//        runTests.runConv(N,M,L,kernelDim,output,bitWidth,scalingFactor,noiseCoefficient);

    }
}
