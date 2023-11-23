package backend;

import IO.OutputHandler;
import IR.IRModule;
import IR.types.VoidType;
import IR.values.*;
import IR.values.instructions.BinaryInst;
import IR.values.instructions.CallInst;
import IR.values.instructions.Instruction;
import IR.values.instructions.men.StoreInst;
import IR.values.instructions.men.AllocInst;
import IR.values.instructions.men.LoadInst;
import IR.values.instructions.terminator.BrInst;
import IR.values.instructions.terminator.RetInst;
import backend.Symbol.MipsSymbol;
import backend.Symbol.MipsSymbolTable;

public class MIPSGenerator {
    private static final MIPSGenerator mipsGenerator = new MIPSGenerator();

    public static MIPSGenerator getInstance() {
        return mipsGenerator;
    }

    private final MipsSymbolTable symbolTables = new MipsSymbolTable();

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
                    translate(instruction);
                }
            }
        }
        symbolTables.rmSymbolTable();
    }

    private void genMacro() {
        genGetInt();
        genPutInt();
        genPutCh();
        genPutStr();
    }

    private void genGlobalVar(GlobalVar globalVar) {
        String ans = globalVarLabel(globalVar.getName()) + ": .word " + globalVar.getValue().getName();
        OutputHandler.genMIPS(ans);
    }

    private String globalVarLabel(String name) {
        return name.substring(1);
    }

    private String blockLabel(Function function, BasicBlock basicBlock) {
        return function.getName() + '_' + basicBlock.getName();
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
        } else if (ir instanceof LoadInst loadInst) {
            parseLoadInst(loadInst);
        } else if (ir instanceof StoreInst storeInst) {
            parseStoreInst(storeInst);
        } else if (ir instanceof BrInst brInst) {

        } else if (ir instanceof RetInst retInst) {
            parseRet(retInst);
        } else if (ir instanceof BinaryInst binaryInst) {
            parseBinary(binaryInst);
        } else if (ir instanceof CallInst callInst) {
            parseCall(callInst);
        }
    }

    private void parseAllocInst(AllocInst allocInst) {
        //指向int32
        getSp(allocInst.getName(), allocInst);
    }

    private void parseLoadInst(LoadInst loadInst) {
        load("$t0", loadInst.pointer().getName());
        store("$t0", loadInst.getName());
    }

    private void parseStoreInst(StoreInst storeInst) {
        load("$t0", storeInst.getVal().getName());
        store("$t0", storeInst.getPtr().getName());
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
            OutputHandler.genMIPS(String.format("addi $sp, $sp, %d", spOff - 4 * rec));
            jal(function.getName());
            OutputHandler.genMIPS("nop");
            OutputHandler.genMIPS(String.format("addi $sp, $sp, %d", -spOff + 4 * rec));
            lw("$ra", "$sp", spOff);
            if (!(callInst.getType() == VoidType.voidType)) {
                store("$v0", callInst.getName());
            }
        }

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
        OutputHandler.genMIPS(String.format("add %s, %s, %s", "$t0", "$t1", "$t2"));
        store("$t0", ans);
    }

    private void sub(String ans, String lVal, String rVal) {
        load("$t1", lVal);
        load("$t2", rVal);
        OutputHandler.genMIPS(String.format("sub %s, %s, %s", "$t0", "$t1", "$t2"));
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
        } else if (getVal(name) instanceof GlobalVar) {
            la(reg, globalVarLabel(name));
            lw(reg, reg, 0);//int
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

    private void jr(String reg) {
        OutputHandler.genMIPS(String.format("jr %s", reg));
    }

    private void mfhi(String reg) {
        OutputHandler.genMIPS("mfhi " + reg);
    }

    private void mflo(String reg) {
        OutputHandler.genMIPS("mflo " + reg);
    }

    private boolean isNumber(String name) {
        return name.matches("-?\\d+");
    }

    int spOff = 0;

    /*
    栈的调用：spOff指向已经存入的值，spOff-4指向当前存入的的值
    */
    private void getSp(String name, Value value) {
        if (name.isEmpty()) {
            return;
        }
        symbolTables.addSymbol(name, new MipsSymbol("$sp", spOff, value));
        spOff -= 4;
    }

    private int getOff(String name) {
        return symbolTables.getSymbol(name).getOff();
    }

    private Value getVal(String name) {
        return symbolTables.getSymbol(name).getVal();
    }

    private void getGp(String name, Value value) {
        if (symbolTables.contains(name)) {
            return;
        }
        symbolTables.addSymbol(name, new MipsSymbol("$gp", 0, value));
    }
}
