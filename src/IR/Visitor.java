package IR;

import IR.SymbolTable.SymbolTable;
import IR.types.*;
import IR.values.*;
import IR.values.instructions.BinaryInst;
import IR.values.instructions.Operator;
import node.*;
import node.Number;
import node.decl.*;
import node.expression.*;
import node.func.FuncDef;
import node.func.FuncFParam;
import node.func.MainFuncDef;
import node.stmt.*;
import token.Token;
import token.TokenType;

import java.util.ArrayList;


public class Visitor {
    private final SymbolTable symbolTables = new SymbolTable();
    private final BuildFactory buildFactory = BuildFactory.getInstance();

    /*信号*/
    private Function curFunction;
    private BasicBlock curBlock;
    private BasicBlock curTrueBlock;
    private BasicBlock curFalseBlock;
    private BasicBlock curEndBlock;
    private BasicBlock curForEndBlock;
    private int immediate;
    private Value tmpValue;
    private Type tmpType;
    private boolean isGlobal;

    private int calculate(int a, int b, TokenType op) {
        switch (op) {
            case PLUS -> {
                return a + b;
            }
            case MINU -> {
                return a - b;
            }
            case MULT -> {
                return a * b;
            }
            case DIV -> {
                return a / b;
            }
            case MOD -> {
                return a % b;
            }
            default -> {
                return 0;
            }
        }
    }

    public void visitCompUnit(CompUnit compUnit) {
        symbolTables.addSymbolTable();
        symbolTables.addSymbol("getint", buildFactory.buildLibFunc("getint", IntegerType.i32, new ArrayList<>()));
        symbolTables.addSymbol("putint", buildFactory.buildOneParamLibFunc("putint", VoidType.voidType, IntegerType.i32));
        symbolTables.addSymbol("putch", buildFactory.buildOneParamLibFunc("putch", VoidType.voidType, IntegerType.i32));
        symbolTables.addSymbol("putstr", buildFactory.buildOneParamLibFunc("putstr", VoidType.voidType, new PointerType(IntegerType.i8)));
        for (Decl decl : compUnit.getDecls()) {
            isGlobal = true;
            visitDecl(decl);
            isGlobal = false;
        }
        for (FuncDef funcDef : compUnit.getFuncDefs()) {
            visitFuncDef(funcDef);
        }
        visitMainFuncDef(compUnit.getMainFuncDef());
    }

    private void visitDecl(Decl decl) {
        if (decl instanceof ConstDecl) {
            visitConstDecl((ConstDecl) decl);
        } else {
            visitVarDecl((VarDecl) decl);
        }
    }

    private void visitConstDecl(ConstDecl constDecl) {
        tmpType = IntegerType.i32;//int
        for (ConstDef constDef : constDecl.getConstDefs()) {
            visitConstDef(constDef);
        }
    }

    private void visitConstDef(ConstDef constDef) {
        String name = constDef.getIdent().getToken();
        visitConstInitVal(constDef.getConstInitVal());
        if (isGlobal) {
            Value value = buildFactory.buildConstInt(immediate);
            tmpValue = buildFactory.globalVar(name, tmpType, true, value);
        } else {
            tmpValue = buildFactory.buildVar(tmpType, tmpValue, curBlock);
        }
        symbolTables.addSymbol(name, tmpValue);
    }

    private void visitConstInitVal(ConstInitVal constInitVal) {
        visitConstExp(constInitVal.getConstExp());
    }

    private void visitConstExp(ConstExp constExp) {
        visitAddExp(constExp.getAddExp());
    }


    private void visitVarDecl(VarDecl varDecl) {
        tmpType = IntegerType.i32;
        for (VarDef varDef : varDecl.getVarDefs()) {
            visitVarDef(varDef);
        }
    }

    private void visitVarDef(VarDef varDef) {
        String name = varDef.getIdent().getToken();
        if (varDef.getInitVal() != null) {
            visitInitVal(varDef.getInitVal());
        } else {
            immediate = 0;
        }
        if (isGlobal) {
            Value value = buildFactory.buildConstInt(immediate);
            tmpValue = buildFactory.globalVar(name, tmpType, false, value);
        } else {
            if (varDef.getInitVal() != null) {
                tmpValue = buildFactory.buildVar(tmpType, tmpValue, curBlock);
            } else {
                tmpValue = buildFactory.buildVar(tmpType, null, curBlock);
            }
        }
        symbolTables.addSymbol(name, tmpValue);
    }

