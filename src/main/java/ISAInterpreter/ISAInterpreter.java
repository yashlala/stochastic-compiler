package ISAInterpreter;

import ISA.InstructionNodes.InstructionNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.var;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ISAInterpreter {
    // Should this return a list of things to print? That would avoid side effects from printing directly.
    public static void executeProgram(List<InstructionNode> instructions)  {
        ExecutionVisitor executionVisitor = new ExecutionVisitor();
        executionVisitor.visitAllInstructions(instructions);
    }
}