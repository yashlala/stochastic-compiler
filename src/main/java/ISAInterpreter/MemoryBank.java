package ISAInterpreter;

import ISAInterpreter.Registers.BinaryRegister;
import ISAInterpreter.Registers.Register;
import ISAInterpreter.Registers.StochasticRegister;

import java.util.HashMap;
import java.util.Map;

public class MemoryBank {
    private final Map<Integer, BinaryRegister> binaryStore = new HashMap<>();
    private final Map<Integer, StochasticRegister> stochasticStore = new HashMap<>();

    public void store(int address, Register register) {
        register.storeAccept(this, address);
    }

    public void store(int address, BinaryRegister register) {
        binaryStore.put(address, new BinaryRegister(register));
    }

    public void store(int address, StochasticRegister register) {
        stochasticStore.put(address, new StochasticRegister(register));
    }

    public void load(int address, Register register) {
        register.loadAccept(this, address);
    }

    public void load(int address, BinaryRegister register) {
        register.assignFrom(binaryStore.get(address));
    }

    public void load(int address, StochasticRegister register) {
        register.assignFrom(stochasticStore.get(address));
    }

    public void clear() {
        binaryStore.clear();
        stochasticStore.clear();
    }
}
