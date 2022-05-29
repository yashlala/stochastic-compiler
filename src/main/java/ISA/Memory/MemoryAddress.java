package ISA.Memory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class MemoryAddress {
    private final int address;

    @Override
    public String toString() {
        return Integer.toString(address);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemoryAddress that = (MemoryAddress) o;
        return getAddress() == that.getAddress();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAddress());
    }
}
