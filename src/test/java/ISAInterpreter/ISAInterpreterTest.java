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
    void memoryStoreLoad() {
        List<InstructionNode> prg = new ArrayList<InstructionNode>();
        BinaryRegister addr = new BinaryRegister("a");
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
    
    private List<Double> toDoubles(List<String> output) {
        List<Double> ret = new ArrayList<>();
        for (String s : output) {
            String[] parts = s.split(" ");
            ret.add(Double.parseDouble(parts[2]));
        }
        return ret;
    }
}