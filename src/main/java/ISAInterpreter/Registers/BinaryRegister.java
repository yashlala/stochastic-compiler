package ISAInterpreter.Registers;

import ISA.Memory.MemoryAddress;
import ISAInterpreter.MemoryBank;
import ISAInterpreter.RegisterFile;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BinaryRegister implements Register {
    private final String name;
    private int value;

    public BinaryRegister(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public BinaryRegister(String name) {
        this(name, 0);
    }

    public BinaryRegister(ISA.Registers.BinaryRegister other) {
        this(other.getName(), 0);
    }

    public BinaryRegister(BinaryRegister other) {
        this(other.getName(), other.getValue());
    }

    public void assignFrom(BinaryRegister other) {
        value = other.getValue();
    }

    @Override
    public void loadAccept(MemoryBank memoryBank, MemoryAddress address) {
        memoryBank.load(address, this);
    }

    @Override
    public void storeAccept(MemoryBank memoryBank, MemoryAddress address) {
        memoryBank.store(address, this);
    }

    @Override
    public void putAccept(RegisterFile registerFile) {
        registerFile.putReg(this);
    }

    @Override
    public double toDouble() {
        return value;
    }

    @Override
    public void fromDouble(double value) {
        this.setValue((int) Math.round(value));
    }
}
