package ISA.InstructionNodes;

import ISA.Literals.Literal;
import ISA.Registers.BinaryRegister;
import ISA.Visitors.ISAVisitor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoadLiteralIns implements InstructionNode {
    @NonNull
    private final BinaryRegister register;
    @NonNull
    private final Literal value;

    @Override
    public void accept(ISAVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "li " + register + ", " + value;
    }
}
