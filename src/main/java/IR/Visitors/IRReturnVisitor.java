package IR.Visitors;

import IR.IRNodes.*;

import java.util.List;

public interface IRReturnVisitor<R> {
    R visitAllInstructions(List<IRNode> instructions);

    R visit(ForLoop forLoop);

    R visit(Add add);

    R visit(Subtract subtract);

    R visit(Multiply multiply);

    R visit(Divide divide);

    R visit(Equals equals);

    R visit(LessThan lessThan);

    R visit(IfZero ifZero);

    R visit(LabelNode labelNode);

    R visit(IfNotEquals ifNotEquals);

    R visit(Goto aGoto);

    R visit(Print print);

    R visit(Load load);

    R visit(Store store);

    R visit(SetLiteral setLiteral);
}
