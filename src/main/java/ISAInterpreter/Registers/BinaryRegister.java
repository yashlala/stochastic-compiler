package ISAInterpreter.Registers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BinaryRegister implements Register {
    private final String name;
    private int value;

    public BinaryRegister(String name) {
        this.name = name;
        this.value = 0;
    }

    public BinaryRegister(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public BinaryRegister(BinaryRegister other) {
        name = other.name;
        value = other.value;
    }

    public void copyValue(BinaryRegister other) {
        value = other.value;
    }

    @Override
    public double toDouble() {
        return value;
    }
}
