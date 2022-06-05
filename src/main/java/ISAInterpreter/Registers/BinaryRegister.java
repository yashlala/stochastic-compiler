package ISAInterpreter.Registers;

import ISA.Memory.MemoryAddress;
import ISAInterpreter.MemoryBank;
import ISAInterpreter.RegisterFile;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class BinaryRegister implements Register {
    private final String name;
    private double value;

    public BinaryRegister(String name, double value) {
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
        this.value = value;
    }

    @Override
    public String toString() {
        return name + " = " + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryRegister that = (BinaryRegister) o;
        return getName().equals(that.getName())
                && Math.abs(getValue() - that.getValue()) < 0.001;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
