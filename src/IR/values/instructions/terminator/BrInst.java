package IR.values.instructions.terminator;

import IR.types.VoidType;
import IR.values.BasicBlock;
import IR.values.Value;
import IR.values.instructions.Operator;

public class BrInst extends TerminatorInst {
    public BrInst(BasicBlock basicBlock, BasicBlock trueBlock) {
        super(VoidType.voidType, Operator.br, basicBlock);
        addOperands(trueBlock);
    }

    public BrInst(BasicBlock basicBlock, BasicBlock trueBlock, BasicBlock falseBlock, Value cond) {
        super(VoidType.voidType, Operator.br, basicBlock);
        addOperands(cond);
        addOperands(trueBlock);
        addOperands(falseBlock);
    }

    public boolean noneCond() {
        return getOperands().size() == 1;
    }

    public BasicBlock getTrueBlock() {
        if (noneCond()) {
            return (BasicBlock) getOperands().get(0);
        } else {
            return (BasicBlock) getOperands().get(1);
        }
    }

    public BasicBlock getFalseBlock() {
        //assert !noneCond();
        return (BasicBlock) getOperands().get(2);
    }

    public Value getCond() {
        //assert !noneCond();
        return getOperands().get(0);
    }

    @Override
    public String toString() {
        if (noneCond()) {
            return String.format("br label %%%s", getTrueBlock().getName());
        } else {
            return String.format("br i1 %s, label %%%s, label %%%s",
                    getCond().getName(), getTrueBlock().getName(), getFalseBlock().getName());
        }

    }
}
