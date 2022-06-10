package ISAInterpreter;

import ISA.InstructionNodes.InstructionNode;
import ISA.Labels.Label;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class ISAInterpreter {
    public static void executeProgram(List<InstructionNode> instructions, double noiseCoefficient) {
        for (String s : getProgramOutput(instructions, noiseCoefficient)) {
            System.out.println(s);
        }
    }

    public static List<String> getProgramOutput(List<InstructionNode> instructions, double noiseCoefficient) {
        // Scan the program for all labels.
        LabelScanningVisitor labelScanningVisitor = new LabelScanningVisitor();
        Map<Label, Integer> labelIndex = labelScanningVisitor.buildLabelIndex(instructions);

        ExecutionVisitor executionVisitor = new ExecutionVisitor();
        return executionVisitor.executeProgram(instructions, labelIndex, noiseCoefficient);
    }
}