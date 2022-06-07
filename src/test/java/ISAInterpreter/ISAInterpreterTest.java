package ISAInterpreter;

import IR.IRNodes.Print;
import ISA.InstructionNodes.*;
import ISA.Literals.Literal;
import ISA.Registers.BinaryRegister;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ISAInterpreterTest {
    @Test
    void emptyProgram() {
        List<InstructionNode> prg = new ArrayList<InstructionNode>();
        List<String> out = ISAInterpreter.getProgramOutput(prg);
        assertEquals(out.size(), 0);
    }

    @Test
    void registerPrint() {
        List<InstructionNode> prg = new ArrayList<InstructionNode>();
        BinaryRegister a = new BinaryRegister("a");
        prg.add(new LoadLiteralIns(a, new Literal(3)));
        prg.add(new PrintIns(a));

        List<Double> out = toDoubles(ISAInterpreter.getProgramOutput(prg));
        assertEquals(out.size(), 1);
        assertEquals(3, out.get(0), 0.001);
    }

    @Test
    void registerOverwrite() {
        List<InstructionNode> prg = new ArrayList<InstructionNode>();
        BinaryRegister a = new BinaryRegister("a");
        prg.add(new LoadLiteralIns(a, new Literal(3)));
        prg.add(new LoadLiteralIns(a, new Literal(4)));
        prg.add(new PrintIns(a));

        List<Double> out = toDoubles(ISAInterpreter.getProgramOutput(prg));
        assertEquals(1, out.size());
        assertEquals(4, out.get(0), 0.001);
    }

    @Test
    void zeroRegister() {
        List<InstructionNode> prg = new ArrayList<InstructionNode>();
        BinaryRegister zero = new BinaryRegister("zero");
        prg.add(new PrintIns(zero));
        prg.add(new LoadLiteralIns(zero, new Literal(3)));
        prg.add(new PrintIns(zero));

        List<Double> out = toDoubles(ISAInterpreter.getProgramOutput(prg));
        assertEquals(0, out.get(0), 0.001);
        assertEquals(0, out.get(1), 0.001);
    }

    @Test
    void memoryStoreLoad() {
        List<InstructionNode> prg = new ArrayList<InstructionNode>();
        BinaryRegister addr = new BinaryRegister("addr");
        BinaryRegister val = new BinaryRegister("val");
        prg.add(new LoadLiteralIns(addr, new Literal(100)));
        prg.add(new LoadLiteralIns(val, new Literal(3)));
        prg.add(new PrintIns(val));
        prg.add(new StoreIns(val, addr));
        prg.add(new LoadLiteralIns(val, new Literal(4)));
        prg.add(new LoadIns(val, addr));
        prg.add(new PrintIns(val));

        List<Double> out = toDoubles(ISAInterpreter.getProgramOutput(prg));
        assertEquals(out.size(), 2);
        assertEquals(3, out.get(0));
        assertEquals(3, out.get(1));
    }

    @Test
    void binaryArithmetic() {
        List<InstructionNode> prg = new ArrayList<InstructionNode>();
        BinaryRegister a = new BinaryRegister("a");
        BinaryRegister b = new BinaryRegister("b");
        BinaryRegister c = new BinaryRegister("c");

        prg.add(new LoadLiteralIns(a, new Literal(1)));
        prg.add(new LoadLiteralIns(b, new Literal(2)));
        prg.add(new LoadLiteralIns(c, new Literal(0)));

        // 1 + 2 = 3
        prg.add(new BinaryAdd(c, a, b));
        prg.add(new PrintIns(c));

        // 1 - 2 = -1
        prg.add(new BinarySub(c, a, b));
        prg.add(new PrintIns(c));

        // 1 / 2 = 0.5. We reuse the same register here!
        prg.add(new BinaryDiv(a, a, b));
        prg.add(new PrintIns(a));

        // 0.5 * 2 = 1
        prg.add(new BinaryMul(c, a, b));
        prg.add(new PrintIns(c));

        // (2 < 1 ? 1 : 0) = 0
        prg.add(new LessThan(a, b, c));
        prg.add(new PrintIns(a));

        // (0 < 2 ? 1 : 0) = 1
        prg.add(new LessThan(c, a, b));
        prg.add(new PrintIns(c));

        List<Double> out = toDoubles(ISAInterpreter.getProgramOutput(prg));
        assertEquals(3, out.get(0), 0.001);
        assertEquals(-1, out.get(1), 0.001);
        assertEquals(0.5, out.get(2), 0.001);
        assertEquals(1, out.get(3), 0.001);
        assertEquals(0, out.get(4), 0.001);
        assertEquals(1, out.get(5), 0.001);
    }

    @Test


    private List<Double> toDoubles(List<String> output) {
        List<Double> ret = new ArrayList<>();
        for (String s : output) {
            String[] parts = s.split(" ");
            ret.add(Double.parseDouble(parts[2]));
        }
        return ret;
    }
}