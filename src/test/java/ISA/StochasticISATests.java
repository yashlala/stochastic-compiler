package ISA;

import ISA.InstructionNodes.*;
import ISA.Labels.Label;
import ISA.Registers.BinaryRegister;
import ISA.Registers.RegisterPolarity;
import ISA.Registers.StochasticRegister;
import ISA.Visitors.PrintVisitor;

import java.util.LinkedList;
import java.util.List;

public class StochasticISATests {
    public static void main(String[] args) {
        int counter = 0;
        List<InstructionNode> instructions = new LinkedList<>();
        System.out.println("--------------------PURE PRINTS------------------");

        StochasticAdd sadd = new StochasticAdd(new StochasticRegister("saddDest", getPolarity(counter), ++counter),
                new StochasticRegister("saddSrc1", getPolarity(counter), ++counter),
                new StochasticRegister("saddSrc2", getPolarity(counter), ++counter));
        System.out.println(sadd);
        instructions.add(sadd);

        StochasticDiv sdiv = new StochasticDiv(new StochasticRegister("sdivDest", getPolarity(counter), ++counter),
                new StochasticRegister("sdivSrc1", getPolarity(counter), ++counter),
                new StochasticRegister("sdivSrc2", getPolarity(counter), ++counter));
        System.out.println(sdiv);
        instructions.add(sdiv);

        StochasticExp sexp = new StochasticExp(new StochasticRegister("sexpDest", getPolarity(counter), ++counter),
                new StochasticRegister("sexpSrc1", getPolarity(counter), ++counter));
        System.out.println(sexp);
        instructions.add(sexp);

        StochasticMul smul = new StochasticMul(new StochasticRegister("smulDest", getPolarity(counter), ++counter),
                new StochasticRegister("smulSrc1", getPolarity(counter), ++counter),
                new StochasticRegister("smulSrc2", getPolarity(counter), ++counter));
        System.out.println(smul);
        instructions.add(smul);

        StochasticSub ssub = new StochasticSub(new StochasticRegister("ssubDest", getPolarity(counter), ++counter),
                new StochasticRegister("ssubSrc1", getPolarity(counter), ++counter),
                new StochasticRegister("ssubSrc2", getPolarity(counter), ++counter));
        System.out.println(ssub);
        instructions.add(ssub);

        StochasticTanh stan = new StochasticTanh(new StochasticRegister("stanDest", getPolarity(counter), ++counter),
                new StochasticRegister("stanSrc1", getPolarity(counter), ++counter));
        System.out.println(stan);
        instructions.add(stan);

        System.out.println();
        System.out.println("--------------------VISITOR PRINTS------------------");
        PrintVisitor visitor = new PrintVisitor();
        visitor.visitAllInstructions(instructions);
    }

    public static RegisterPolarity getPolarity(int c) {
        if (c%2 == 0) {
            return RegisterPolarity.UNIPOLAR;
        } else {
            return RegisterPolarity.BIPOLAR;
        }
    }
}
