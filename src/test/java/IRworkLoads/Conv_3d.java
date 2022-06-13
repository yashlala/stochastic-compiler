package IRworkLoads;

import IR.IRNodes.*;
import IR.Literals.Literal;
import IR.Variables.Variable;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public
class Conv_3d {
    //This function initializes the kernel by some random numbers
    int count =1;
    public List<IRNode> initKernel(Integer dim, Double[][][]kernel){
        List<IRNode> results = new LinkedList <>();
        Random rand = new Random();
        for(int i=0; i<dim; i++){
            for(int j=0; j<dim; j++){
                for(int k=0; k<dim; k++){
                    Double entry = rand.nextDouble();
                    kernel[i][j][k]=entry;
                    Variable register = new Variable("kernel_"+i+"_"+j+"_"+k);
                    Variable dest =  new Variable("kernel_addr_"+i+"_"+j+"_"+k);

                    //example: kernel_1_1_1 = 0.99
                    SetLiteral literal = new SetLiteral(register,new Literal(entry));
                    results.add(literal);
                    SetLiteral addressInit = new SetLiteral(dest,new Literal(count));
                    count++;
                    results.add(addressInit);
                    // example: @kernel_addr_1_1_1 = kernel_1_1_1
                    Store store = new Store(dest,register);
                    results.add(store);

                }
            }

        }
        return results;
    }
    //this function initializes all the input memory locations, also stores the random initial values in the input array passed in
    public List<IRNode> initInput(Integer N, Integer M, Integer L, Double [][][]input){
        List<IRNode> results = new LinkedList <>();
        Random rand = new Random();
        for(int i=0; i<N; i++){
            for(int j=0; j<M; j++){
                for(int k=0; k<L; k++){
                    Double entry = rand.nextDouble();
                    input[i][j][j] = entry;

                    //example: input_1_1_1 = 0.99
                    Variable register = new Variable("input_"+i+"_"+j+"_"+k);
                    SetLiteral literal = new SetLiteral(register,new Literal(entry));
                    results.add(literal);

                    Variable dest = new Variable("input_addr_"+i+"_"+j+"_"+k);

                    //initi the input-addr , example: input_addr_1_1_1 =1
                    SetLiteral initInputAddr = new SetLiteral(dest,new Literal(count));
                    count++;
                    results.add(initInputAddr);
                    // example: @input_addr_1_1_1  = input_1_1_1

                    Store store = new Store(dest,register);
                    results.add(store);
                }
            }
        }
        return results;
    }
    //we just need to load the kernel once, as it doesn't change, thus this function performs that task for us
    public List<IRNode> loadKernel(Integer kernelDim){
        List<IRNode> results = new LinkedList <>();
        for(int i = 0; i< kernelDim; i++){
            for(int j=0; j<kernelDim; j++){
                for(int k=0; k<kernelDim; k++){
                    //load the kernel , loaded_kernel_1_1_1 = @kernel_1_1_1
                    Variable addr_ker = new Variable("kernel_addr_"+i+"_"+j+"_"+k);
                    Variable dest_ker= new Variable("loaded_kernel_"+i+"_"+j+"_"+k);
                    Load load_ker = new Load(dest_ker,addr_ker);
                    results.add(load_ker);
                }
            }
        }
        return results;
    }

