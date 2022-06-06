package ISAInterpreter.Registers;

import ISA.Registers.RegisterPolarity;
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
    private RegisterPolarity polarity;
    @Getter
    @Setter
    private int frameSize;

    public StochasticRegister(String name, double value, int frameSize, RegisterPolarity polarity) {
        this.name = name;
        this.frameSize = frameSize;
        this.polarity = polarity;
        this.bitSet = encode(value, frameSize, polarity);
    }

    public StochasticRegister(String name, double value, int frameSize) {
        this(name, value, frameSize, RegisterPolarity.BIPOLAR);
    }

    public StochasticRegister(double value, int frameSize) {
        this("", value, frameSize);
    }

    public StochasticRegister(ISA.Registers.StochasticRegister other) {
        this(other.getName(), 0, other.getWidth(), other.getPolarity());
    }

    public StochasticRegister(StochasticRegister other) {
        name = other.name;
        this.assignFrom(other);
    }

    public static void add(StochasticRegister dest, StochasticRegister src1,
                           StochasticRegister src2, Register scale) {
        double scaleVal = scale.toDouble();
        double destVal = (src1.toDouble() * scaleVal) + ((1 - scaleVal) * src2.toDouble());
        checkAssignValue(dest, destVal);
    }

    public static void subtract(StochasticRegister dest, StochasticRegister src1,
                                StochasticRegister src2, Register scale) {
        double scaleVal = scale.toDouble();
        double destVal = (src1.toDouble() * scaleVal) - ((1 - scaleVal) * src2.toDouble());
        checkAssignValue(dest, destVal);
    }

    public static void multiply(StochasticRegister dest, StochasticRegister src1, StochasticRegister src2) {
        double destVal = (src1.toDouble() * src2.toDouble());
        checkAssignValue(dest, destVal);
    }

    public static void divide(StochasticRegister dest, StochasticRegister src1,
                              StochasticRegister src2, Register scale) {
        double scaleVal = scale.toDouble();
        double destVal = src1.toDouble() / src2.toDouble() * scaleVal;
        saturateAssignValue(dest, destVal);
    }

    public static void exp(StochasticRegister dest, StochasticRegister src) {
        double destVal = Math.exp(src.toDouble());
        checkAssignValue(dest, destVal);
    }

    public static void tanh(StochasticRegister dest, StochasticRegister src) {
        double destVal = Math.tanh(src.toDouble());
        checkAssignValue(dest, destVal);
    }

    private static void checkAssignValue(StochasticRegister register, double value) {
        if (!inRange(value, register.getPolarity())) {
            throw new RuntimeException("Register " + register.getName() + " value out of range");
        }
        register.bitSet = encode(value, register.getFrameSize(), register.getPolarity());
    }

    private static void saturateAssignValue(StochasticRegister register, double value) {
        switch (register.getPolarity()) {
            case BIPOLAR:
                value = Math.max(value, -1);
                value = Math.min(value, 1);
                break;
            case UNIPOLAR:
                value = Math.max(value, 0);
                value = Math.min(value, 1);
                break;
            default:
                throw new RuntimeException("Unknown register polarity encountered");
        }
        register.bitSet = encode(value, register.getFrameSize(), register.getPolarity());
    }

    private static boolean inRange(double value, RegisterPolarity polarity) {
        switch (polarity) {
            case BIPOLAR:
                return -1 <= value && value <= 1;
            case UNIPOLAR:
                return 0 <= value && value <= 1;
            default:
                throw new RuntimeException("Unknown register polarity encountered");
        }
    }

    // Copy the "data" of another stochastic register.
    public void assignFrom(StochasticRegister other) {
        this.frameSize = other.frameSize;
        this.polarity = other.polarity;
        this.bitSet = (BitSet) other.bitSet.clone();
    }

    @Override
    public double toDouble() {
        return decode(bitSet, frameSize, polarity);
    }

    // Be careful when you call this function! It doesn't adjust width and polarity.
    @Override
    public void fromDouble(double value) {
        this.bitSet = encode(value, this.getFrameSize(), this.getPolarity());
    }

    public void fromDouble(double value, int frameSize, RegisterPolarity polarity) {
        this.frameSize = frameSize;
        this.polarity = polarity;
        this.bitSet = encode(value, frameSize, polarity);
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
                && Math.abs(toDouble() - that.toDouble()) < 0.01
                && this.getPolarity() == that.getPolarity();
    }

    // HELPER METHODS

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public void loadAccept(MemoryBank memoryBank, int address) {
        memoryBank.load(address, this);
    }

    @Override
    public void storeAccept(MemoryBank memoryBank, int address) {
        memoryBank.store(address, this);
    }

    @Override
    public void putAccept(RegisterFile registerFile) {
        registerFile.putReg(this);
    }


    private static BitSet encode(double value, int frameSize, RegisterPolarity polarity) {
        if (!inRange(value, polarity)) {
            throw new RuntimeException("Cannot encode stochastic bistream; value out of range");
        }

        double fractionOfOnes;
        if (polarity == RegisterPolarity.UNIPOLAR) {
            fractionOfOnes = value;
        } else if (polarity == RegisterPolarity.BIPOLAR) {
            fractionOfOnes = 2 * value - 1;
        } else {
            throw new RuntimeException("Unknown Polarity Type Encountered");
        }

        // As of 2022-06-06, we're not actually simulating stochastic bit operations.
        // Instead, we decode into binary, perform floating operations at full precision,
        // then reencode into stochastic form. Noise terms are explicitly introduced later.
        // As such, it doesn't...really matter that we implement this right.

        ArrayList<Integer> free = new ArrayList<Integer>();
        ArrayList<Integer> set = new ArrayList<Integer>();
        for (int i = 0; i < frameSize; i++) {
            free.add(i);
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        while (set.size() < fractionOfOnes * frameSize) {
            set.add(free.remove(random.nextInt(free.size())));
        }

        BitSet ret = new BitSet();
        for (Integer i : set) {
            ret.set(i);
        }
        return ret;
    }

    private double decode(BitSet bitSet, int frameSize, RegisterPolarity polarity) {
        double fractionOfOnes = (double) bitSet.cardinality() / frameSize;
        if (polarity == RegisterPolarity.UNIPOLAR) {
            return fractionOfOnes;
        } else if (polarity == RegisterPolarity.BIPOLAR) {
            return (fractionOfOnes + 1) / 2;
        } else {
            throw new RuntimeException("Unknown Polarity Type Encountered");
        }
    }
}
