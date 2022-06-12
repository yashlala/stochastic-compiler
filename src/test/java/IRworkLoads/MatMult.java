package IRworkLoads;

import IR.IRNodes.IRNode;
import IR.Variables.Variable;

import java.util.*;

public
class MatMult {
    public List< IRNode > calculateMatMult( Integer N, Integer M, List<Variable> out ){
        DotProduct dotProduct = new DotProduct();

        List<IRNode> results = new LinkedList <>();

        //calculate the product
        for(int i=0; i<N; i++){
            results.addAll(dotProduct.calculateDotProduct(M,out.get(i),i*M));
        }
        return results;
    }

   // uncomment for local testing
//    public static void main(String[] args){
//        List <Variable> res = new LinkedList <>();
//        for(int i=0; i<2; i++)res.add(new Variable("res_"+i));
//        for(IRNode i: new MatMult().calculateMatMult(2,3,res)){
//            System.out.println(i);
//        }
//
//    }
}
