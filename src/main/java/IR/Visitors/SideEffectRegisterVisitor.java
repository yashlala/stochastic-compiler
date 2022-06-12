package IR.Visitors;

import IR.IRNodes.*;
import IR.Variables.Variable;


import java.util.LinkedList;
import java.util.List;

public
class SideEffectRegisterVisitor implements IRReturnVisitor<List<Variable>> {

    @Override
    public List<Variable> visitAllInstructions( List < IRNode > instructions) {
        List<Variable> result = new LinkedList <>();
        for(IRNode i: instructions){
            result.addAll(i.accept(this));
        }
        return result;
    }

    @Override
    public List<Variable> visit( ForLoop forLoop) {
        List<Variable> result = new LinkedList <>();
        ;
//        result.add(forLoop.getLoopRangeStart());
//        result.add(forLoop.getLoopRangeEnd());
        RegisterCollectorVisitor rcv = new RegisterCollectorVisitor();
        for(IRNode i: forLoop.getContents()){
            System.out.println(i.accept(rcv));

            result.addAll( i.accept(rcv));
        }
        return  result;
    }

    @Override
    public List<Variable> visit( Add add) {
        List<Variable> result = new LinkedList <>();
        result.add(add.getSrc1());
        result.add(add.getSrc2());
        return  result;
    }

    @Override
    public List<Variable> visit( Subtract subtract) {
        List<Variable> result = new LinkedList <>();
        result.add(subtract.getSrc1());
        result.add(subtract.getSrc2());
        return  result;
    }

    @Override
    public List<Variable> visit( Multiply multiply) {
        List<Variable> result = new LinkedList <>();
        result.add(multiply.getSrc1());
        result.add(multiply.getSrc2());
        return  result;
    }

    @Override
    public List<Variable> visit(Divide divide) {
        List<Variable> result = new LinkedList <>();
        result.add(divide.getSrc1());
        result.add(divide.getSrc2());
        return  result;
    }

    @Override
    public List<Variable> visit(Equals equals) {
        List<Variable> result = new LinkedList <>();
        result.add(equals.getSrc1());
        result.add(equals.getSrc2());
        return  result;
    }

    @Override
    public List<Variable> visit(LessThan lessThan) {
        List<Variable> result = new LinkedList <>();
        result.add(lessThan.getSrc1());
        result.add(lessThan.getSrc2());
        return  result;
    }

    @Override
    public List<Variable> visit(IfZero ifZero) {
        List<Variable> result = new LinkedList <>();
        return  result;
    }

    @Override
    public List<Variable> visit(LabelNode labelNode) {
        List<Variable> result = new LinkedList <>();
        return  result;
    }

    @Override
    public List<Variable> visit(IfNotEquals ifNotEquals) {
        List<Variable> result = new LinkedList <>();
        return  result;
    }

    @Override
    public List<Variable> visit(Goto aGoto) {
        List<Variable> result = new LinkedList <>();
        return  result;
    }

    @Override
    public List<Variable> visit(Print print) {
        List<Variable> result = new LinkedList <>();
        return  result;
    }

    @Override
    public List<Variable> visit(Load load) {
        List<Variable> result = new LinkedList <>();
        result.add(load.getAddress());
        return  result;
    }

    @Override
    public List<Variable> visit(Store store) {
        List<Variable> result = new LinkedList <>();
        result.add(store.getSrc());
        return  result;
    }

    @Override
    public List<Variable> visit(SetLiteral setLiteral) {
        List<Variable> result = new LinkedList <>();
        return  result;
    }
}