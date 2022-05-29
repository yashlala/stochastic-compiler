package IR.IRNodes;

import IR.Labels.Label;
import IR.Variables.Variable;
import IR.Visitors.IRVisitor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class IfZero implements IRNode {
    @NonNull
    private final Variable condition;
    @NonNull
    private final Label label;

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "if0 " + condition + " goto " + label;
    }
}
