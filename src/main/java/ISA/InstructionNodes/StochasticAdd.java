package ISA.InstructionNodes;

import ISA.Registers.BinaryRegister;
import ISA.Registers.Register;
import ISA.Registers.StochasticRegister;
import ISA.Visitors.ISAVisitor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StochasticAdd implements InstructionNode {
    @NonNull
    private final StochasticRegister dest;
    @NonNull
    private final StochasticRegister src1;
    @NonNull
    private final StochasticRegister src2;
    @NonNull
    private final Register scale;

    @Override
    public void accept(ISAVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "add " + dest + ", " + src1 + ", " + src2 + ", " + scale;
    }
}
