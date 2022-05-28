package ISA.InstructionNodes;

import ISA.Visitors.ISAVisitor;

public interface InstructionNode {
    void accept(ISAVisitor visitor);
}
