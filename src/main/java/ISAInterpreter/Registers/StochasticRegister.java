package ISAInterpreter.Registers;

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

    public StochasticRegister(String name) {
        this.name = name;
        this.frameSize = 0;
    }

    public StochasticRegister(StochasticRegister other) {
        name = other.name;
        bitSet = (BitSet) other.bitSet.clone();
        frameSize = other.frameSize;
    }

    public void copyValue(StochasticRegister other) {
        // Don't copy the name!
        this.bitSet = (BitSet) other.bitSet.clone();
        this.frameSize = other.frameSize;
    }

    @Override
    public double toDouble() {
        return (double) bitSet.cardinality() / frameSize;
    }

    public int size() {
        return bitSet.size();
    }
}
