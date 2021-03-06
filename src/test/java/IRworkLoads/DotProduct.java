package IRworkLoads;

import IR.IRNodes.*;
import IR.Literals.Literal;
import IR.Variables.Variable;


import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public
class DotProduct {
    int counter =1;
    public
    List<IRNode> calculateDotProduct(Integer N, Variable output, int index){
        List < IRNode > results= new LinkedList <>();

        //used to capture random numbers for correctness comparison later
        Double [] input_1 = new Double[N];
        Double [] input_2 = new Double[N];

        //use to accumulate intermediate results
        Variable accum = new Variable("accum");
        SetLiteral init_accum = new SetLiteral(accum,new Literal(0.0));
        results.add(init_accum);
        //initialize variables into memory
        results.addAll(this.init_variables(input_1, input_2, N,index));


        //calculate the dot product
        for(int i=index; i<N+index; i++){
            Variable dest1 = new Variable("dest_x_"+i);
            Variable dest2 = new Variable("dest_y_"+i);
            Variable dest_temp = new Variable("temp");

            Load load_x = new Load(dest1,new Variable("addr_x_"+i));
            Load load_y = new Load(dest2,new Variable("addr_y_"+i));
            Multiply mult = new Multiply(dest_temp,dest1,dest2);
            Add add = new Add(accum,accum, dest_temp);
            results.add(load_x);
            results.add(load_y);
            results.add(mult);
            results.add(add);
        }
        SetLiteral setOutput = new SetLiteral(output,new Literal(counter));
        results.add(setOutput);
        Store store_result = new Store(output,accum);
        results.add(store_result);
        return results;

    }

    List<IRNode> init_variables(Double[] input_1, Double[] input_2,Integer N, int index){
        List < IRNode > results = new LinkedList <>();
        Random rand = new Random();
//        index =0;
        for(int i=index; i<N+index; i++){
        double d1 = this.getRandomDouble(2);
//        double d2 =  Double.parseDouble(String.format("%.5f" ,rand.nextDouble()));
        double d2 = this.getRandomDouble(2);

        Literal input1_raw = new Literal(d1);
        Literal input2_raw = new Literal(d2);

        Variable input1 = new Variable("x_"+i);
        Variable input2 = new Variable("y_"+i);
        SetLiteral setinput1 = new SetLiteral(input1,input1_raw);
        SetLiteral setinput2 = new SetLiteral(input2,input2_raw);

        Variable addr1 = new Variable("addr_x_"+i);
        SetLiteral setliteral1 = new SetLiteral(addr1,new Literal(counter));
        counter++;

        Variable addr2 = new Variable("addr_y_"+i);
        SetLiteral setliteral2 = new SetLiteral(addr2,new Literal(counter));
        counter++;


        input_1[i-index]=d1;
        input_2[i-index]=d2;

        Store s1  = new Store(addr1,input1);
        Store s2  = new Store(addr2,input2);

        results.add(setinput1);
        results.add(setliteral1);
        results.add(s1);
        results.add(setinput2);
        results.add(setliteral2);
        results.add(s2);

        }

        return results;
    }
//    public static List<IRNode> calculateDotProduct(List<Double> x, List<Double> y, Variable output) {
//        if (x.size() != y.size()) {
//            throw new RuntimeException("Unequal list lengths");
//        }
//
//        Variable acc = new Variable("acc");
//        Variable tmp = new Variable("tmp");
//
//        List<IRNode> prg = new ArrayList<>();
//        for (int i=0; i < x.size(); i++) {
//            prg.add(new SetLiteral(new Variable("x_" + i), new Literal(x.get(i))));
//            prg.add(new SetLiteral(new Variable("y_" + i), new Literal(y.get(i))));
//        }
//
//        prg.add(new SetLiteral(acc, new Literal(0)));
//        for (int i=0; i < x.size(); i++) {
//            prg.add(new Multiply(tmp, new Variable("x_" + i), new Variable("y_" + i)));
//            prg.add(new Add(acc, acc, tmp));
//        }
//
//        prg.add(new SetLiteral(tmp, new Literal(0)));
//        prg.add(new Add(output, acc, tmp));
//
//        return prg;
//    }
    public Double dotProductCheck(Double[] a, Double[] b){
        double sum = 0;
        int n = a.length;
        for (int i = 0; i < n; i++)
        {
            sum += a[i] * b[i];
        }
        return sum;
    }
    private Double getRandomDouble( double range) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();


            return (rand.nextDouble() - 0.5) * 2 * range ;

    }
    public static List<IRNode> getDotProductIR(List<Double> x, List<Double> y, Variable output) {
        if (x.size() != y.size()) {
            throw new RuntimeException("Unequal list lengths");
        }

        Variable acc = new Variable("acc");
        Variable tmp = new Variable("tmp");

        List<IRNode> prg = new ArrayList<>();
        for (int i=0; i < x.size(); i++) {
            prg.add(new SetLiteral(new Variable("x_" + i), new Literal(x.get(i))));
            prg.add(new SetLiteral(new Variable("y_" + i), new Literal(y.get(i))));
        }

        prg.add(new SetLiteral(acc, new Literal(0)));
        for (int i=0; i < x.size(); i++) {
            prg.add(new Multiply(tmp, new Variable("x_" + i), new Variable("y_" + i)));
            prg.add(new Add(acc, acc, tmp));
        }

        prg.add(new SetLiteral(tmp, new Literal(0)));
        prg.add(new Add(output, acc, tmp));

        return prg;
    }

    //uncomment for local testing
//    public static void main(String[] args){
//        System.out.println(new DotProduct().calculateDotProduct(4,new Variable("output"),0));
//    }

}
