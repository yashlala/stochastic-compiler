package ISA.InstructionNodes;

import ISA.Registers.BinaryRegister;
import ISA.Visitors.ISAVisitor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoadIns implements InstructionNode {
    @NonNull
    private final BinaryRegister register;
    @NonNull
    private final BinaryRegister address;

    @Override
    public void accept(ISAVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "lw " + register + ", @" + address;
    }
}
