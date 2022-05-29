package IR.IRNodes;

import IR.MemoryAddresses.MemoryAddress;
import IR.Variables.Variable;
import IR.Visitors.IRVisitor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Load implements IRNode{
    @NonNull
    private final Variable dest;
    @NonNull
    private final MemoryAddress address;

    @Override
    public void accept(IRVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return dest + " = @" + address;
    }
}
