package IR.Visitors;

import IR.IRNodes.*;

import java.util.List;

public class TypeVisitor implements IRReturnVisitor<String>{
    @Override
    public String visitAllInstructions(List<IRNode> instructions) {
        return "ALL_INST";
    }

    @Override
    public String visit(ForLoop forLoop) {
        return "ForLoop";
    }

    @Override
    public String visit(Add add) {
        return "Add";
    }

    @Override
    public String visit(Subtract subtract) {
        return "Subtract";
    }

    @Override
    public String visit(Multiply multiply) {
        return "Multiply";
    }

    @Override
    public String visit(Divide divide) {
        return "Divide";
    }

    @Override
    public String visit(Equals equals) {
        return "Equals";
    }

    @Override
    public String visit(LessThan lessThan) {
        return "LessThan";
    }

    @Override
    public String visit(IfZero ifZero) {
        return "IfZero";
    }

    @Override
    public String visit(LabelNode labelNode) {
        return "LabelNode";
    }

    @Override
    public String visit(IfNotEquals ifNotEquals) {
        return "IfNotEquals";
    }

    @Override
    public String visit(Goto aGoto) {
        return "aGoto";
    }

    @Override
    public String visit(Print print) {
        return "Print";
    }

    @Override
    public String visit(Load load) {
        return "Load";
    }

    @Override
    public String visit(Store store) {
        return "Store";
    }

    @Override
    public String visit(SetLiteral setLiteral) {
        return "SetLiteral";
    }
}