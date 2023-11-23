package IR.values;

import IO.OutputHandler;
import IR.types.LabelType;
import IR.values.instructions.Instruction;
import IR.values.instructions.terminator.TerminatorInst;

import java.util.ArrayList;

public class BasicBlock extends Value {
    private final ArrayList<Instruction> instructions = new ArrayList<>();
    private boolean isTerminated = false;

    public BasicBlock(Function function) {
        super(String.valueOf(valNumber++), new LabelType());
        function.addBasicBlock(this);
    }

    public BasicBlock() {
        super("", null);
    }

    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }

    public void refill(Function function) {
        setName(String.valueOf(valNumber++));
        setType(new LabelType());
        function.addBasicBlock(this);
    }

    public void addInstruction(Instruction instruction) {
        if (isTerminated) {
            return;
        }
        instructions.add(instruction);
        if (instruction instanceof TerminatorInst) {
            this.isTerminated = true;
        }
    }

    public void genLLVM() {
        OutputHandler.genLLVM(getName() + ":");
        for (Instruction instruction : instructions) {
            OutputHandler.genLLVM("\t" + instruction);
        }
    }
}
