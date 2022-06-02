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
    // Should this return a list of things to print? That would avoid side effects from printing directly.
    public static void executeProgram(List<InstructionNode> instructions) {
        // Scan the program for all labels.
        LabelScanningVisitor labelScanningVisitor = new LabelScanningVisitor();
        Map<Label, Integer> labelIndex = labelScanningVisitor.buildLabelIndex(instructions);

        ExecutionVisitor executionVisitor = new ExecutionVisitor();
        executionVisitor.executeProgram(instructions, labelIndex);
    }
}