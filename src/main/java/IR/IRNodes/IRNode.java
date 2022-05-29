package IR.IRNodes;

import IR.Visitors.IRVisitor;

public interface IRNode {
    void accept(IRVisitor visitor);
}
