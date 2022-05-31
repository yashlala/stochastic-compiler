package ISAInterpreter;

import ISA.Registers.Register;
import ISAInterpreter.Registers.BinaryRegister;

import java.util.Map;

public class RegisterFile {
    // TODO: Let's just worry about all this later.
    public void saveVar(Register register) {
    }

    public BinaryRegister getBinaryReg(String name) {
        // TODO Do proper lookup
        return null;
    }

    public BinaryRegister getBinaryReg(ISA.Registers.BinaryRegister register)
    {
        return this.getBinaryReg(register.getName());
    }

    public RegisterFile setBinaryReg(BinaryRegister register)
    {
        // TODO: Implement all this
        return this;
    }

    public RegisterFile clear() {
        // Clear all internal state
        return this;
    }
}
