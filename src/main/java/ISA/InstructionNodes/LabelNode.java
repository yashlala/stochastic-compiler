package ISA.InstructionNodes;

import ISA.Labels.Label;
import ISA.Visitors.ISAVisitor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LabelNode implements InstructionNode {
    @NonNull
    private final Label label;

    @Override
    public void accept(ISAVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return label + ":";
    }
}
