package backend;

import IO.OutputHandler;
import IR.IRModule;
import IR.types.*;
import IR.values.*;
import IR.values.instructions.BinaryInst;
import IR.values.instructions.CallInst;
import IR.values.instructions.ConvInst;
import IR.values.instructions.Instruction;
import IR.values.instructions.men.GEPInst;
import IR.values.instructions.men.StoreInst;
import IR.values.instructions.men.AllocInst;
import IR.values.instructions.men.LoadInst;
import IR.values.instructions.terminator.BrInst;
import IR.values.instructions.terminator.RetInst;
import backend.Symbol.MipsSymbol;
import backend.Symbol.MipsSymbolTable;

import java.util.ArrayList;

public class MIPSGenerator {
    private static final MIPSGenerator mipsGenerator = new MIPSGenerator();

    public static MIPSGenerator getInstance() {
        return mipsGenerator;
    }

    private final MipsSymbolTable symbolTables = new MipsSymbolTable();
    Function curFunc;

    public void genMIPS() {
        IRModule irModule = IRModule.getInstance();
        symbolTables.addSymbolTable();
        genMacro();
        OutputHandler.genMIPS(".data");
        for (GlobalVar globalVar : irModule.getGlobalVars()) {
            getGp(globalVar.getName(), globalVar);
            genGlobalVar(globalVar);
        }
        OutputHandler.genMIPS(".text");
        jal("main");
        li("$v0", 10);
        syscall();
        for (Function function : irModule.getFunctions()) {
            curFunc = function;
            symbolTables.addSymbolTable();
            OutputHandler.genMIPS(function.getName() + ":");
            int rec = function.getArguments().size();
            for (Argument arg : function.getArguments()) {
                lw("$t0", "$sp", 4 * rec);
                rec--;
                getSp(arg.getName(), arg);
                sw("$t0", "$sp", getOff(arg.getName()));
            }
            for (BasicBlock basicBlock : function.getBasicBlocks()) {
                OutputHandler.genMIPS(blockLabel(function, basicBlock) + ":");
                for (Instruction instruction : basicBlock.getInstructions()) {
                    if (!(instruction instanceof AllocInst)) {
                        getSp(instruction.getName(), instruction);
                    }
                    OutputHandler.genMIPS("#" + instruction);
                    translate(instruction);
                }
            }
            symbolTables.rmSymbolTable();
        }
    }

    private void genMacro() {
        genGetInt();
        genPutInt();
        genPutCh();
        genPutStr();
    }

    private void genGlobalVar(GlobalVar globalVar) {
        StringBuilder ans = new StringBuilder(globalVarLabel(globalVar.getName()));
        if (globalVar.getValue() instanceof ConstArray constArray) {
            if (constArray.allZero()) {
                ans.append(": .space ");
                int space = 4 * ((ArrayType) constArray.getType()).getCapacity();
                ans.append(space);
            } else {
                ans.append(": .word ");
                ArrayType arrayType = (ArrayType) ((PointerType) globalVar.getType()).getTargetType();
                ans.append(listGlobalArray(arrayType, constArray));
            }
        } else {
            ans.append(": .word ");
            ans.append(globalVar.getValue().getName());
        }
        OutputHandler.genMIPS(ans.toString());
    }

    private String listGlobalArray(ArrayType arrayType, ConstArray constArray) {
        StringBuilder res = new StringBuilder();
        for (Value value : constArray.getValues()) {
            if (value instanceof ConstArray) {
                res.append(listGlobalArray((ArrayType) arrayType.getElementType(),
                        (ConstArray) value));
            } else {
                res.append(value.getName()).append(',');
            }
        }
        int last = arrayType.getLen() - constArray.getValues().size();
        res.append("0,".repeat(Math.max(0, last)));
        return res.toString();
    }

    private String globalVarLabel(String name) {
        return name.substring(1);
    }

    private String blockLabel(Function function, BasicBlock basicBlock) {
        return function.getName() + '_' + basicBlock.getName();
    }

    private String blockLabel(BasicBlock basicBlock) {
        return curFunc.getName() + '_' + basicBlock.getName();
    }

