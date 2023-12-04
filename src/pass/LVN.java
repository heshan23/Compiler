package pass;

import IR.IRModule;
import IR.values.BasicBlock;
import IR.values.ConstInt;
import IR.values.Function;
import IR.values.Value;
import IR.values.instructions.BinaryInst;
import IR.values.instructions.Instruction;
import IR.values.instructions.Operator;

import java.util.HashMap;
import java.util.Iterator;


public class LVN {
    private final IRModule irModule;
    private HashMap<String, Instruction> map;

    public LVN(IRModule irModule) {
        this.irModule = irModule;
    }

    public void run() {
        for (Function function : irModule.getFunctions()) {
            map = new HashMap<>();
            LVNVisit(function.getBasicBlocks().get(0));
        }
    }

    private void LVNVisit(BasicBlock entry) {
        optimizeCalc(entry);
        for (BasicBlock bb : entry.getImmDomList()) {
            LVNVisit(bb);
        }
    }

    private void optimizeCalc(BasicBlock bb) {
        Iterator<Instruction> it = bb.getInstructions().iterator();
        while (it.hasNext()) {
            Instruction instr = it.next();
            if (!(instr instanceof BinaryInst binaryInst && binaryInst.isNumber())) {
                continue;
            }
            Value left = binaryInst.lVal();
            Value right = binaryInst.rVal();
            Operator op = binaryInst.getOp();
            int cnt = 0;
            if (left instanceof ConstInt) cnt++;
            if (right instanceof ConstInt) cnt++;
            if (cnt == 1) {
                Value ans = calcSingleCon(left, right, op);
                if (ans != null) {
                    instr.replacedByNewVal(ans);
                    instr.deleteUse();
                    it.remove();
                }
            } else if (cnt == 2) {
                Value ans = calcDoubleCon(left, right, op);
                instr.replacedByNewVal(ans);
                instr.deleteUse();
                it.remove();
            }
        }
    }

    private Value calcDoubleCon(Value left, Value right, Operator op) {
        int lVal = ((ConstInt) left).getVal();
        int rVal = ((ConstInt) right).getVal();
        int ans = 0;
        switch (op) {
            case Add -> ans = lVal + rVal;
            case Sub -> ans = lVal - rVal;
            case Mul -> ans = lVal * rVal;
            case SDiv -> ans = lVal / rVal;
            case Mod -> ans = lVal % rVal;
        }
        return new ConstInt(ans);
    }

//    private Value calcNoneCon() {
//
//    }

    private Value calcSingleCon(Value left, Value right, Operator op) {
        switch (op) {
            case Add -> {
                //a+0
                if (isZero(left)) return right;
                if (isZero(right)) return left;
            }
            case Sub -> {
                //a-0
                if (isZero(right)) return left;
            }
            case Mul -> {
                //a*0,a*1
                if (isZero(left) || isZero(right)) return new ConstInt(0);
                if (isOne(left)) return right;
                if (isOne(right)) return left;
            }
            case SDiv -> {
                //0/a,a/1
                if (isZero(left)) return new ConstInt(0);
                if (isOne(right)) return left;
            }
            case Mod -> {
                //0%a,a%1
                if (isZero(left) || isOne(right)) return new ConstInt(0);
            }
        }
        return null;
    }

    private boolean isZero(Value value) {
        return (value instanceof ConstInt constInt && constInt.getVal() == 0);
    }

    private boolean isOne(Value value) {
        return (value instanceof ConstInt constInt && constInt.getVal() == 1);
    }
}
