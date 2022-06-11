package RegSelector;
import java.util.*;
import IR.IRNodes.*;
import IR.Labels.Label;
import IR.Variables.Variable;
import org.omg.PortableServer.AdapterActivator;

public
class Selector {
    static  Integer count = 0;
    public
    Set < Variable > returnBinaryRegisters ( List < IRNode > instructions, Boolean ifBasic, Boolean ifAdvanced, Integer  advanced ) {
        Set < Variable > results = new HashSet <>();


        BasicFilters basicFilters = new BasicFilters();
        results = basicFilters.allBasicFilters(instructions);
        if(ifBasic){
            return results;
        }
        if(ifAdvanced){
            AdvancedFilters advancedFilters = new AdvancedFilters();
            switch (advanced){
                case 1:
                    results.addAll(advancedFilters.otherOriginFilter(instructions));
                case 2:
                   results.addAll( advancedFilters.conditionalOriginFilter(instructions));
                   break;

                case 3:
                    results.addAll(advancedFilters.otherOriginFilter(instructions));
                    break;
            }
        }
        return results;

//Uncomment below code for local testing
//        results = basicFilters.conditionalsFilter(generateTest());
//        results.addAll(basicFilters.loadStoreFilter((generateTest())));
//        results.addAll(basicFilters.loopFilter((generateTest())));
//        results.addAll(basicFilters.divideFilter((generateTest())));
//        System.out.println("Filter Result:");
//        System.out.println(results);

    }

    public static
    void main ( String[] args ) {

       System.out.println( new Selector().returnBinaryRegisters(generateTest(), false,true,2));
    }

    public static Variable generateVariable(){
       return new Variable("variable"+(count++));
    }

    public static
    List < IRNode > generateTest (){
        List < IRNode > instructions = new LinkedList <>();

        Label label1 = new Label("Label1");
        instructions.add(new Add(generateVariable(),generateVariable(),generateVariable()));
        instructions.add(new Subtract(generateVariable(),generateVariable(),generateVariable()));
        instructions.add(new Load(generateVariable(),generateVariable()));
        instructions.add(new Store(generateVariable(),generateVariable()));
        instructions.add(new IfNotEquals(generateVariable(),generateVariable(),label1));
        instructions.add(new LessThan(generateVariable(),generateVariable(),generateVariable()));
        instructions.add(new Divide(generateVariable(),generateVariable(),generateVariable()));
        instructions.add(new IfZero(generateVariable(),label1));
        List <IRNode> loopContent = new LinkedList<IRNode>(Arrays.asList(new Add(generateVariable(),generateVariable(),generateVariable())));
        instructions.add(new ForLoop(generateVariable(),generateVariable(),generateVariable(), loopContent));
        instructions.add(new Divide(generateVariable(),generateVariable(),generateVariable()));


        System.out.println("\n******* Beginning Of Test Code ********\n");
        for (IRNode element : instructions) {
            System.out.println(element);
        }
        System.out.println("\n******* End Of Test Code ********\n");
        return instructions;
    }



}


