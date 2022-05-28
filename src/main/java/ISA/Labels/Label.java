package ISA.Labels;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Label {
    @NonNull
    private final String name;

    @Override
    public String toString() {
        return name;
    }
}
