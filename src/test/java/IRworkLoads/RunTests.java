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
    public  void runMatVectMult(){
        List< IRNode > instructions = new LinkedList<>();
        MatVectMult matVectMult = new MatVectMult();
    }
    public void runMatMatMult(Integer N, Integer M, List<Variable> output,int scalingFactor,int bitWidth, int noiseCoefficient){
        List< IRNode > instructions = new LinkedList<>();
        MatMult matMult = new MatMult();
        instructions = matMult.calculateMatMult(N,M,output);
        System.out.println("\n\n\nGenerated IR\n\n\n");
        for(IRNode i: instructions){
            System.out.println(i);
        }
        Selector selector = new Selector();
        ImmutableSet<Variable> stochVars = ImmutableSet.copyOf(selector.collectAllRegisters(instructions));
        List< InstructionNode > isaInstructions= this.generateISAcode(instructions,scalingFactor,bitWidth, stochVars);
        this.executeISAcode(isaInstructions,noiseCoefficient);
    }
    public void runConv(){
        List< IRNode > instructions = new LinkedList<>();
        Conv_3d conv_3d = new Conv_3d();
    }
    public List< InstructionNode > generateISAcode(List<IRNode> instructions, int scalingFactor, int bitWidth, ImmutableSet <Variable> stochVars){
        List< InstructionNode > isaInstructions = new ArrayList <>();
        CompilerVisitor cv = new CompilerVisitor(stochVars, bitWidth, RegisterPolarity.BIPOLAR, scalingFactor);
//        System.out.println("\n\npassed in ir nodes are");
//        System.out.println(instructions);
        isaInstructions.addAll(cv.visitAllInstructions(instructions));
        return isaInstructions;
    }
    public void executeISAcode(List< InstructionNode > instructions,int noiseCoefficient){
         ISAInterpreter isaIP = new ISAInterpreter();

         for(String i:isaIP.getProgramOutput(instructions,noiseCoefficient)){
//             System.out.println("Final results are");
             System.out.println(i);
         }

    }
    public static void main(String[] args){
        List< IRNode > instructions = new LinkedList<>();
        RunTests runTests = new RunTests();
        //Specify the test parameters
        int noiseCoefficient = 10;
        int scalingFactor = 25000;
        int bitWidth = 64;
        int N = 20;
        int M = 30;
        //Uncomment to run the test for the dotProduct
        //Specify the register name for the final result to be stored
//        Variable output = new Variable("output");
//        runTests.runDotProduct(N,output,bitWidth,scalingFactor,noiseCoefficient);


        //Uncomment to run the test for matrix marix multiply
        //Specify the register names for the final results to be stored
        List<Variable> output = new LinkedList <>();
        for(int i=0; i<N;i++){
            output.add(new Variable("output_"+i));
        }
        runTests.runMatMatMult(N,M,output,scalingFactor,bitWidth,noiseCoefficient);

        runTests.runConv();
    }
}
