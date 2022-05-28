package ISA.Memory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemoryAddress {
    private final int address;

    @Override
    public String toString() {
        return Integer.toString(address);
    }
}
