package ISA.InstructionNodes;

import ISA.Registers.Register;
import ISA.Visitors.ISAVisitor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PrintIns implements InstructionNode {
    @NonNull
    private final Register register;

    @Override
    public void accept(ISAVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "print " + register;
    }
}