    private void visitInitVal(InitVal initVal) {
        visitExp(initVal.getExp());
    }

    private void visitFuncDef(FuncDef funcDef) {
        String name = funcDef.getIdent().getToken();
        ArrayList<Type> argTypes = new ArrayList<>();
        Type retType;
        if (funcDef.getFuncType().getFuncType().getSymbol() == TokenType.VOIDTK) {
            retType = VoidType.voidType;
        } else {
            retType = IntegerType.i32;
        }
        if (funcDef.getFuncFParams() != null) {
            for (FuncFParam funcFParam : funcDef.getFuncFParams().getFuncFParams()) {
                argTypes.add(getFuncFParamType(funcFParam));
            }
        }
        FuncType funcType = new FuncType(retType, argTypes);
        Function function = buildFactory.buildFunction(name, funcType);
        curFunction = function;
        symbolTables.addSymbol(name, function);
        symbolTables.addSymbolTable();
        curBlock = buildFactory.buildBasicBlock(function);
        if (funcDef.getFuncFParams() != null) {
            int index = 0;
            for (FuncFParam funcFParam : funcDef.getFuncFParams().getFuncFParams()) {
                visitFuncFParam(funcFParam, function.getKArg(index++));
            }
        }
        visitBlock(funcDef.getBlock());
        symbolTables.rmSymbolTable();
    }

    private Type getFuncFParamType(FuncFParam funcFParam) {
        Type type = null;
        if (!funcFParam.isArray()) {
            type = IntegerType.i32;
        }
        return type;
    }

    private void visitFuncFParam(FuncFParam funcFParam, Argument arg) {
        String name = funcFParam.getIdent().getToken();
        symbolTables.addSymbol(name, buildFactory.buildVar(arg.getType(), arg, curBlock));
    }

    private void visitMainFuncDef(MainFuncDef mainFuncDef) {
        FuncType funcType = buildFactory.buildFuncType(new IntegerType(32), new ArrayList<>());
        Function function = buildFactory.buildFunction("main", funcType);
        curFunction = function;
        symbolTables.addSymbol("main", function);
        symbolTables.addSymbolTable();
        curBlock = buildFactory.buildBasicBlock(curFunction);
        visitBlock(mainFuncDef.getBlock());
        symbolTables.rmSymbolTable();
    }

    private void visitBlock(Block block) {
        for (BlockItem blockItem : block.getBlockItems()) {
            visitBlockItem(blockItem);
        }
    }

    private void visitBlockItem(BlockItem blockItem) {
        if (blockItem.getDecl() != null) {
            visitDecl(blockItem.getDecl());
        } else {
            visitStmt(blockItem.getStmt());
        }
    }

    private void visitStmt(Stmt stmt) {
        if (stmt instanceof ReturnStmt returnStmt) {
            if (returnStmt.getExp() != null) {
                visitExp(returnStmt.getExp());
                buildFactory.buildRetInst(curBlock, tmpValue);
            } else {
                buildFactory.buildRetInst(curBlock);
            }
        } else if (stmt instanceof BlockStmt blockStmt) {
            symbolTables.addSymbolTable();
            visitBlock(blockStmt.getBlock());
        } else if (stmt instanceof AssignExpStmt assignExpStmt) {
            doLValAssignExp(assignExpStmt.getlVal(), assignExpStmt.getExp());
        } else if (stmt instanceof AssignGetintStmt assignGetintStmt) {
            doGetInt(assignGetintStmt);
        } else if (stmt instanceof PrintfStmt printfStmt) {
            doPrintf(printfStmt);
        } else if (stmt instanceof ExpStmt expStmt) {
            visitExp(expStmt.getExp());
        } else if (stmt instanceof IfStmt ifStmt) {
            doIfStmt(ifStmt);
        } else if (stmt instanceof For stmtFor) {
            doFor(stmtFor);
        } else if (stmt instanceof BreakStmt) {
            doBreak();
        } else if (stmt instanceof ContinueStmt) {
            doContinue();
        }
    }

