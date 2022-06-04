package IR.IRNodes;

import IR.MemoryAddresses.MemoryAddress;
import IR.Variables.Variable;
import IR.Visitors.IRReturnVisitor;
import IR.Visitors.IRVisitor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Store implements IRNode {
    @NonNull
    private final MemoryAddress address;
    @NonNull
    private final Variable src;

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
        return "@" + address + " = " + src;
    }
}
