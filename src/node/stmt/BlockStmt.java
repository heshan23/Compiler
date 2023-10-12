package node.stmt;

import IO.OutputHandler;
import node.Block;

public class BlockStmt implements Stmt {
    //Block
    private Block block;

    public BlockStmt(Block block) {
        this.block = block;
    }

    @Override
    public void print() {
        block.print();
        OutputHandler.println("<Stmt>");
    }
}