    private void genGetInt() {
        OutputHandler.genMIPS(".macro getInt");
        li("$v0", 5);
        syscall();
        OutputHandler.genMIPS(".end_macro");
        //返回值在$v0
    }

    private void genPutInt() {
        //move("$a0", "%in");
        OutputHandler.genMIPS(".macro putInt");
        li("$v0", 1);
        syscall();
        OutputHandler.genMIPS(".end_macro");
    }

    private void genPutCh() {
        //move($a0,%in)
        OutputHandler.genMIPS(".macro putCh");
        li("$v0", 11);
        syscall();
        OutputHandler.genMIPS(".end_macro");
    }

    private void genPutStr() {
        //move($a0,%addr)
        OutputHandler.genMIPS(".macro putStr");
        li("$v0", 4);
        syscall();
        OutputHandler.genMIPS(".end_macro");
    }

    private void translate(Instruction ir) {
        if (ir instanceof AllocInst allocInst) {
            parseAllocInst(allocInst);
        } else if (ir instanceof GEPInst gepInst) {
            parseGepInst(gepInst);
        } else if (ir instanceof LoadInst loadInst) {
            parseLoadInst(loadInst);
        } else if (ir instanceof StoreInst storeInst) {
            parseStoreInst(storeInst);
        } else if (ir instanceof BrInst brInst) {
            parseBrInst(brInst);
        } else if (ir instanceof RetInst retInst) {
            parseRet(retInst);
        } else if (ir instanceof BinaryInst binaryInst) {
            parseBinary(binaryInst);
        } else if (ir instanceof CallInst callInst) {
            parseCall(callInst);
        } else if (ir instanceof ConvInst convInst) {
            parseConvInst(convInst);
        }
    }

    private void parseAllocInst(AllocInst allocInst) {
        //指向int32
        Type type = allocInst.getType();
        Type target = ((PointerType) type).getTargetType();
        if (target instanceof IntegerType) {
            getSp(allocInst.getName(), allocInst);
        } else if (target instanceof ArrayType arrayType) {
            getSpArray(allocInst.getName(), 4 * arrayType.getCapacity(), allocInst);
        } else if (target instanceof PointerType pointerType) {
            if (pointerType.getTargetType() instanceof IntegerType) {
                getSp(allocInst.getName(), allocInst);
            } else if (pointerType.getTargetType() instanceof ArrayType arrayType) {
                getSpArray(allocInst.getName(), 4 * arrayType.getCapacity(), allocInst);
            }
        }
    }

    private void parseGepInst(GEPInst gepInst) {
        Value pointer = gepInst.getPointer();
        Type type = pointer.getType();
        Type target = ((PointerType) type).getTargetType();
        ArrayList<Value> indices = gepInst.getIndices();
        load("$t0", pointer.getName());
        for (Value value : indices) {
            int base;
            int off;
            if (target instanceof ArrayType) {
                base = ((ArrayType) target).getCapacity() * 4;
            } else {
                base = 4;
            }
            if (value instanceof ConstInt) {
                off = ((ConstInt) value).getVal() * base;
                OutputHandler.genMIPS(String.format("addiu $t0, $t0, %d", off));
            } else {
                li("$t1", base);
                load("$t2", value.getName());
                OutputHandler.genMIPS("mult $t1, $t2");
                mflo("$t1");
                OutputHandler.genMIPS("addu $t0, $t0, $t1");
            }
            if (target instanceof ArrayType arrayType) {
                target = arrayType.getElementType();
            }
        }
        store("$t0", gepInst.getName());
    }

    private void parseLoadInst(LoadInst loadInst) {
        if (loadInst.pointer() instanceof GEPInst) {
            load("$t0", loadInst.pointer().getName());
            lw("$t0", "$t0", 0);
            store("$t0", loadInst.getName());
        } else {
            load("$t0", loadInst.pointer().getName());
            store("$t0", loadInst.getName());
        }
    }

    private void parseStoreInst(StoreInst storeInst) {
        if (storeInst.getPtr() instanceof GEPInst) {
            load("$t0", storeInst.getVal().getName());
            load("$t1", storeInst.getPtr().getName());
            sw("$t0", "$t1", 0);
        } else {
            load("$t0", storeInst.getVal().getName());
            store("$t0", storeInst.getPtr().getName());
        }
    }

