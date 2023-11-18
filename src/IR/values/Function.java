package IR.values;


import IO.OutputHandler;
import IR.IRModule;
import IR.types.FuncType;
import IR.types.IntegerType;
import IR.types.Type;

import java.util.ArrayList;

public class Function extends Value {
    private final boolean isLibrary;
    private final ArrayList<Argument> arguments;
    private final ArrayList<BasicBlock> basicBlocks = new ArrayList<>();

    public Function(String name, Type type, boolean isLibrary) {
        super(name, type);
        valNumber = 0;
        this.isLibrary = isLibrary;
        this.arguments = new ArrayList<>();
        addAllArguments();
        if (!isLibrary) {
            IRModule.getInstance().addFunction(this);
        }

    }

    public boolean isLibrary() {
        return isLibrary;
    }

    public void addBasicBlock(BasicBlock basicBlock) {
        this.basicBlocks.add(basicBlock);
    }

    public void addAllArguments() {
        for (Type type : ((FuncType) getType()).getParamsType()) {
            this.arguments.add(new Argument(type));
        }
    }

    public Argument getKArg(int k) {
        return arguments.get(k);
    }

    public void genLLVM() {
        String retType = ((FuncType) getType()).getRetType().toString();
        StringBuilder args = new StringBuilder();
        if (!arguments.isEmpty()) {
            args.append(arguments.get(0).toString());
            for (int i = 1; i < arguments.size(); i++) {
                args.append(", ").append(arguments.get(i).toString());
            }
        }
        OutputHandler.genLLVM(String.format("define dso_local %s @%s(%s) {", retType, getName(), args));
        for (BasicBlock basicBlock : basicBlocks) {
            basicBlock.genLLVM();
        }
        OutputHandler.genLLVM("}");
    }
}
