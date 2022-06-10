package ISAInterpreter.Registers;

import ISA.Registers.RegisterPolarity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StochasticRegisterTest {
    @Test
    void bipolarMath() {
        // We have for a frame size of 10
        // 2 * x - 1, where x is the fraction of ones
        // Therefore [-1,1] with a precision of 0.2

        StochasticRegister zero = new StochasticRegister("zero", 0, 10, RegisterPolarity.BIPOLAR);
        BinaryRegister scale = new BinaryRegister("scale", 0.5);
        StochasticRegister a = new StochasticRegister("a", 0.5, 10, RegisterPolarity.BIPOLAR);
        StochasticRegister b = new StochasticRegister("b", 0.5, 10, RegisterPolarity.BIPOLAR);
        StochasticRegister out = new StochasticRegister("out", 0, 10, RegisterPolarity.BIPOLAR);

        StochasticRegister.add(out, a, zero, scale, 0);
        assertEquals(0.5 * 0.5, out.toDouble(), 0.25);
    }

    @Test
    void nonTrivialScale() {
        StochasticRegister zero = new StochasticRegister("zero", 0, 20, RegisterPolarity.BIPOLAR);
        BinaryRegister scale = new BinaryRegister("scale", 0.35);
        StochasticRegister a = new StochasticRegister("a", 0.5, 20, RegisterPolarity.BIPOLAR);
        StochasticRegister b = new StochasticRegister("b", 0.5, 20, RegisterPolarity.BIPOLAR);
        StochasticRegister out = new StochasticRegister("out", 0, 20, RegisterPolarity.BIPOLAR);

        StochasticRegister.add(out, a, zero, scale, 0);
        assertEquals(0.35 * 0.5, out.toDouble(), 0.1);

        StochasticRegister.subtract(out, zero, a, scale, 0);
        assertEquals((0 * .35) - ((1 - .35) * .5), out.toDouble(), 0.1);

        StochasticRegister.divide(out, a, b, scale, 0);
        assertEquals(0.35 * 0.5 / 0.5, out.toDouble(), 0.1);
    }
    @Test
    void bipolarMathPrecise() {
        StochasticRegister zero = new StochasticRegister("zero", 0, 20, RegisterPolarity.BIPOLAR);
        BinaryRegister scale = new BinaryRegister("scale", 0.5);
        StochasticRegister a = new StochasticRegister("a", 0.5, 20, RegisterPolarity.BIPOLAR);
        StochasticRegister b = new StochasticRegister("b", 0.5, 20, RegisterPolarity.BIPOLAR);
        StochasticRegister out = new StochasticRegister("out", 0, 20, RegisterPolarity.BIPOLAR);

        StochasticRegister.add(out, a, zero, scale, 0);
        assertEquals(0.5 * 0.5, out.toDouble(), 0.1);

        StochasticRegister.subtract(out, zero, a, scale, 0);
        assertEquals((0 - 0.5) * 0.5, out.toDouble(), 0.1);

        StochasticRegister.multiply(out, a, b, 0);
        assertEquals(0.5 * 0.5, out.toDouble(), 0.1);

        StochasticRegister.divide(out, a, b, scale, 0);
        assertEquals(0.5 * 0.5 / 0.5, out.toDouble(), 0.1);
    }

    @Test
    void mixedMathPrecise() {
        StochasticRegister zero = new StochasticRegister("zero", 0, 20, RegisterPolarity.UNIPOLAR);
        BinaryRegister scale = new BinaryRegister("scale", 0.5);
        StochasticRegister a = new StochasticRegister("a", 0.5, 20, RegisterPolarity.BIPOLAR);
        StochasticRegister b = new StochasticRegister("b", 0.5, 20, RegisterPolarity.UNIPOLAR);
        StochasticRegister out = new StochasticRegister("out", 0, 20, RegisterPolarity.BIPOLAR);

        StochasticRegister.add(out, a, zero, scale, 0);
        assertEquals(0.5 * 0.5, out.toDouble(), 0.1);

        StochasticRegister.subtract(out, zero, a, scale, 0);
        assertEquals((0 - 0.5) * 0.5, out.toDouble(), 0.1);

        StochasticRegister.multiply(out, a, b, 0);
        assertEquals(0.5 * 0.5, out.toDouble(), 0.1);

        StochasticRegister.divide(out, a, b, scale, 0);
        assertEquals(0.5 * 0.5 / 0.5, out.toDouble(), 0.1);

        zero = new StochasticRegister("zero", 0, 20, RegisterPolarity.BIPOLAR);
        scale = new BinaryRegister("scale", 0.5);
        a = new StochasticRegister("a", 0.5, 20, RegisterPolarity.UNIPOLAR);
        b = new StochasticRegister("b", 0.5, 20, RegisterPolarity.BIPOLAR);
        out = new StochasticRegister("out", 0, 20, RegisterPolarity.BIPOLAR);

        StochasticRegister.add(out, a, zero, scale, 0);
        assertEquals(0.5 * 0.5, out.toDouble(), 0.1);

        StochasticRegister.subtract(out, zero, a, scale, 0);
        assertEquals((0 - 0.5) * 0.5, out.toDouble(), 0.1);

        StochasticRegister.multiply(out, a, b, 0);
        assertEquals(0.5 * 0.5, out.toDouble(), 0.1);

        StochasticRegister.divide(out, a, b, scale, 0);
        assertEquals(0.5 * 0.5 / 0.5, out.toDouble(), 0.1);
    }

    @Test
    void unevenFrameSizes() {
        StochasticRegister zero = new StochasticRegister("zero", 0, 40, RegisterPolarity.UNIPOLAR);
        BinaryRegister scale = new BinaryRegister("scale", 0.5);
        StochasticRegister a = new StochasticRegister("a", 0.5, 20, RegisterPolarity.UNIPOLAR);
        StochasticRegister b = new StochasticRegister("b", 0.5, 23, RegisterPolarity.UNIPOLAR);

        // Note OUT Reg must be bipolar
        StochasticRegister out = new StochasticRegister("out", 0, 11, RegisterPolarity.BIPOLAR);

        StochasticRegister.add(out, a, zero, scale, 0);
        assertEquals(0.5 * 0.5, out.toDouble(), 0.1);

        StochasticRegister.subtract(out, zero, a, scale, 0);
        assertEquals((0 - 0.5) * 0.5, out.toDouble(), 0.1);

        StochasticRegister.multiply(out, a, b, 0);
        assertEquals(0.5 * 0.5, out.toDouble(), 0.1);

        StochasticRegister.divide(out, a, b, scale, 0);
        assertEquals(0.5 * 0.5 / 0.5, out.toDouble(), 0.1);
    }
}
