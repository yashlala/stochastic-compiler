package ISA.InstructionNodes;

import ISA.Memory.MemoryAddress;
import ISA.Registers.Register;
import ISA.Visitors.ISAVisitor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StoreIns implements InstructionNode {
    @NonNull
    private final Register register;
    @NonNull
    private final MemoryAddress address;

    @Override
    public void accept(ISAVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "sw " + register + ", " + address;
    }
}
