package IR;

import IR.IRNodes.*;
import IR.Labels.Label;
import IR.Variables.Variable;
import IR.Visitors.PrintVisitor;

import java.util.LinkedList;
import java.util.List;

public class IRNodeTests {
    public static void main(String[] args) {
        List<IRNode> instructions = new LinkedList<>();
        int counter = 0;
        List<IRNode> loop = new LinkedList<>();

        Add add = new Add(generateVariable(++counter), generateVariable(++counter), generateVariable(++counter));
        loop.add(add);
        Divide div = new Divide(generateVariable(++counter), generateVariable(++counter), generateVariable(++counter));
        loop.add(div);
        Equals eq = new Equals(generateVariable(++counter), generateVariable(++counter), generateVariable(++counter));
        loop.add(eq);
        ForLoop forLoop = new ForLoop(generateVariable(++counter), generateVariable(++counter),
                generateVariable(++counter), loop);
        instructions.add(forLoop);

        Goto go = new Goto(generateLabel(++counter));
        instructions.add(go);

        IfNotEquals ifne = new IfNotEquals(generateVariable(++counter) ,generateVariable(++counter), generateLabel(++counter));
        instructions.add(ifne);

        IfZero ifz = new IfZero(generateVariable(++counter), generateLabel(++counter));
        instructions.add(ifz);

        LabelNode labelNode = new LabelNode(generateLabel(++counter));
        instructions.add(labelNode);

        LessThan lt = new LessThan(generateVariable(++counter), generateVariable(++counter), generateVariable(++counter));
        instructions.add(lt);

        Load load = new Load(generateVariable(++counter), generateVariable(++counter));
        instructions.add(load);

        Multiply mul = new Multiply(generateVariable(++counter), generateVariable(++counter), generateVariable(++counter));
        instructions.add(mul);

        Print p = new Print(generateVariable(++counter));
        instructions.add(p);

        Store store = new Store(generateVariable(++counter), generateVariable(++counter));
        instructions.add(store);

        Subtract sub = new Subtract(generateVariable(++counter), generateVariable(++counter), generateVariable(++counter));
        instructions.add(sub);

        System.out.println("------------------------PURE PRINT---------------------");
        instructions.forEach(System.out::println);
        System.out.println("------------------------VISITOR PRINT---------------------");
        PrintVisitor visitor = new PrintVisitor();
        visitor.visitAllInstructions(instructions);
    }

    public static Variable generateVariable(int c) {
        return new Variable(Integer.toString(c));
    }

    public static Label generateLabel(int c) {
        return new Label(Integer.toString(c));
    }
}
