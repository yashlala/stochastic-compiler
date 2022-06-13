package RegSelector;
import java.util.*;
import IR.IRNodes.*;
import IR.Labels.Label;
import IR.Literals.Literal;
import IR.Variables.Variable;
import IR.Visitors.RegisterCollectorVisitor;

public
class Selector {
     int count = 0;
    public Set<Variable> collectAllRegisters (List<IRNode> instructions){
        RegisterCollectorVisitor rcv = new RegisterCollectorVisitor();
        List<Variable> registers = new LinkedList<>();
        Set<Variable> hashSet = new HashSet <>();
        registers.addAll(rcv.visitAllInstructions(instructions));
        hashSet.addAll(registers);
        return hashSet;
    }
    public
    Set < Variable > returnBinaryRegisters ( List < IRNode > instructions, Boolean ifBasic, Boolean ifAdvanced, Integer  advanced ) {
        Set < Variable > results = new HashSet <>();
        BasicFilters basicFilters = new BasicFilters();
        results = basicFilters.allBasicFilters(instructions);
//        if(ifBasic){
//            return results;
//        }
        if(ifAdvanced){

            AdvancedFilters advancedFilters = new AdvancedFilters();
            results.addAll(advancedFilters.conditionalOriginFilter(instructions));
            switch (advanced){
                case 1:
                    results.addAll( advancedFilters.conditionalOriginFilter(instructions));
                    break;
                case 2:
                    results.addAll( advancedFilters.conditionalOriginFilter(instructions));
                    results.addAll( advancedFilters.loopOriginFilter(instructions,ifBasic,ifAdvanced,advanced));
                    break;
                case 3:
                    results.addAll( advancedFilters.conditionalOriginFilter(instructions));
                    results.addAll( advancedFilters.loopOriginFilter(instructions,ifBasic,ifAdvanced,advanced));
                    results.addAll(advancedFilters.loadStoreOriginFilter(instructions));
                    break;

                case 4:
                    results.addAll( advancedFilters.conditionalOriginFilter(instructions));
                    results.addAll( advancedFilters.loopOriginFilter(instructions,ifBasic,ifAdvanced,advanced));
                    results.addAll(advancedFilters.loadStoreOriginFilter(instructions));
                    results.addAll(advancedFilters.divideOriginFilter(instructions));
                    break;
                case 5:

                    results.addAll(advancedFilters.applyAllOriginFilters(instructions,ifBasic,ifAdvanced,advanced));
                    break;
            }
        }
        return results;


    }
    public Set < Variable > returnStochRegisters(Set<Variable>binaryRegs, List<IRNode> instructions){
        HashSet<Variable> result =(HashSet) this.collectAllRegisters(instructions);
//        System.out.println("all the registers:");
//        System.out.println(result);
        result.removeAll(binaryRegs);
        return result;
    }



    public  Variable generateVariable(){
       return new Variable("variable"+(count++));
    }

    public
    List < IRNode > generateTest (){
        List < IRNode > instructions = new LinkedList <>();

        Label label1 = new Label("Label1");
        Label label2 = new Label("Label2");
        instructions.add(new Add(new Variable("r6"), new Variable("r5") , new Variable("r66")));
        instructions.add(new Add(new Variable("r7"), new Variable("r5") , new Variable("r6")));
        instructions.add(new Subtract(new Variable("v_in"), new Variable("r5ee") , new Variable("r6ee")));
        instructions.add(new SetLiteral(new Variable("r3"),new Literal(2.0)));
        instructions.add(new SetLiteral(new Variable("v1"),new Literal(0.03414977043447387)));
        instructions.add(new Store(new Variable("v1"),new Variable("v1")));
        instructions.add(new Add(new Variable("v6"), new Variable("r8") , new Variable("r7")));
        instructions.add(new Add(new Variable("v4"), new Variable("r1") , new Variable("r2")));
        instructions.add(new IfZero(new Variable("r7"), label1));
        List <IRNode> loopContent = new LinkedList<IRNode>(Arrays.asList(new Add(generateVariable(),generateVariable(),generateVariable())));
        loopContent.add(new IfZero(new Variable("v_in"),label2));
        instructions.add(new ForLoop(new Variable("iter"), new Variable("v4"), new Variable("r6"),loopContent));

//        instructions.add(new SetLiteral(new Variable("r3"),new Literal(2.0)));
//        instructions.add(new SetLiteral(new Variable("v1"),new Literal(0.03414977043447387)));
//        instructions.add(new SetLiteral(new Variable("v2"),new Literal(0.03414977043447387)));
//        instructions.add(new SetLiteral(new Variable("r1"),new Literal(1.0)));
//        instructions.add(new SetLiteral(new Variable("r2"),new Literal(2.0)));
//        instructions.add(new Store(new Variable("r1"),new Variable("v1")));
//        instructions.add(new Store(new Variable("r2"),new Variable("v2")));
//        instructions.add(new Load(new Variable("dest1"), new Variable("r1")));
//        instructions.add(new Load(new Variable("dest2"), new Variable("r2")));
//        instructions.add(new Add(new Variable("temp"),new Variable("dest1"),new Variable("dest2")));
//        instructions.add(new Print(new Variable("dest1")));
//
//        instructions.add(new Add(new Variable("r1"),new Variable("r2"), new Variable("r3")));
//        instructions.add(new Add(generateVariable(),generateVariable(),generateVariable()));
//        instructions.add(new Subtract(generateVariable(),generateVariable(),generateVariable()));
//        instructions.add(new Load(generateVariable(),generateVariable()));
//        instructions.add(new Store(generateVariable(),generateVariable()));
//        instructions.add(new IfNotEquals(generateVariable(),generateVariable(),label1));
//        instructions.add(new LessThan(generateVariable(),generateVariable(),generateVariable()));
//        instructions.add(new Divide(generateVariable(),generateVariable(),generateVariable()));
//        instructions.add(new IfZero(generateVariable(),label1));
//        instructions.add(new Goto(label2));
//        List <IRNode> loopContent = new LinkedList<IRNode>(Arrays.asList(new Add(generateVariable(),generateVariable(),generateVariable())));
//        instructions.add(new ForLoop(generateVariable(),generateVariable(),generateVariable(), loopContent));
//        instructions.add(new Divide(generateVariable(),generateVariable(),generateVariable()));


        System.out.println("\n******* Beginning Of Test Code ********\n");
        for (IRNode element : instructions) {
            System.out.println(element);
        }
        System.out.println("\n******* End Of Test Code ********\n");
        return instructions;
    }


    public static
    void main ( String[] args ) {
        Selector rs = new Selector();
//        rs.collectAllRegisters(generateTest());
        List<IRNode> instructions = rs.generateTest();
        Set result = rs.returnStochRegisters(rs.returnBinaryRegisters(instructions,true,true,5),instructions);

        System.out.println("stochastic registers:");
        System.out.println(result);


//
//        this.collectAllRegisters(generateTest());
//        System.out.println("all the registers:");
//        System.out.println(registers);
//        System.out.println(registers.size());
//       System.out.println( new Selector().returnBinaryRegisters(generateTest(), false,true,2));
    }

}