    //this function computes the convolution for a single sliding window
    public List<IRNode> computeSingleConv(Integer i_index, Integer j_index, Integer k_index, Integer kernelDim, Variable output){
        List<IRNode> results = new LinkedList <>();

        for(int i = i_index; i< kernelDim+i_index; i++){
            for(int j=j_index; j<kernelDim+j_index; j++){
                for(int k=k_index; k<kernelDim+k_index; k++){
                    // load the input, loaded_input_1_1_1 = @input_addr_1_1_1
                    Variable addr = new Variable("input_addr_"+i+"_"+j+"_"+k);
                    Variable dest = new Variable("loaded_input_"+i+"_"+j+"_"+k);
                    Load load = new Load(dest,addr);
                    results.add(load);

                }
            }
        }

        //init the accumulator: accum = 0.0
        SetLiteral literal = new SetLiteral(new Variable("accum"), new Literal(0.0));
        results.add(literal);

        //compute convolution
        for(int i = i_index; i< kernelDim+i_index; i++){
            for(int j=j_index; j<kernelDim+j_index; j++){
                for(int k=k_index; k<kernelDim+k_index; k++){
                        Variable temp_reg = new Variable("temp");
                        Variable src1 = new Variable("loaded_input_"+i+"_"+j+"_"+k);
                        Variable src2= new Variable("loaded_kernel_"+(i-i_index)+"_"+(j-j_index)+"_"+(k-k_index));
                        Multiply multiply = new Multiply(temp_reg,src1,src2);
                        results.add(multiply);
                        Add add = new Add(new Variable("accum"),new Variable("accum"),temp_reg);
                        results.add(add);
                        //set the output variable to the result



                }
            }
        }
        //init the output address, ex: output_1_1_1 = 1
        SetLiteral initOutAddr = new SetLiteral(output,new Literal(count));
        count++;
        results.add(initOutAddr);
        Store store = new Store(output,new Variable("accum"));
        results.add(store);
    return  results;
    }
    //This code is used to run the convolution across all the dimension of the input array
    public List<IRNode> computeAllConv(Integer N, Integer M, Integer L, Integer kernelDim, Variable[][][]output){
        List<IRNode> results = new LinkedList <>();
        Conv_3d conv_3d = new Conv_3d();
        for(int i=0; i+kernelDim <= N; i++) {;
            for (int j = 0; j+kernelDim <= M; j++) {
                for (int k = 0; k +kernelDim <= L; k++) {
                    results.addAll(conv_3d.computeSingleConv(i,j, k, kernelDim,output[i][j][k]));
                }

            }
        }
        return results;
    }
    //This function calls init functions and the convolution functions to produce the final IR code
    public List<IRNode> fullConvolution(Integer N, Integer M, Integer L, Integer kernelDim, Variable[][][]output){
        Conv_3d conv_3d = new Conv_3d();
        List <IRNode> results = new LinkedList <>();
        //these arrays are used to capture the randomly generated input and kernel , for later correctness comparison
        Double[][][] kernel = new Double[kernelDim][kernelDim][kernelDim];
        Double[][][] input = new Double[N][M][L];

        //initialize the kernel and input matrices
        results.addAll(conv_3d.initKernel(2,kernel));
        results.addAll(conv_3d.initInput(N,M,L,input));
        //load in the kernel
        results.addAll(conv_3d.loadKernel(kernelDim));
        //compute the convolution
        results.addAll(conv_3d.computeAllConv(N,M,L,kernelDim,output));

        return  results;
    }


    // uncomment for local testing
//    public static void main(String[] args) {
//        List < Variable > res = new LinkedList <>();
//        Integer N = 3;
//        Integer M = 3;
//        Integer L = 3;
//        Integer kernelDim = 2;
//
//        Variable[][][] output = new Variable[N - kernelDim + 1][M - kernelDim + 1][L - kernelDim + 1];
//        for (int i = 0; i < N - kernelDim + 1; i++) {
//            for (int j = 0; j < M - kernelDim + 1; j++) {
//                for (int k = 0; k < L - kernelDim + 1; k++) {
//                    output[i][j][k]=new Variable("output_" + i + "_" + j + "_" + k);
//                }
//            }
//        }


//        for (IRNode i : new Conv_3d().fullConvolution(N, M, L, kernelDim, output)) {
//            System.out.println(i);
//        }
 //   }

//    public Double convolutionReferenceImplentation(Double[][][]input, Double[][][] kernel, Double[][]][]output){
//
//    }

}
