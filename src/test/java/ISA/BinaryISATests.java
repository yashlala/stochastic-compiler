package ISA;

import ISA.InstructionNodes.*;
import ISA.Labels.Label;
import ISA.Registers.BinaryRegister;
import ISA.Visitors.PrintVisitor;

import java.util.LinkedList;
import java.util.List;

public class BinaryISATests {
    public static void main(String[] args) {
        List<InstructionNode> instructions = new LinkedList<>();
        System.out.println("--------------------PURE PRINTS------------------");
        BinaryAdd add = new BinaryAdd(new BinaryRegister("addDest"), new BinaryRegister("addSrc1"),
                new BinaryRegister("addSrc2"));
        System.out.println(add);
        instructions.add(add);

        BinaryDiv div = new BinaryDiv(new BinaryRegister("divDest"), new BinaryRegister("divSrc1"),
                new BinaryRegister("divSrc2"));
        System.out.println(div);
        instructions.add(div);

        BinaryEq eq = new BinaryEq(new BinaryRegister("eqDest"), new BinaryRegister("eqSrc1"),
                new BinaryRegister("eqSrc2"));
        System.out.println(eq);
        instructions.add(eq);

        BinaryJump j = new BinaryJump(new Label("jlabel"), new BinaryRegister("jCond"));
        System.out.println(j);
        instructions.add(j);

        BinaryLessThan lt = new BinaryLessThan(new BinaryRegister("ltDest"), new BinaryRegister("ltSrc1"),
                new BinaryRegister("ltSrc2"));
        System.out.println(lt);
        instructions.add(lt);

        BinaryMul mul = new BinaryMul(new BinaryRegister("mulDest"), new BinaryRegister("mulSrc1"),
                new BinaryRegister("mulSrc2"));
        System.out.println(mul);
        instructions.add(mul);

        BinarySub sub = new BinarySub(new BinaryRegister("subDest"), new BinaryRegister("subSrc1"),
                new BinaryRegister("subSrc2"));
        System.out.println(sub);
        instructions.add(sub);

        LabelNode labelNode = new LabelNode(new Label("labelLabel"));
        System.out.println(labelNode);
        instructions.add(labelNode);

        System.out.println();
        System.out.println("--------------------VISITOR PRINTS------------------");
        PrintVisitor visitor = new PrintVisitor();
        visitor.visitAllInstructions(instructions);
    }
}
