package ISA;

import ISA.Registers.BinaryRegister;
import ISA.Registers.RegisterPolarity;
import ISA.Registers.StochasticRegister;

public class Testing {
    public static void main(String[] args) {
        BinaryRegister testBinaryReg = new BinaryRegister("testBinary");
        StochasticRegister testUnipolar = new StochasticRegister("unipolar", RegisterPolarity.UNIPOLAR, 190);
        StochasticRegister testBipolar = new StochasticRegister("bipolar", RegisterPolarity.BIPOLAR, 1247);

        System.out.println(testBinaryReg);
        System.out.println(testUnipolar);
        System.out.println(testBipolar);
    }
}
