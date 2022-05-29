package IR.IRNodes;

import IR.Variables.Variable;
import IR.Visitors.IRVisitor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Print implements IRNode {
    @NonNull
    private final Variable var;

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "print " + var;
    }
}