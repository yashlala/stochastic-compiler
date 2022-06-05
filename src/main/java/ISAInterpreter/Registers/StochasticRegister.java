package ISAInterpreter.Registers;

import ISA.Memory.MemoryAddress;
import ISAInterpreter.MemoryBank;
import ISAInterpreter.RegisterFile;
import lombok.Getter;
import lombok.Setter;

import java.util.BitSet;


public class StochasticRegister implements Register {
    @Getter
    @Setter
    private String name;
    private BitSet bitSet = new BitSet();
    @Getter
    @Setter
    private int frameSize;

    public StochasticRegister(String name, double value, int frameSize) {
        this.name = name;
        // TODO: Implement proper value setting and getting here.
        this.frameSize = frameSize;
    }

    public StochasticRegister(String name) {
        this(name, 0, 0);
    }

    public StochasticRegister(StochasticRegister other) {
        name = other.name;
        frameSize = other.frameSize;
        bitSet = (BitSet) other.bitSet.clone();
    }

    public StochasticRegister(ISA.Registers.StochasticRegister other) {
        this(other.getName(), 0, 0);
    }

    public void assignFrom(StochasticRegister other) {
        // Don't copy the name!
        this.frameSize = other.frameSize;
        this.bitSet = (BitSet) other.bitSet.clone();
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
        return (double) bitSet.cardinality() / frameSize;
    }

    @Override
    public void fromDouble(double value) {
        // TODO Implement proper value setting here!
    }

    @Override
    public String toString() {
        return name + " ~= " + ((double) bitSet.cardinality() / frameSize);
    }

    public int size() {
        return bitSet.size();
    }
}
