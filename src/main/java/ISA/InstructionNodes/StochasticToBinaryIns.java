package ISA.InstructionNodes;

import ISA.Registers.BinaryRegister;
import ISA.Registers.StochasticRegister;
import ISA.Visitors.ISAVisitor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StochasticToBinaryIns implements InstructionNode {
    @NonNull
    private final BinaryRegister dest;
    @NonNull
    private final StochasticRegister src;

    @Override
    public void accept(ISAVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "s2b " + dest + ", " + src;
    }
}