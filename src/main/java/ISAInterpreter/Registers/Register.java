package ISAInterpreter.Registers;

import ISA.Memory.MemoryAddress;
import ISAInterpreter.MemoryBank;
import ISAInterpreter.RegisterFile;

public interface Register {
    String getName();

    double toDouble();

    // Acceptor pattern hook for register loads into memoryBank
    void loadAccept(MemoryBank memoryBank, MemoryAddress address);

    // Acceptor pattern hook for register stores into memoryBank
    void storeAccept(MemoryBank memoryBank, MemoryAddress address);

    void putAccept(RegisterFile registerFile);

    String toString();
}
