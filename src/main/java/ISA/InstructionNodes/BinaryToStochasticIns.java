package ISA.InstructionNodes;

import ISA.Registers.BinaryRegister;
import ISA.Registers.StochasticRegister;
import ISA.Visitors.ISAVisitor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BinaryToStochasticIns implements InstructionNode {
    @NonNull
    private final StochasticRegister dest;
    @NonNull
    private final BinaryRegister src;

    @Override
    public void accept(ISAVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "b2s " + dest + ", " + src;
    }
}