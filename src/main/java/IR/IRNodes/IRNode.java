package IR.IRNodes;

import IR.Visitors.IRReturnVisitor;
import IR.Visitors.IRVisitor;

public interface IRNode {
    void accept(IRVisitor visitor);

    <R> R accept(IRReturnVisitor<R> visitor);
}
