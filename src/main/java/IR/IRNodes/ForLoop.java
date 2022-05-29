package IR.IRNodes;

import IR.Variables.Variable;
import IR.Visitors.IRVisitor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ForLoop implements IRNode{
    @NonNull
    private final Variable loopIterVar;
    @NonNull
    private final Variable loopRangeStart;
    @NonNull
    private final Variable loopRangeEnd;
    @NonNull
    private final List<IRNode> contents;
    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("for (").append(loopIterVar).append(" in ").append(loopRangeStart)
                .append("..").append(loopRangeEnd).append(")\nBEGIN\n");
        for (IRNode node: contents) {
            builder.append(node).append("\n");
        }
        builder.append("END");
        return builder.toString();
    }
}
