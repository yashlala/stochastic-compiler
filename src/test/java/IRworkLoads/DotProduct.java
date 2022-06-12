package IRworkLoads;

import IR.IRNodes.*;
import IR.Literals.Literal;
import IR.Variables.Variable;


import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public
class DotProduct {
    public
    List<IRNode> calculateDotProduct(Integer N,Variable output, int index){
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

        //uncomment to see intialization code process
//        System.out.println("********** Generated initialization code :**********");
//        for(IRNode i:results)System.out.println(i);
//        System.out.println("********** End of Code **********\n");

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
        Store store_result = new Store(output,accum);
        results.add(store_result);
        return results;

    }

    List<IRNode> init_variables(Double[] input_1, Double[] input_2,Integer N, int index){
        List < IRNode > results = new LinkedList <>();
        Random rand = new Random();
        for(int i=index; i<N+index; i++){
        double d1 = rand.nextDouble();
        double d2 = rand.nextDouble();

        Literal input1_raw = new Literal(d1);
        Literal input2_raw = new Literal(d2);

        Variable input1 = new Variable("x_"+i);
        Variable input2 = new Variable("y_"+i);
        SetLiteral setinput1 = new SetLiteral(input1,input1_raw);
        SetLiteral setinput2 = new SetLiteral(input2,input2_raw);

        Variable addr1 = new Variable("addr_x_"+i);
        Variable addr2 = new Variable("addr_y_"+i);
        input_1[i-index]=d1;
        input_2[i-index]=d2;

        Store s1  = new Store(addr1,input1);
        Store s2  = new Store(addr2,input2);

        results.add(setinput1);
        results.add(s1);
        results.add(setinput2);
        results.add(s2);

        }
        System.out.println("Correct dot product is: ");
        System.out.println(dotProductCheck(input_1, input_2));
        return results;
    }
    public Double dotProductCheck(Double[] a, Double[] b){
        double sum = 0;
        int n = a.length;
        for (int i = 0; i < n; i++)
        {
            sum += a[i] * b[i];
        }
        return sum;
    }


    //uncomment for local testing
//    public static void main(String[] args){
//        System.out.println(new DotProduct().calculateDotProduct(4,new Variable("output"),0));
//    }

}
