package IR.IRNodes;

import IR.Variables.Variable;
import IR.Visitors.IRVisitor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Subtract implements IRNode {
    @NonNull
    private final Variable dest;
    @NonNull
    private final Variable src1;
    @NonNull
    private final Variable src2;

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return dest + " = " + src1 + " - " + src2;
    }
}
