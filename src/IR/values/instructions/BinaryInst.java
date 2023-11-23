package IR.values.instructions;

import IR.types.Type;
import IR.values.BasicBlock;
import IR.values.Value;

import java.util.Collections;

public class BinaryInst extends Instruction {
    public BinaryInst(BasicBlock basicBlock, Operator op, Value lVal, Value rVal) {
        super(lVal.getType(), op, basicBlock);
        addOperands(lVal);
        addOperands(rVal);
        setName("%" + valNumber++);
    }

    public Value lVal() {
        return getOperands().get(0);
    }

    public Value rVal() {
        return getOperands().get(1);
    }

    private Type type() {
        return lVal().getType();
    }

    public boolean isNumber() {
        Operator op = getOp();
        return op == Operator.Add || op == Operator.Sub || op == Operator.Mul
                || op == Operator.SDiv || op == Operator.Mod;
    }

    public boolean isLogical() {
        Operator op = getOp();
        return op == Operator.Eq || op == Operator.Ne ||
                op == Operator.Sle || op == Operator.Slt ||
                op == Operator.Sge || op == Operator.Sgt;
    }

    @Override
    public String toString() {
        String opHead = getName() + " = ";
        switch (getOp()) {
            case Add -> opHead += "add";
            case Sub -> opHead += "sub";
            case Mul -> opHead += "mul";
            case SDiv -> opHead += "sdiv";
            case Mod -> opHead += "srem";
            case Eq -> opHead += "icmp eq";
            case Ne -> opHead += "icmp ne";
            case Sle -> opHead += "icmp sle";
            case Slt -> opHead += "icmp slt";
            case Sge -> opHead += "icmp sge";
            case Sgt -> opHead += "icmp sgt";
        }
        return opHead + ' ' + type() + ' ' + lVal().getName() + ", " + rVal().getName();
    }
}
