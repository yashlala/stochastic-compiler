package RegSelector;

import IR.IRNodes.*;
import IR.Variables.Variable;
import IR.Visitors.AssignmentVisitor;
import IR.Visitors.SideEffectRegisterVisitor;
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
        Set < Variable > variableList = new HashSet <>();
        int index = 0;
            for(IRNode i: instructions){
                switch (i.accept(tv)){
                    case "LessThan":
                        variableList.addAll( this.recursiveSearchOrigin( ((LessThan)i).getSrc1(),getSublist(0,index,instructions)));
                        variableList.addAll( this.recursiveSearchOrigin( ((LessThan)i).getSrc2(),getSublist(0,index,instructions)));
                        break;
                    case "Equals":
                        variableList.addAll( this.recursiveSearchOrigin( ((Equals)i).getSrc1(),getSublist(0,index,instructions)));
                        variableList.addAll( this.recursiveSearchOrigin( ((Equals)i).getSrc2(),getSublist(0,index,instructions)));
                        break;
                    case "IfZero":
                        //TODO: confirm if condition is a register or actual condition
                        variableList.addAll( this.recursiveSearchOrigin( ((IfZero)i).getCondition(),getSublist(0,index,instructions)));
                        break;
                    case "IfNotEquals":
                        variableList.addAll( this.recursiveSearchOrigin( ((IfNotEquals)i).getCond1(),getSublist(0,index,instructions)));
                        variableList.addAll( this.recursiveSearchOrigin( ((IfNotEquals)i).getCond2(),getSublist(0,index,instructions)));
                        break;
                }
                index++;
            }

        return variableList;
    }
    public
    Set < Variable > loadStoreOriginFilter( List < IRNode > instructions  ){
        TypeVisitor tv = new TypeVisitor();
        Set < Variable > results = new HashSet <>();
        int index = 0;
        for(IRNode i: instructions){
            if (i.accept(tv).equals("Load") ){
                results.addAll( this.recursiveSearchOrigin( ((Load)i).getAddress(),getSublist(0,index,instructions)));

            }else if(i.accept(tv).equals("Store" )){
                results.addAll( this.recursiveSearchOrigin( ((Store)i).getAddress(),getSublist(0,index,instructions)));

            }
            index++;
        }
        return results;
    }
    public
    Set < Variable > loopOriginFilter( List < IRNode > instructions,Boolean ifBasic,Boolean ifAdvanced, int advanced ) {
        TypeVisitor tv = new TypeVisitor();
        Set < Variable > results = new HashSet <>();
        int index=0;
        for (IRNode i : instructions) {
            if (i.accept(tv).equals("ForLoop")) {
                results.addAll( this.recursiveSearchOrigin( ((ForLoop)i).getLoopRangeStart(),getSublist(0,index,instructions)));
                results.addAll( this.recursiveSearchOrigin( ((ForLoop)i).getLoopRangeEnd(),getSublist(0,index,instructions)));
                Selector sl = new Selector();
                List<IRNode> sublist = getSublist(0,index-1,instructions);
                sublist.addAll(((ForLoop) i).getContents());
                    results.addAll(sl.returnBinaryRegisters( sublist,ifBasic,ifAdvanced,advanced ));
            }
            index++;
        }
        return results;
    }
    public
    Set < Variable > printOriginFilter( List < IRNode > instructions  ){
        TypeVisitor tv = new TypeVisitor();
        Set < Variable > results = new HashSet <>();
        int index=0;
        for (IRNode i : instructions) {
            if (i.accept(tv).equals("Print")) {
                results.addAll( this.recursiveSearchOrigin( ((Print)i).getVar(),getSublist(0,index,instructions)));
            }
            index++;
        }

        return results;
    }
    public
    Set < Variable > divideOriginFilter ( List < IRNode > instructions ) {
        TypeVisitor tv = new TypeVisitor();
        Set < Variable > results = new HashSet <>();
        int index=0;
        for (IRNode i : instructions) {
            if (i.accept(tv).equals("Divide")) {
                results.addAll( this.recursiveSearchOrigin( ((Divide)i).getSrc2(),getSublist(0,index,instructions)));
            }
            index++;
        }
        return results;
    }
    public
    Set < Variable > applyAllOriginFilters ( List < IRNode > instructions, Boolean ifBasic, Boolean ifAdvaced, int advanced ) {
        Set < Variable > results = new HashSet <>();
        results.addAll(this.conditionalOriginFilter(instructions));
        results.addAll(this.loopOriginFilter(instructions,ifBasic, ifAdvaced, advanced));
        results.addAll(this.loadStoreOriginFilter(instructions));
        results.addAll(this.divideOriginFilter(instructions));
        results.addAll(this.printOriginFilter(instructions));

        return results;

    }
    public List<IRNode>getSublist(int start, int end, List<IRNode> fullList){
        List<IRNode> subList = new LinkedList <>();
        for (int i = start; i <= end; i++) {
            subList.add(fullList.get(i));
        }
        return  subList;
    }



    public List<Variable> recursiveSearchOrigin(Variable register, List<IRNode> previousInstructions){


            List<Variable> result= new LinkedList <>();
            if(previousInstructions.size() == 0){
                return result;
            }
            SideEffectRegisterVisitor serv  = new SideEffectRegisterVisitor();
            AssignmentVisitor av = new AssignmentVisitor();
            IRNode currentInstr = previousInstructions.get(previousInstructions.size()-1);


            previousInstructions.remove(previousInstructions.size()-1);
            if( currentInstr.accept(av).size()>0 && currentInstr.accept(av).get(0).toString() == register.toString()){

                result.addAll(currentInstr.accept(serv));
                for(Variable i: currentInstr.accept(serv)){
                    result.addAll(recursiveSearchOrigin(i,previousInstructions));
                }

            }else {
                result.addAll(recursiveSearchOrigin(register,previousInstructions));
            }
        return  result;

    }
}
