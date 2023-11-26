package IR.values.instructions.men;

import IR.types.PointerType;
import IR.types.Type;
import IR.values.Assignable;
import IR.values.BasicBlock;
import IR.values.Value;
import IR.values.instructions.Operator;

public class LoadInst extends MemInst implements Assignable {
    public LoadInst(BasicBlock basicBlock, Value pointer) {
        super(((PointerType) pointer.getType()).getTargetType(), Operator.Load, basicBlock);
        setName("%" + valNumber++);
        addOperands(pointer);
    }

    public Value pointer() {
        return getOperands().get(0);
    }

    @Override
    public String toString() {
        return String.format("%s = load %s, %s %s",
                getName(), getType(), pointer().getType(), pointer().getName());
    }
}
