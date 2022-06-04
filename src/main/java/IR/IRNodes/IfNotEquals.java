package IR.IRNodes;

import IR.Labels.Label;
import IR.Variables.Variable;
import IR.Visitors.IRReturnVisitor;
import IR.Visitors.IRVisitor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class IfNotEquals implements IRNode{
    @NonNull
    private final Variable cond1;
    @NonNull
    private final Variable cond2;
    @NonNull
    private final Label label;
    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <R> R accept(IRReturnVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "ifne " + cond1 + " " + cond2 + " goto " + label;
    }
}
