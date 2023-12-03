package pass.Analysis;

import IR.IRModule;
import IR.values.BasicBlock;
import IR.values.Function;
import IR.values.Value;

import java.util.HashSet;
import java.util.Iterator;

public class DelDeadBB {
    private final IRModule irModule;

    public DelDeadBB(IRModule irModule) {
        this.irModule = irModule;
    }

    public void run() {
        for (Function function : irModule.getFunctions()) {
            HashSet<BasicBlock> reachedBB = new HashSet<>();
            dfs(function.getBasicBlocks().get(0), reachedBB);
            function.getBasicBlocks().removeIf(bb -> !reachedBB.contains(bb));
        }
    }

    private void dfs(BasicBlock entry, HashSet<BasicBlock> reachedBB) {
        reachedBB.add(entry);
        for (BasicBlock bb : entry.getNext()) {
            if (!reachedBB.contains(bb)) {
                dfs(bb, reachedBB);
            }
        }
    }
}
