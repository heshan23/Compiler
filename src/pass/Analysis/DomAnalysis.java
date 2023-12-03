package pass.Analysis;

import IR.IRModule;
import IR.values.BasicBlock;
import IR.values.Function;
import IR.values.Value;
import IR.values.instructions.Instruction;
import IR.values.instructions.terminator.BrInst;

import java.util.HashSet;

public class DomAnalysis {
    private static final DomAnalysis domAnalysis = new DomAnalysis();

    public static DomAnalysis getInstance() {
        return domAnalysis;
    }

    public void analyze(IRModule irModule) {
        for (Function function : irModule.getFunctions()) {
            calcDom(function);
            calcIdom(function);
            calcDF(function);
        }
    }

    private void calcDom(Function function) {
        BasicBlock entry = function.getBasicBlocks().get(0);
        for (BasicBlock target : function.getBasicBlocks()) {
            HashSet<BasicBlock> reachedBB = new HashSet<>();
            notDomedByTarget(target, entry, reachedBB);
            HashSet<BasicBlock> dom = new HashSet<>();
            for (BasicBlock bb : function.getBasicBlocks()) {
                if (!reachedBB.contains(bb)) {
                    dom.add(bb);
                }
            }
            target.setDom(dom);
        }
    }

    private void notDomedByTarget(Value target, BasicBlock entry, HashSet<BasicBlock> reachedBB) {
        if (entry == target) {
            return;
        }
        reachedBB.add(entry);
        for (BasicBlock bb : entry.getNext()) {
            if (!reachedBB.contains(bb)) {
                notDomedByTarget(target, bb, reachedBB);
            }
        }
    }

    private boolean dom(BasicBlock a, BasicBlock b) {
        return a.getDom().contains(b);
    }

    private boolean strictDom(BasicBlock a, BasicBlock b) {
        return a != b && dom(a, b);
    }

    private void calcIdom(BasicBlock it) {
        for (BasicBlock basicBlock1 : it.getDom()) {
            if (basicBlock1 == it) {//确保严格支配
                continue;
            }
            boolean flot = true;
            for (BasicBlock basicBlock2 : it.getDom()) {
                if (basicBlock2 == it) {
                    continue;
                }
                if (strictDom(basicBlock2, basicBlock1)) {
                    flot = false;
                    break;
                }
            }
            if (flot) {
                basicBlock1.setIdom(it);
            }
        }
    }

    private void calcIdom(Function function) {
        for (BasicBlock basicBlock : function.getBasicBlocks()) {
            calcIdom(basicBlock);
        }
    }

    private void calcDF(Function function) {
        for (BasicBlock a : function.getBasicBlocks()) {
            for (BasicBlock b : a.getNext()) {
                BasicBlock x = a;
                while (!strictDom(x, b)) {
                    x.addDF(b);
                    x = x.getIdom();
                }
            }
        }
    }
}
