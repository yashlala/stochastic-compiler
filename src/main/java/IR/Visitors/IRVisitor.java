package IR.Visitors;

import IR.IRNodes.*;

import java.util.Collection;

public interface IRVisitor {
    void visitAllInstructions(Collection<IRNode> instructions);

    void visit(ForLoop forLoop);

    void visit(Add add);

    void visit(Subtract subtract);

    void visit(Multiply multiply);

    void visit(Divide divide);

    void visit(Equals equals);

    void visit(LessThan lessThan);

    void visit(IfZero ifZero);

    void visit(LabelNode labelNode);

    void visit(IfNotEquals ifNotEquals);

    void visit(Goto aGoto);

    void visit(Print print);

    void visit(Load load);

    void visit(Store store);
}
