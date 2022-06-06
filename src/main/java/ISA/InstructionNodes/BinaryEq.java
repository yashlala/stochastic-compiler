package ISA.InstructionNodes;

import ISA.Registers.BinaryRegister;
import ISA.Visitors.ISAVisitor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BinaryEq implements InstructionNode {
    @NonNull
    private final BinaryRegister dest;
    @NonNull
    private final BinaryRegister src1;
    @NonNull
    private final BinaryRegister src2;

    @Override
    public void accept(ISAVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "eq " + dest + ", " + src1 + ", " + src2;
    }
}
