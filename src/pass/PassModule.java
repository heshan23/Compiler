package pass;

import IR.IRModule;
import IR.values.Function;
import pass.Analysis.DelDeadBB;
import pass.Analysis.RemovePhi;

public class PassModule {
    private static final PassModule passModule = new PassModule();

    public static PassModule getInstance() {
        return passModule;
    }

    public void run(IRModule irModule) {
        new DelDeadBB(irModule).run();
        new Mem2Reg(irModule).run();
        //new RemovePhi(irModule).run();
        irModule.refreshName();
    }
}
