package ISAInterpreter;

import ISAInterpreter.Registers.BinaryRegister;
import ISAInterpreter.Registers.Register;
import ISAInterpreter.Registers.StochasticRegister;

import java.util.HashMap;
import java.util.Map;


public class RegisterFile {
    private static final String zeroRegName = "zero";

    private final Map<String, BinaryRegister> binaryStore = new HashMap<>();
    private final Map<String, StochasticRegister> stochasticStore = new HashMap<>();

    public Register getReg(String name) {
        if (name.equals(zeroRegName)) {
            return new BinaryRegister("zero", 0);
        }
        if (binaryStore.containsKey(name)) {
            return binaryStore.get(name);
        } else {
            return stochasticStore.get(name);
        }
    }

    public Register getReg(ISA.Registers.Register register) {
        return getReg(register.getName());
    }

    public BinaryRegister getBinaryReg(String name) {
        if (name.equals(zeroRegName)) {
            return new BinaryRegister(zeroRegName, 0);
        }
        return new BinaryRegister(binaryStore.get(name));
    }

    public BinaryRegister getBinaryReg(ISA.Registers.BinaryRegister register) {
        return getBinaryReg(register.getName());
    }

    public StochasticRegister getStochasticReg(String name) {
        if (name.equals(zeroRegName)) {
            return new StochasticRegister(zeroRegName, 0, 0);
        }
        return new StochasticRegister(stochasticStore.get(name));
    }

    public StochasticRegister getStochasticReg(ISA.Registers.StochasticRegister register) {
        return getStochasticReg(register.getName());
    }

    public void putReg(Register register) {
        register.putAccept(this);
    }

    public void putReg(BinaryRegister register) {
        if (register.getName().equals(zeroRegName)) {
            return;
        }
        binaryStore.put(register.getName(), new BinaryRegister(register));
    }

    public void putReg(StochasticRegister register) {
        if (register.getName().equals(zeroRegName)) {
            return;
        }
        stochasticStore.put(register.getName(), new StochasticRegister((register)));
    }

    public void clear() {
        binaryStore.clear();
        stochasticStore.clear();
    }
}