    private void parseBrInst(BrInst brInst) {
        if (brInst.noneCond()) {
            j(blockLabel(brInst.getTrueBlock()));
        } else {
            load("$t0", brInst.getCond().getName());
            bgtz("$t0", blockLabel(brInst.getTrueBlock()));
            j(blockLabel(brInst.getFalseBlock()));
        }
    }

    private void parseRet(RetInst retInst) {
        if (!(retInst.getType() == VoidType.voidType)) {
            load("$v0", retInst.getOperands().get(0).getName());
        }
        jr("$ra");
    }

    private void parseBinary(BinaryInst binaryInst) {
        String ans = binaryInst.getName();
        String lVal = binaryInst.lVal().getName();
        String rVal = binaryInst.rVal().getName();
        switch (binaryInst.getOp()) {
            case Add -> add(ans, lVal, rVal);
            case Sub -> sub(ans, lVal, rVal);
            case Mul -> mul(ans, lVal, rVal);
            case SDiv -> div(ans, lVal, rVal);
            case Mod -> mod(ans, lVal, rVal);
            case Sgt -> cmp("sgt", ans, lVal, rVal);
            case Sge -> cmp("sge", ans, lVal, rVal);
            case Slt -> cmp("slt", ans, lVal, rVal);
            case Sle -> cmp("sle", ans, lVal, rVal);
            case Ne -> cmp("sne", ans, lVal, rVal);
            case Eq -> cmp("seq", ans, lVal, rVal);
        }
    }

    private void parseCall(CallInst callInst) {
        Function function = callInst.getFunction();
        String caller = callInst.getName();
        if (function.isLibrary()) {
            switch (function.getName()) {
                case "getint" -> {
                    genInt();
                    sw("$v0", "$sp", getOff(caller));
                }
                case "putint" -> {
                    load("$a0", callInst.getOperands().get(1).getName());
                    putInt();
                }
                case "putch" -> {
                    load("$a0", callInst.getOperands().get(1).getName());
                    putCh();
                }//case "putstr" -> ;
            }
        } else {
            sw("$ra", "$sp", spOff);
            int rec = 1;
            for (int i = 1; i < callInst.getOperands().size(); i++) {
                load("$t0", callInst.getOperands().get(i).getName());
                sw("$t0", "$sp", spOff - 4 * rec);
                rec++;
            }
            OutputHandler.genMIPS(String.format("addiu $sp, $sp, %d", spOff - 4 * rec));
            jal(function.getName());
            //OutputHandler.genMIPS("nop");关闭延迟槽
            OutputHandler.genMIPS(String.format("addiu $sp, $sp, %d", -spOff + 4 * rec));
            lw("$ra", "$sp", spOff);
            if (!(callInst.getType() == VoidType.voidType)) {
                store("$v0", callInst.getName());
            }
        }

    }

    private void parseConvInst(ConvInst convInst) {
        load("$t0", convInst.getValue().getName());
        store("$t0", convInst.getName());
    }

    private void li(String reg, String val) {
        String ans = String.format("li %s, %s", reg, val);
        OutputHandler.genMIPS(ans);
    }

    private void li(String reg, int val) {
        String ans = String.format("li %s, %d", reg, val);
        OutputHandler.genMIPS(ans);
    }

    private void la(String reg, String val) {
        OutputHandler.genMIPS(String.format("la %s, %s", reg, val));
    }

    private void syscall() {
        OutputHandler.genMIPS("syscall");
    }

    private void jal(String func) {
        OutputHandler.genMIPS("jal " + func);
    }

    private void j(String label) {
        OutputHandler.genMIPS("j " + label);
    }

    private void jr(String reg) {
        OutputHandler.genMIPS(String.format("jr %s", reg));
    }

    private void bgtz(String reg, String label) {
        OutputHandler.genMIPS("bgtz " + reg + ", " + label);
    }

    private void move(String reg, String valReg) {
        OutputHandler.genMIPS(String.format("move %s, %s", reg, valReg));
    }

    private void genInt() {
        OutputHandler.genMIPS("getInt");
    }

