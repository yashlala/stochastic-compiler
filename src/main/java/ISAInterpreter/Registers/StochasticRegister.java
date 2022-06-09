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
    public static final double NOISE_COEFFICIENT = 0;

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

        //bitSet property is set to our encode method, which returns a bitset class
        this.bitSet = encode(value, frameSize, polarity);

//        System.out.println(this.bitSet);
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

//        System.out.println(src1.toDouble() + " src1");
//        System.out.println(src2.toDouble() + " src2");

        double destVal = (src1.toDouble() * scaleVal) + ((1 - scaleVal) * src2.toDouble());
//        System.out.println(destVal + " destVal");
        checkAssignValue(dest, destVal);

        addNoise(dest.bitSet, dest.getFrameSize(), NOISE_COEFFICIENT);
    }

    public static void subtract(StochasticRegister dest, StochasticRegister src1,
                                StochasticRegister src2, Register scale) {
        double scaleVal = scale.toDouble();
        double destVal = (src1.toDouble() * scaleVal) - ((1 - scaleVal) * src2.toDouble());
        checkAssignValue(dest, destVal);
        addNoise(dest.bitSet, dest.getFrameSize(), NOISE_COEFFICIENT);
    }

    public static void multiply(StochasticRegister dest, StochasticRegister src1, StochasticRegister src2) {
        double destVal = (src1.toDouble() * src2.toDouble());
        checkAssignValue(dest, destVal);
        addNoise(dest.bitSet, dest.getFrameSize(), NOISE_COEFFICIENT);
    }

    public static void divide(StochasticRegister dest, StochasticRegister src1,
                              StochasticRegister src2, Register scale) {
        double scaleVal = scale.toDouble();
        double destVal = src1.toDouble() / src2.toDouble() * scaleVal;
        saturateAssignValue(dest, destVal);
        addNoise(dest.bitSet, dest.getFrameSize(), NOISE_COEFFICIENT);
    }

    public static void exp(StochasticRegister dest, StochasticRegister src) {
        double destVal = Math.exp(src.toDouble());
        checkAssignValue(dest, destVal);
        addNoise(dest.bitSet, dest.getFrameSize(), NOISE_COEFFICIENT);
    }

    public static void tanh(StochasticRegister dest, StochasticRegister src) {
        double destVal = Math.tanh(src.toDouble());
        checkAssignValue(dest, destVal);
        addNoise(dest.bitSet, dest.getFrameSize(), NOISE_COEFFICIENT);
    }

    private static void checkAssignValue(StochasticRegister register, double value) {
        if (!inRange(value, register.getPolarity())) {
            throw new RuntimeException("Register " + register.getName() + " value out of range");
        }
//        System.out.println(value);
//        System.out.println(register.getFrameSize());
//        System.out.println(register.getPolarity());



        register.bitSet = encode(value, register.getFrameSize(), register.getPolarity());
//        System.out.println(register.bitSet);
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
            throw new RuntimeException("Cannot encode stochastic bitstream; value out of range");
        }

        double fractionOfOnes;
        if (polarity == RegisterPolarity.UNIPOLAR) {
            fractionOfOnes = value;
        } else if (polarity == RegisterPolarity.BIPOLAR) {
            // (value + 1) / 2
            // so value of -1 corresponds to 0/10 ones set
            // value of 0.4 corresponds to 7/10 ones set
            // value of 1 corresponds to 10/10 ones set

            // value of 0.25 would then correspond to 1.25/2 = 0.625 (will give this value exactly)

            // value of 0.25 corresponds to 1.25/2 = .625
            // 2 * (.6) - 1 = .2
            // 2 * (.7) - 1 = .4

            // value of -0.25 corresponds to .75/2 = .375
            // 2 * (.3) - 1 = -.4
            // 2 * (.4) - 1 = -.2

            // value of -0.31 corresponds to .345
            // rounding down works appropriately here for negatives
            // therefore we can simply round the magnitude of the fraction

            // value of .3 corresponds to .65
            // 2 * .6 - 1 = .2
            // 2 * . = .4

            fractionOfOnes = (value + 1) / 2;
        } else {
            throw new RuntimeException("Unknown Polarity Type Encountered");
        }

        // As of 2022-06-06, we're not actually simulating stochastic bit operations.
        // Instead, we decode into binary, perform floating operations at full precision,
        // then reencode into stochastic form. Noise terms are explicitly introduced later.
        // As such, it doesn't...really matter that we implement this right.

        ArrayList<Integer> free = new ArrayList<Integer>();
        for (int i = 0; i < frameSize; i++) {
            free.add(i);
        }
        // free will be 1,2,3... frameSize

        //Creating an array list of set
        ArrayList<Integer> set = new ArrayList<Integer>();

        ThreadLocalRandom random = ThreadLocalRandom.current();
//        System.out.println(fractionOfOnes * frameSize);
//        System.out.println(fractionOfOnes);
//        System.out.println(frameSize);

        // KEY IDEA we cannot round the fraction since we do not know the frame size
        // EX: If we have 20 as frame size, then it does not make sense to round first decimal
        // We need to round the computed fraction * frameSize

        while (set.size() < Math.round(fractionOfOnes * frameSize)) {
            set.add(free.remove(random.nextInt(free.size())));

            // adding to set, removing from free a random int bounded by the size of free
        }

        // then set is a BitSet with the position of ones described by set
//        System.out.println(set);
        BitSet ret = new BitSet(frameSize);
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
            return (2 * fractionOfOnes) - 1;
        } else {
            throw new RuntimeException("Unknown Polarity Type Encountered");
        }
    }

    private static void addNoise(BitSet bitSet, int frameSize, double noiseCoefficient) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i=0; i < Math.ceil(noiseCoefficient * frameSize); i++) {
            bitSet.flip(random.nextInt(0, frameSize));
        }
    }
}
