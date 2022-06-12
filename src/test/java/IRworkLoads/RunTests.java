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
    public void runDotProduct(int N, Variable v, int noiseCoefficient, int bitWidth,int scalingFactor ){
        List< IRNode > instructions = new LinkedList<>();
        DotProduct dotProduct = new DotProduct();
        instructions.addAll( dotProduct.calculateDotProduct(N,v,0));
        instructions.add(new Print(v));
        Selector selector = new Selector();
        ImmutableSet<Variable> stochVars = ImmutableSet.copyOf(selector.collectAllRegisters(instructions));
//        System.out.println("IR code");
//        for(IRNode i: instructions){
//            System.out.println(i);
//        }
//        instructions = selector.generateTest();
        List< InstructionNode > isaInstructions= this.generateISAcode(instructions,scalingFactor,bitWidth, stochVars);
//        System.out.println("\n\n ISA instructions \n\n");
//        for(InstructionNode i: isaInstructions){
//            System.out.println(i);
//        }

        this.executeISAcode(isaInstructions,noiseCoefficient);
    }
    public  void runMatVectMult(){
        List< IRNode > instructions = new LinkedList<>();
        MatVectMult matVectMult = new MatVectMult();
    }
    public void runMatMatMult(Integer N, Integer M){
        List< IRNode > instructions = new LinkedList<>();
        MatMult matMult = new MatMult();
//        instructions = matMult.calculateMatMult(N,M,new Variable("output"));
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
        int noiseCoefficient = 10;
        int scalingFactor = 5;
        int bitWidth = 64;
        int N = 2;
        Variable output = new Variable("output");
        runTests.runDotProduct(N,output,noiseCoefficient,bitWidth,scalingFactor);

        runTests.runConv();
    }
}
