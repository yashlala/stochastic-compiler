package ISA.Registers;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class BinaryRegister implements Register{
    @NonNull
    private final String name;

    @Override
    public String toString() {
        return name;
    }
}
