package ISA.Literals;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;


@Getter
@RequiredArgsConstructor
public class Literal {
    private final double value;

    @Override
    public String toString() {
        return Double.toString(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ISA.Literals.Literal that = (ISA.Literals.Literal) o;
        return getValue() == that.getValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}