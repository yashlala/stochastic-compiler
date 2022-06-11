package IRworkLoads;

import IR.IRNodes.*;
import IR.Literals.Literal;
import IR.Variables.Variable;
import jdk.nashorn.internal.ir.ForNode;
import jdk.nashorn.internal.ir.LoopNode;

import java.util.LinkedList;
import java.util.List;

public
class DotProduct {
    public
    Variable calculateDotProduct(List<Variable> input1, List<Variable> input2){
        List < IRNode > instructions = new LinkedList <>();
        //Note: there is no error checking, I asusme input1.size() == input2.size()
        //Also, first list needs to be called input1 the other needs to be called input2

        SetLiteral intializeIter = new SetLiteral(new Variable("LoopIter"),new Literal(0.0));
        SetLiteral endIter = new SetLiteral(new Variable("LoopIter"),new Literal(input1.size()));
        Variable acc = new Variable("acc");
        Variable temp = new Variable("temp");
        Variable src1 = new Variable("src1");
        Variable src2 = new Variable("src2");

        //initialize accumulate variables once
        SetLiteral initAcc = new SetLiteral(new Variable("acc"),new Literal(0.0));

        SetLiteral increment = new SetLiteral(new Variable("increment"),new Literal(8.0));
        Add addToInput1 = new Add(new Variable("input1"),new Variable("input1"),new Variable("increment"));
        Add addToInput2 = new Add(new Variable("input2"),new Variable("input2"),new Variable("increment"));
        Load load1 = new Load(src1,new Variable("input1"));
        Load load2 = new Load(src2,new Variable("input2"));

        Multiply multiply = new Multiply(temp,src1,src2);
//        ForLoop forloop = new ForLoop(new Variable("LoopIter"),intializeIter,endIter,)
        for(int i=0; i<input1.size(); i++){





        }
    }
}
