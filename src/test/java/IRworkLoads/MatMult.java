package IRworkLoads;

import IR.IRNodes.IRNode;
import IR.Variables.Variable;

import java.util.*;

public
class MatMult {
    public List< IRNode > calculateMatMult( Integer N, Integer M, List<Variable> out ){
        DotProduct dotProduct = new DotProduct();
//        Double[][] input_1 = new Double[N][M];
//        Double[][] input_2 = new Double[M][N];
        List<IRNode> results = new LinkedList <>();

        //initialize both input arrays
//        for(int i=0; i<N; i++){
//            System.out.println("m");
//            System.out.println(M);
//
//            //extract columns of input_2 array
//            Double[] input_2_col = new Double[M];
//            System.out.println(input_2_col.length);
//            for(int j=0; j<M; j++){
//                input_2_col[j] = input_2[j][i];
//            }
//            results.addAll( dotProduct.init_variables(input_1[i],input_2_col,M,i*N));
//        }
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
