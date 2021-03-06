package ISA;

import ISA.InstructionNodes.*;
import ISA.Registers.BinaryRegister;
import ISA.Registers.RegisterPolarity;
import ISA.Registers.StochasticRegister;
import ISA.Visitors.PrintVisitor;

import java.util.LinkedList;
import java.util.List;

public class CombinedISATests {
    public static void main(String[] args) {
        List<InstructionNode> instructions = new LinkedList<>();
        System.out.println("--------------------PURE PRINTS------------------");

        int counter = 0;

        PrintIns pBin = new PrintIns(new BinaryRegister("pBinReg"));
        System.out.println(pBin);
        instructions.add(pBin);

        PrintIns pU = new PrintIns(new StochasticRegister("pUReg", RegisterPolarity.UNIPOLAR, ++counter));
        System.out.println(pU);
        instructions.add(pU);

        PrintIns pB = new PrintIns(new StochasticRegister("pBReg", RegisterPolarity.BIPOLAR, ++counter));
        System.out.println(pB);
        instructions.add(pB);

        LoadIns lBin = new LoadIns(new BinaryRegister("lBinReg"), new BinaryRegister("lMemLoc"));
        System.out.println(lBin);
        instructions.add(lBin);

        StoreIns sBin = new StoreIns(new BinaryRegister("sBinReg"), new BinaryRegister("sMemLoc"));
        System.out.println(sBin);
        instructions.add(sBin);

        LessThan lt = new LessThan(new BinaryRegister("ltDest"), new BinaryRegister("ltSrc1"),
                new BinaryRegister("ltSrc2"));
        System.out.println(lt);
        instructions.add(lt);

        System.out.println("--------------------VISITOR PRINTS------------------");
        PrintVisitor visitor = new PrintVisitor();
        visitor.visitAllInstructions(instructions);
    }
}
