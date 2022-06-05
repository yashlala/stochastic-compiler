package ISAInterpreter;

import ISAInterpreter.Registers.Register;
import ISAInterpreter.Registers.BinaryRegister;
import ISAInterpreter.Registers.StochasticRegister;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegisterFileTest {
    @Test
    void binaryRegs() {
        RegisterFile rf = new RegisterFile();
        BinaryRegister r1 = new BinaryRegister("alpha", 10);
        BinaryRegister r2 = new BinaryRegister("beta", 20);
        rf.putReg(r1);
        rf.putReg(r2);
        assertEquals(r1, rf.getBinaryReg("alpha"));
    }
    @Test
    void genericRegs() {
        RegisterFile rf = new RegisterFile();
        Register r1 = new BinaryRegister("alpha", 10);
        Register r2 = new StochasticRegister("beta", 0.4, 20);
        rf.putReg(r1);
        rf.putReg(r2);
        assertEquals(r2, rf.getReg("beta"));
    }
}