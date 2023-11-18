package IR.values.instructions;

import IR.types.Type;
import IR.values.BasicBlock;
import IR.values.User;
import IR.values.Value;

public class Instruction extends User {
    private Operator op;

    public Instruction(Type type, Operator op, BasicBlock basicBlock) {
        super("", type);
        this.op = op;
        basicBlock.addInstruction(this);
    }

    public Operator getOp() {
        return op;
    }
}
