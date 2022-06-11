package RegSelector;

import IR.IRNodes.*;
import IR.Variables.Variable;
import IR.Visitors.TypeVisitor;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public
class AdvancedFilters {


    public
    Set < Variable > conditionalOriginFilter ( List < IRNode > instructions ) {
        TypeVisitor tv = new TypeVisitor();
        Set < Variable > results = new HashSet <>();
        int index = 0;
            for(IRNode i: instructions){
                index++;
                List variableList = new LinkedList<Variable>();
                switch (i.accept(tv)){
                    case "LessThan":
                        variableList.add( ((LessThan)i).getSrc1());
                        variableList.add( ((LessThan)i).getSrc2());
                        variableList.add( ((LessThan)i).getDest());

                       results.addAll( this.findOccurrence(instructions,variableList,index));
                        break;
                    case "Equals":
                        variableList.add( ((Equals)i).getSrc1());
                        variableList.add( ((Equals)i).getSrc2());
                        variableList.add( ((Equals)i).getDest());
                        results.addAll( this.findOccurrence(instructions,variableList,index));
                        break;
                    case "IfZero":
                        //TODO: confirm if condition is a register or actual condition
                        variableList.add( ((IfZero)i).getCondition());
                        results.addAll( this.findOccurrence(instructions,variableList,index));
                        break;

                    case "IfNotEquals":
                        variableList.add ( ((IfNotEquals)i).getCond1());
                        variableList.add( ((IfNotEquals)i).getCond2());
                        results.addAll( this.findOccurrence(instructions,variableList,index));
                        break;
                }
            }

        return results;
    }
    public
    List<IRNode> findOccurrence(List < IRNode > instructions, List<Variable> variables, Integer index){
        TypeVisitor tv = new TypeVisitor();
        List< IRNode > results = new LinkedList <>();
        for(Variable v: variables){
            for (int i = index; i<instructions.size()-1; i++) {
                if (instructions.get(i).accept(tv).equals("Divide")) {
//                results.add(((Divide) i).getSrc2());
                }
            }
        }

        return results;
    }

    public
    Set < Variable > otherOriginFilter ( List < IRNode > instructions ) {
        TypeVisitor tv = new TypeVisitor();
        Set < Variable > results = new HashSet <>();
        return results;
    }
}
