package ISA.InstructionNodes;

import ISA.Registers.Register;
import ISA.Visitors.ISAVisitor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LessThan implements InstructionNode {
    @NonNull
    private final Register dest;
    @NonNull
    private final Register src1;
    @NonNull
    private final Register src2;

    @Override
    public void accept(ISAVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "lt " + dest + ", " + src1 + ", " + src2;
    }
}
