package RegSelector;

import IR.IRNodes.*;
import IR.Variables.Variable;
import IR.Visitors.TypeVisitor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public
class BasicFilters {
    public
    Set < Variable > conditionalsFilter ( List < IRNode > instructions ){
        TypeVisitor tv = new TypeVisitor();
        Set < Variable > results = new HashSet <>();
        for(IRNode i: instructions){
            switch (i.accept(tv)){
                case "LessThan":
                    results.add( ((LessThan)i).getSrc1());
                    results.add( ((LessThan)i).getSrc2());
                    results.add( ((LessThan)i).getDest());
                    break;
                case "Equals":
                    results.add( ((Equals)i).getSrc1());
                    results.add( ((Equals)i).getSrc2());
                    results.add( ((Equals)i).getDest());
                    break;
                case "IfZero":
                    //TODO: confirm if condition is a register or actual condition
                    results.add( ((IfZero)i).getCondition());
                    break;

                case "IfNotEquals":
                    results.add( ((IfNotEquals)i).getCond1());
                    results.add( ((IfNotEquals)i).getCond2());
                    break;
            }
        }
        return results;
    }

    public
    Set < Variable > loadStoreFilter( List < IRNode > instructions  ){
        TypeVisitor tv = new TypeVisitor();
        Set < Variable > results = new HashSet <>();
        for(IRNode i: instructions){
            if (i.accept(tv).equals("Load") ){
                results.add( ((Load)i).getDest());
                results.add( ((Load)i).getAddress());
            }else if(i.accept(tv).equals("Store" )){
                results.add( ((Store)i).getAddress());
                results.add( ((Store)i).getSrc());
            }
        }
        return results;
    }
    public
    Set < Variable > loopFilter( List < IRNode > instructions  ) {
        TypeVisitor tv = new TypeVisitor();
        Set < Variable > results = new HashSet <>();
        for (IRNode i : instructions) {
            if (i.accept(tv).equals("ForLoop")) {
                results.add(((ForLoop) i).getLoopIterVar());
                results.add(((ForLoop) i).getLoopRangeStart());
                results.add(((ForLoop) i).getLoopRangeEnd());
            }
        }
        return results;
    }
        public
        Set < Variable > printFilter( List < IRNode > instructions  ){
            TypeVisitor tv = new TypeVisitor();
            Set < Variable > results = new HashSet <>();
            for (IRNode i : instructions) {
                if (i.accept(tv).equals("Print")) {
                    results.add(((Print) i).getVar());
                }
            }

        return results;
    }
    public
    Set < Variable > divideFilter ( List < IRNode > instructions ) {
        TypeVisitor tv = new TypeVisitor();
        Set < Variable > results = new HashSet <>();
        for (IRNode i : instructions) {
            if (i.accept(tv).equals("Divide")) {
                results.add(((Divide) i).getSrc2());
            }
        }
        return results;
    }

    public
    Set < Variable > allBasicFilters( List < IRNode > instructions  ) {
        Set < Variable > results = new HashSet <>();
        results.addAll(this.loadStoreFilter(instructions));
        results.addAll(this.conditionalsFilter(instructions));
        results.addAll(this.printFilter(instructions));
        results.addAll( this.loopFilter(instructions));
        results.addAll( this.divideFilter(instructions));
        return results;

    }

}
