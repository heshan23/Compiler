package pass.Analysis;

import IR.IRModule;
import IR.values.BasicBlock;
import IR.values.Function;
import IR.values.instructions.Instruction;
import IR.values.instructions.men.PhiInst;

import java.util.Iterator;

public class RemovePhi {
    private IRModule irModule;

    public RemovePhi(IRModule irModule) {
        this.irModule = irModule;
    }

    public void run() {
        for (Function function : irModule.getFunctions()) {
            for (BasicBlock basicBlock : function.getBasicBlocks()) {
                basicBlock.getInstructions().removeIf(
                        instr -> instr instanceof PhiInst phiInst && phiInst.unUsed());
            }
        }
    }
}
