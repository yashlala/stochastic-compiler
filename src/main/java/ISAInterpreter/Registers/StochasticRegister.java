package ISAInterpreter.Registers;

import ISA.Memory.MemoryAddress;
import ISAInterpreter.MemoryBank;
import ISAInterpreter.RegisterFile;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;


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
        this.frameSize = frameSize;
        this.bitSet = encode(value, frameSize);
    }

    public StochasticRegister(double value, int frameSize) {
        this("", value, frameSize);
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

    // TODO: NOBODY should be using this! Always copy over the frame size!
    @Override
    public void fromDouble(double value) {
        fromDouble(value, 32);
    }

    public void fromDouble(double value, int frameSize) {
        this.frameSize = frameSize;
        this.bitSet = encode(value, frameSize);
    }

    private BitSet encode(double value, int frameSize) {
        ArrayList<Integer> free = new ArrayList<Integer>();
        ArrayList<Integer> set = new ArrayList<Integer>();
        for (int i=0; i < frameSize; i++) {
            free.add(i);
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        while (set.size() < frameSize) {
            set.add(free.remove(random.nextInt(free.size())));
        }

        BitSet ret = new BitSet();
        for (Integer i : set) {
            ret.set(i);
        }
        return ret;
    }

    @Override
    public String toString() {
        return name + " ~= " + this.toDouble();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StochasticRegister that = (StochasticRegister) o;
        return getName().equals(that.getName())
                && Math.abs(toDouble() - that.toDouble()) < 0.01;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
