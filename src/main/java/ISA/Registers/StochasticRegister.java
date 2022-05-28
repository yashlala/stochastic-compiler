package ISA.Registers;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

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
}
