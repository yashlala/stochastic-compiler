package ISAInterpreter;

import ISA.Memory.MemoryAddress;
import ISAInterpreter.Registers.BinaryRegister;
import ISAInterpreter.Registers.Register;
import ISAInterpreter.Registers.StochasticRegister;

import java.util.HashMap;
import java.util.Map;

public class MemoryBank {
    private final Map<Integer, BinaryRegister> binaryStore = new HashMap<>();
    private final Map<Integer, StochasticRegister> stochasticStore = new HashMap<>();

    public void store(MemoryAddress address, Register register) {
        register.storeAccept(this, address);
    }

    public void store(MemoryAddress address, BinaryRegister register) {
        binaryStore.put(address.getAddress(), new BinaryRegister(register));
    }

    public void store(MemoryAddress address, StochasticRegister register) {
        stochasticStore.put(address.getAddress(), new StochasticRegister(register));
    }

    public void load(MemoryAddress address, Register register) {
        register.loadAccept(this, address);
    }

    public void load(MemoryAddress address, BinaryRegister register) {
        register.assignFrom(binaryStore.get(address.getAddress()));
    }

    public void load(MemoryAddress address, StochasticRegister register) {
        register.assignFrom(stochasticStore.get(address.getAddress()));
    }

    public void clear() {
        binaryStore.clear();
        stochasticStore.clear();
    }
}