    private void visitForStmt(ForStmt forStmt) {
        doLValAssignExp(forStmt.getlVal(), forStmt.getExp());
    }

    private void doLValAssignExp(LVal lVal, Exp exp) {
        visitExp(exp);
        if (lVal.getExps().isEmpty()) {
            String name = lVal.getIdent().getToken();
            Value pointer = symbolTables.getValue(name);
            tmpValue = buildFactory.storeInst(curBlock, tmpValue, pointer);
        }
    }

    private void doContinue() {
        buildFactory.brInst(curBlock, curEndBlock);
    }

    private void doBreak() {
        buildFactory.brInst(curBlock, curForEndBlock);
    }

    private void doFor(For stmtFor) {
        BasicBlock tmpTrueBlock = curTrueBlock;
        BasicBlock tmpFalseBlock = curFalseBlock;
        visitForStmt(stmtFor.getForStmt1());//forStmt1
        BasicBlock condBlock = buildFactory.buildBasicBlock(curFunction);//cond
        BasicBlock endBlock = buildFactory.unnamedBasicBlock();//forStmt2
        BasicBlock trueBlock = buildFactory.unnamedBasicBlock();//stmt
        BasicBlock falseBlock = buildFactory.unnamedBasicBlock();//后继块
        buildFactory.brInst(curBlock, condBlock);//没有新的变量引入，可以不重填
        //cond
        curBlock = condBlock;
        curTrueBlock = trueBlock;
        curFalseBlock = falseBlock;
        curEndBlock = endBlock;
        curForEndBlock = falseBlock;
        visitCond(stmtFor.getCond());//cond内会处理跳转
        //stmt
        trueBlock.refill(curFunction);
        curBlock = trueBlock;
        visitStmt(stmtFor.getStmt());
        buildFactory.brInst(curBlock, endBlock);
        //forStmt2
        endBlock.refill(curFunction);
        curBlock = endBlock;
        visitForStmt(stmtFor.getForStmt2());
        buildFactory.brInst(curBlock, condBlock);
        //跳到之后的basicBlock
        falseBlock.refill(curFunction);
        curBlock = falseBlock;
        curTrueBlock = tmpTrueBlock;
        curFalseBlock = tmpFalseBlock;
    }

    private void doGetInt(AssignGetintStmt assignGetintStmt) {
        LVal lVal = assignGetintStmt.getlVal();
        if (lVal.getExps().isEmpty()) {
            Value val = symbolTables.getValue(lVal.getIdent().getToken());
            Function function = (Function) symbolTables.getValue("getint");
            tmpValue = buildFactory.callInst(curBlock, function, new ArrayList<>());
            tmpValue = buildFactory.storeInst(curBlock, tmpValue, val);
        }
    }

    private void doIfStmt(IfStmt ifStmt) {
        BasicBlock tmpTrueBlock = curTrueBlock;
        BasicBlock tmpFalseBlock = curFalseBlock;
        if (ifStmt.getStmt2() == null) {
            BasicBlock trueBlock = buildFactory.unnamedBasicBlock();
            BasicBlock falseBlock = buildFactory.unnamedBasicBlock();
            curTrueBlock = trueBlock;
            curFalseBlock = falseBlock;
            visitCond(ifStmt.getCond());
            trueBlock.refill(curFunction);
            curBlock = trueBlock;
            visitStmt(ifStmt.getStmt1());
            buildFactory.brInst(curBlock, falseBlock);
            falseBlock.refill(curFunction);
            curBlock = falseBlock;
        } else {
            BasicBlock trueBlock = buildFactory.unnamedBasicBlock();
            BasicBlock falseBlock = buildFactory.unnamedBasicBlock();
            BasicBlock endBlock = buildFactory.unnamedBasicBlock();
            curTrueBlock = trueBlock;
            curFalseBlock = falseBlock;
            visitCond(ifStmt.getCond());
            trueBlock.refill(curFunction);
            curBlock = trueBlock;
            visitStmt(ifStmt.getStmt1());
            buildFactory.brInst(curBlock, endBlock);
            falseBlock.refill(curFunction);
            curBlock = falseBlock;
            visitStmt(ifStmt.getStmt2());
            buildFactory.brInst(curBlock, endBlock);
            endBlock.refill(curFunction);
            curBlock = endBlock;
        }
        curTrueBlock = tmpTrueBlock;
        curFalseBlock = tmpFalseBlock;
    }

