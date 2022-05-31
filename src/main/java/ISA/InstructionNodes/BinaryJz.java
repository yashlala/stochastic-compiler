package ISA.InstructionNodes;

import ISA.Labels.Label;
import ISA.Registers.BinaryRegister;
import ISA.Visitors.ISAVisitor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BinaryJz implements InstructionNode{
    @NonNull
    private final Label label;

    @NonNull
    private final BinaryRegister conditionReg;

    @Override
    public void accept(ISAVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "jz " + label + " " + conditionReg;
    }
}
