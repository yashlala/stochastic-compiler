package IR.Visitors;

import IR.IRNodes.*;

import java.util.List;

public class PrintVisitor implements IRVisitor{
    @Override
    public void visitAllInstructions(List<IRNode> instructions) {
        instructions.forEach(System.out::println);
    }

    @Override
    public void visit(ForLoop forLoop) {
        System.out.println(forLoop);
    }

    @Override
    public void visit(Add add) {
        System.out.println(add);
    }

    @Override
    public void visit(Subtract subtract) {
        System.out.println(subtract);
    }

    @Override
    public void visit(Multiply multiply) {
        System.out.println(multiply);
    }

    @Override
    public void visit(Divide divide) {
        System.out.println(divide);
    }

    @Override
    public void visit(Equals equals) {
        System.out.println(equals);
    }

    @Override
    public void visit(LessThan lessThan) {
        System.out.println(lessThan);
    }

    @Override
    public void visit(IfZero ifZero) {
        System.out.println(ifZero);
    }

    @Override
    public void visit(LabelNode labelNode) {
        System.out.println(labelNode);
    }

    @Override
    public void visit(IfNotEquals ifNotEquals) {
        System.out.println(ifNotEquals);
    }

    @Override
    public void visit(Goto aGoto) {
        System.out.println(aGoto);
    }

    @Override
    public void visit(Print print) {
        System.out.println(print);
    }

    @Override
    public void visit(Load load) {
        System.out.println(load);
    }

    @Override
    public void visit(Store store) {
        System.out.println(store);
    }
}