    private void doPrintf(PrintfStmt printfStmt) {
        Function putCh = (Function) symbolTables.getValue("putch");
        Function putInt = (Function) symbolTables.getValue("putint");
        String format = printfStmt.getFormatString().getToken();
        int cnt = 0;
        for (int i = 1; i < format.length() - 1; i++) {
            char c = format.charAt(i);
            ArrayList<Value> args = new ArrayList<>();
            if (c == '%') {
                i++;//%d
                visitExp(printfStmt.getExps().get(cnt++));
                args.add(tmpValue);
                tmpValue = buildFactory.callInst(curBlock, putInt, args);
            } else if (c == '\\') {
                i++;// \n
                args.add(buildFactory.buildConstInt('\n'));
                tmpValue = buildFactory.callInst(curBlock, putCh, args);
            } else {
                args.add(buildFactory.buildConstInt(c));
                tmpValue = buildFactory.callInst(curBlock, putCh, args);
            }
        }
    }

    private void visitCond(Cond cond) {
        visitLOrExp(cond.getlOrExp());
    }

    private void visitLOrExp(LOrExp lOrExp) {
        BasicBlock trueBlock = curTrueBlock;//类似压栈弹栈的操作
        BasicBlock falseBlock = curFalseBlock;
        int len = lOrExp.getlAndExps().size();
        for (int i = 0; i < len - 1; i++) {
            BasicBlock thenBlock = buildFactory.unnamedBasicBlock();
            curFalseBlock = thenBlock;
            visitLAndExp(lOrExp.getlAndExps().get(i));
            thenBlock.refill(curFunction);//暂定采用回填方式解决
            curBlock = thenBlock;
        }
        visitLAndExp(lOrExp.getlAndExps().get(len - 1));
        curTrueBlock = trueBlock;
        curFalseBlock = falseBlock;
    }

    private void visitLAndExp(LAndExp lAndExp) {
        BasicBlock trueBlock = curTrueBlock;
        BasicBlock falseBlock = curFalseBlock;
        int len = lAndExp.getEqExps().size();
        for (int i = 0; i < len - 1; i++) {
            visitEqExp(lAndExp.getEqExps().get(i));
            BasicBlock thenBlock = buildFactory.buildBasicBlock(curFunction);
            buildFactory.brInst(curBlock, thenBlock, curFalseBlock, tmpValue);
            curBlock = thenBlock;
        }
        visitEqExp(lAndExp.getEqExps().get(len - 1));
        buildFactory.brInst(curBlock, trueBlock, falseBlock, tmpValue);
        curTrueBlock = trueBlock;
        curFalseBlock = falseBlock;
    }

    private void visitEqExp(EqExp eqExp) {
        visitRelExp(eqExp.getRelExps().get(0));
        if (eqExp.getRelExps().size() == 1) {
            if (((BinaryInst) tmpValue).isNumber()) {
                tmpValue = buildFactory.binaryInst(curBlock, Operator.Ne, tmpValue, ConstInt.ZERO);
            }
            return;
        }
        for (int i = 1; i < eqExp.getRelExps().size(); i++) {
            Value prevAns = tmpValue;
            visitRelExp(eqExp.getRelExps().get(i));
            Operator op = null;
            switch (eqExp.getOps().get(i - 1).getSymbol()) {
                case EQL -> op = Operator.Eq;
                case NEQ -> op = Operator.Ne;
            }
            tmpValue = buildFactory.binaryInst(curBlock, op, prevAns, tmpValue);
        }
    }

    private void visitRelExp(RelExp relExp) {
        visitAddExp(relExp.getAddExps().get(0));
        for (int i = 1; i < relExp.getAddExps().size(); i++) {
            Value prevAns = tmpValue;
            visitAddExp(relExp.getAddExps().get(i));
            Operator op = null;
            switch (relExp.getOps().get(i - 1).getSymbol()) {
                case LSS -> op = Operator.Slt;//<
                case LEQ -> op = Operator.Sle;//<=
                case GRE -> op = Operator.Sgt;//>
                case GEQ -> op = Operator.Sge;//>=
            }
            tmpValue = buildFactory.binaryInst(curBlock, op, prevAns, tmpValue);
        }
    }


