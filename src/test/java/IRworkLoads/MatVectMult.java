package IRworkLoads;

import IR.IRNodes.IRNode;
import IR.Variables.Variable;

import java.util.LinkedList;
import  java.util.List;

public
class MatVectMult {
    public List< IRNode >calculateMatVectMult ( Integer N, Integer M, List< Variable > output){
        List<IRNode> results = new LinkedList <>();
        DotProduct dotProduct = new DotProduct();
        for(int i=0; i<N; i++){
            results.addAll(dotProduct.calculateDotProduct(M,output.get(i),i*M));
        }
        return results;

    }
    // uncomment for local testing
//    public static void main(String[] args){
//        List <Variable> res = new LinkedList <>();
//        for(int i=0; i<2; i++)res.add(new Variable("res_"+i));
//        for(IRNode i: new MatVectMult().calculateMatVectMult(2,3,res)){
//            System.out.println(i);
//        }
//
//    }

}