    private void putInt() {
        OutputHandler.genMIPS("putInt");
    }

    private void putCh() {
        OutputHandler.genMIPS("putCh");
    }

    private void putStr() {
        OutputHandler.genMIPS("putStr");
    }

    private void add(String ans, String lVal, String rVal) {
        load("$t1", lVal);
        load("$t2", rVal);
        OutputHandler.genMIPS(String.format("addu %s, %s, %s", "$t0", "$t1", "$t2"));
        store("$t0", ans);
    }

    private void sub(String ans, String lVal, String rVal) {
        load("$t1", lVal);
        load("$t2", rVal);
        OutputHandler.genMIPS(String.format("subu %s, %s, %s", "$t0", "$t1", "$t2"));
        store("$t0", ans);
    }

    private void mul(String ans, String lVal, String rVal) {
        load("$t1", lVal);
        load("$t2", rVal);
        OutputHandler.genMIPS(String.format("mult %s, %s", "$t1", "$t2"));
        mflo("$t0");
        store("$t0", ans);
    }

    private void div(String ans, String lVal, String rVal) {
        load("$t1", lVal);
        load("$t2", rVal);
        OutputHandler.genMIPS(String.format("div %s, %s", "$t1", "$t2"));
        mflo("$t0");
        store("$t0", ans);
    }

    private void mod(String ans, String lVal, String rVal) {
        load("$t1", lVal);
        load("$t2", rVal);
        OutputHandler.genMIPS(String.format("div %s, %s", "$t1", "$t2"));
        mfhi("$t0");
        store("$t0", ans);
    }

    private void load(String reg, String name) {
        if (isNumber(name)) {
            li(reg, name);
        } else if (getVal(name) instanceof GlobalVar globalVar) {
            la(reg, globalVarLabel(name));
            if (globalVar.getValue() instanceof ConstInt) {
                lw(reg, reg, 0);//int
            }
        } else {
            lw(reg, "$sp", getOff(name));
        }
    }

    private void lw(String reg, String name, int off) {
        OutputHandler.genMIPS(String.format("lw %s, %s(%s)", reg, off, name));
    }

    private void store(String reg, String name) {
        if (getVal(name) instanceof GlobalVar) {
            la("$t1", globalVarLabel(name));
            sw(reg, "$t1", 0);
        } else {
            sw(reg, "$sp", getOff(name));
        }
    }

    private void sw(String reg, String base, int off) {
        OutputHandler.genMIPS(String.format("sw %s, %s(%s)", reg, off, base));
    }

    private void mfhi(String reg) {
        OutputHandler.genMIPS("mfhi " + reg);
    }

    private void mflo(String reg) {
        OutputHandler.genMIPS("mflo " + reg);
    }

    private void cmp(String ins, String ans, String reg1, String reg2) {
        load("$t1", reg1);
        load("$t2", reg2);
        OutputHandler.genMIPS(ins + " $t0, $t1, $t2");
        store("$t0", ans);
    }

    private boolean isNumber(String name) {
        return name.matches("-?\\d+");
    }

    int spOff = 0;

    /*
    栈的调用：spOff指向将要存的值，spOff+4指向上一个存入的的值
    */
    private void getSp(String name, Value value) {
        if (name.isEmpty() || symbolTables.contains(name)) {
            return;
        }
        symbolTables.addSymbol(name, new MipsSymbol("$sp", spOff, value));
        spOff -= 4;
    }

    private void getSpArray(String name, int off, Value value) {
        if (name.isEmpty() || symbolTables.contains(name)) {
            return;
        }
        getSp(name, value);
        spOff -= off;
        OutputHandler.genMIPS("addiu $t0, $sp, " + (spOff + 4));
        store("$t0", name);
    }

    private int getOff(String name) {
        return symbolTables.getSymbol(name).getOff();
    }

    private Value getVal(String name) {
        return symbolTables.getSymbol(name).getVal();
    }

    private void getGp(String name, Value value) {
        if (name.isEmpty() || symbolTables.contains(name)) {
            return;
        }
        symbolTables.addSymbol(name, new MipsSymbol("$gp", 0, value));
    }
}
