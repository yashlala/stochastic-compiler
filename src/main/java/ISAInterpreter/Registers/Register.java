package ISAInterpreter.Registers;

import ISA.Memory.MemoryAddress;
import ISAInterpreter.MemoryBank;
import ISAInterpreter.RegisterFile;

public interface Register {
    String getName();

    double toDouble();

    // Set your value based on the double's value.
    void fromDouble(double value);

    // Acceptor pattern hook for register loads into a MemoryBank
    void loadAccept(MemoryBank memoryBank, MemoryAddress address);

    // Acceptor pattern hook for register stores into a MemoryBank
    void storeAccept(MemoryBank memoryBank, MemoryAddress address);

    // Acceptor pattern hook for register puts into a RegisterFile.
    // TODO: Maybe the "put/get" vs "load/store" should be made consistent.
    void putAccept(RegisterFile registerFile);

    // Doesn't actually enforce anything, because the Object version of this method
    // is always ready as a fallback. Oh well.
    String toString();
}
