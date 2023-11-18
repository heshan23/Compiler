package IR;

import IR.types.FuncType;
import IR.types.Type;
import IR.values.*;
import IR.values.instructions.*;
import IR.values.instructions.men.AllocInst;
import IR.values.instructions.men.LoadInst;
import IR.values.instructions.terminator.BrInst;
import IR.values.instructions.terminator.RetInst;

import java.util.ArrayList;
import java.util.Collections;


public class BuildFactory {
    private static final BuildFactory buildFactory = new BuildFactory();

    public static BuildFactory getInstance() {
        return buildFactory;
    }

    public FuncType buildFuncType(Type retType, ArrayList<Type> paramsType) {
        return new FuncType(retType, paramsType);
    }

    public Function buildFunction(String name, FuncType funcType) {
        return new Function(name, funcType, false);
    }

    public Function buildLibFunc(String name, Type retType, ArrayList<Type> params) {
        FuncType funcType = new FuncType(retType, params);
        return new Function(name, funcType, true);
    }

    public Function buildOneParamLibFunc(String name, Type retType, Type paramType) {
        ArrayList<Type> params = new ArrayList<>(Collections.singleton(paramType));
        return buildLibFunc(name, retType, params);
    }

    public BasicBlock buildBasicBlock(Function function) {
        return new BasicBlock(function);
    }

    public BasicBlock unnamedBasicBlock() {
        return new BasicBlock();
    }

    public RetInst buildRetInst(BasicBlock basicBlock) {
        return new RetInst(basicBlock);
    }

    public RetInst buildRetInst(BasicBlock basicBlock, Value value) {
        return new RetInst(basicBlock, value);
    }

    public ConstInt buildConstInt(int val) {
        return new ConstInt(val);
    }

    public GlobalVar globalVar(String name, Type type, boolean isCon, Value value) {
        return new GlobalVar(name, type, isCon, value);
    }

    public AllocInst buildVar(Type type, Value value, BasicBlock basicBlock) {
        AllocInst allocInst = new AllocInst(type, basicBlock);
        if (value != null) {
            storeInst(basicBlock, value, allocInst);
        }
        return allocInst;
    }

    public StoreInst storeInst(BasicBlock basicBlock, Value val, Value ptr) {
        return new StoreInst(basicBlock, val, ptr);
    }

    public BinaryInst binaryInst(BasicBlock basicBlock, Operator op, Value lVal, Value rVal) {
        return new BinaryInst(basicBlock, op, lVal, rVal);
    }

    public LoadInst loadInst(BasicBlock basicBlock, Value pointer) {
        return new LoadInst(basicBlock, pointer);
    }

    public CallInst callInst(BasicBlock basicBlock, Function function, ArrayList<Value> args) {
        return new CallInst(basicBlock, function, args);
    }

    public BrInst brInst(BasicBlock basicBlock, BasicBlock trueBlock) {
        return new BrInst(basicBlock, trueBlock);
    }

    public BrInst brInst(BasicBlock basicBlock, BasicBlock trueBlock,
                         BasicBlock falseBlock, Value cond) {
        return new BrInst(basicBlock, trueBlock, falseBlock, cond);
    }

    public ConvInst buildZext(BasicBlock basicBlock) {
        return new ConvInst(Operator.Zext, basicBlock);
    }

}
