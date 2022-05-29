package ISA.Registers;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class StochasticRegister implements Register {
    private static final ImmutableMap<RegisterPolarity, String> polarityNames =
            new ImmutableMap.Builder<RegisterPolarity, String>().put(RegisterPolarity.BIPOLAR, "b")
                    .put(RegisterPolarity.UNIPOLAR, "u")
                    .build();
    @NonNull
    private final String name;
    @NonNull
    private final RegisterPolarity polarity;
    private final int width;

    @Override
    public String toString() {
        return name + "[" + polarityNames.get(polarity) + width + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StochasticRegister that = (StochasticRegister) o;
        return getWidth() == that.getWidth() && getName().equals(that.getName()) && getPolarity() == that.getPolarity();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getPolarity(), getWidth());
    }
}