    private void visitExp(Exp exp) {
        visitAddExp(exp.getAddExp());
    }

    private void visitAddExp(AddExp addExp) {
        ArrayList<MulExp> mulExps = addExp.getMulExps();
        visitMulExp(mulExps.get(0));
        for (int i = 1; i < mulExps.size(); i++) {
            int keep = immediate;
            Value prevAns = tmpValue;
            visitMulExp(mulExps.get(i));
            if (isGlobal) {
                immediate = calculate(keep, immediate, addExp.getOps().get(i - 1).getSymbol());
            } else {
                Operator op = null;
                switch (addExp.getOps().get(i - 1).getSymbol()) {
                    case PLUS -> op = Operator.Add;
                    case MINU -> op = Operator.Sub;
                }
                tmpValue = buildFactory.binaryInst(curBlock, op, prevAns, tmpValue);
            }
        }
    }

    private void visitMulExp(MulExp mulExp) {
        ArrayList<UnaryExp> unaryExps = mulExp.getUnaryExps();
        ArrayList<Token> ops = mulExp.getOps();
        visitUnaryExp(unaryExps.get(0));
        Operator op = null;
        for (int i = 1; i < unaryExps.size(); i++) {
            Value prevAns = tmpValue;
            int keep = immediate;
            visitUnaryExp(unaryExps.get(i));
            if (isGlobal) {
                immediate = calculate(keep, immediate, mulExp.getOps().get(i - 1).getSymbol());
            } else {
                switch (ops.get(i - 1).getSymbol()) {
                    case MULT -> op = Operator.Mul;
                    case DIV -> op = Operator.SDiv;
                    case MOD -> op = Operator.Mod;
                }
                tmpValue = buildFactory.binaryInst(curBlock, op, prevAns, tmpValue);
            }
        }
    }

    private void visitUnaryExp(UnaryExp unaryExp) {
        if (unaryExp.getPrimaryExp() != null) {
            visitPrimaryExp(unaryExp.getPrimaryExp());
        } else if (unaryExp.getUnaryExp() != null) {
            visitUnaryExp(unaryExp.getUnaryExp());
            TokenType op = unaryExp.getUnaryOp().getOp().getSymbol();
            if (op == TokenType.MINU) {
                if (isGlobal) {
                    immediate = -immediate;
                } else {
                    tmpValue = buildFactory.binaryInst(curBlock, Operator.Sub, ConstInt.ZERO, tmpValue);
                }
            }
        } else {
            String name = unaryExp.getIdent().getToken();
            Function func = (Function) symbolTables.getValue(name);
            ArrayList<Value> args = new ArrayList<>();
            if (unaryExp.getFuncRParams() != null) {
                for (Exp exp : unaryExp.getFuncRParams().getExps()) {
                    visitExp(exp);
                    args.add(tmpValue);
                }
            }
            tmpValue = buildFactory.callInst(curBlock, func, args);
        }
    }

    private void visitPrimaryExp(PrimaryExp primaryExp) {
        if (primaryExp.getExp() != null) {
            visitExp(primaryExp.getExp());
        } else if (primaryExp.getNumber() != null) {
            visitNumber(primaryExp.getNumber());
        } else {
            visitLVal(primaryExp.getlVal());
        }
    }

    private void visitNumber(Number number) {
        if (isGlobal) {
            immediate = number.getVal();
        } else {
            tmpValue = new ConstInt(number.getVal());
        }
    }

    private void visitLVal(LVal lVal) {
        String name = lVal.getIdent().getToken();
        if (lVal.getExps().isEmpty()) {
            Value pointer = symbolTables.getValue(name);
            if (isGlobal) {
                immediate = ((ConstInt) ((GlobalVar) pointer).getValue()).getVal();
            } else {
                tmpValue = buildFactory.loadInst(curBlock, pointer);
            }
        }
    }
}
