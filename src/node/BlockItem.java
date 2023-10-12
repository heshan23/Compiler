package node;

import node.stmt.Stmt;

public class BlockItem {
    //BlockItem → Decl | Stmt
    private Decl decl;
    private Stmt stmt;

    public BlockItem(Decl decl) {
        this.decl = decl;
    }

    public BlockItem(Stmt stmt) {
        this.stmt = stmt;
    }

    public void print() {
        if (decl != null) {
            decl.print();
        } else {
            stmt.print();
        }
    }
}
