package IR.values.instructions;

import IR.types.IntegerType;
import IR.types.Type;
import IR.types.VoidType;
import IR.values.BasicBlock;

public class ConvInst extends Instruction {

    public ConvInst(Operator op, BasicBlock basicBlock) {
        super(VoidType.voidType, op, basicBlock);
        setName("%" + valNumber++);
    }
}
