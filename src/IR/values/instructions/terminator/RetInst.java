package IR.values.instructions.terminator;

import IO.OutputHandler;
import IR.types.Type;
import IR.types.VoidType;
import IR.values.BasicBlock;
import IR.values.Value;
import IR.values.instructions.Operator;

public class RetInst extends TerminatorInst {
    public RetInst(BasicBlock basicBlock) {
        super(VoidType.voidType, Operator.ret, basicBlock);
    }

    public RetInst(BasicBlock basicBlock, Value value) {
        super(value.getType(), Operator.ret, basicBlock);
        addOperands(value);
    }

    public RetInst(Type type, Operator op, BasicBlock basicBlock) {
        super(type, op, basicBlock);
    }


    @Override
    public String toString() {
        if (getType() == VoidType.voidType) {
            return "ret void";
        } else {
            return "ret" + " " + getOperands().get(0).getType()
                    + " " + getOperands().get(0).getName();
        }
    }
}
