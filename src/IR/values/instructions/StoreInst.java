package IR.values.instructions;

import IR.values.BasicBlock;
import IR.values.Value;
import IR.values.instructions.men.MemInst;

public class StoreInst extends MemInst {
    private final Value ptr;

    private final Value val;

    //ptr 指向被赋值地址，val是需要的赋值
    public StoreInst(BasicBlock basicBlock, Value val, Value ptr) {
        super(val.getType(), Operator.Store, basicBlock);
        this.ptr = ptr;
        this.val = val;
        addOperands(val);
        addOperands(ptr);
    }

    @Override
    public String toString() {
        return String.format("store %s %s, %s %s",
                val.getType(), val.getName(), ptr.getType(), ptr.getName());
    }
}
