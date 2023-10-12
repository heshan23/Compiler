package node;

import IO.OutputHandler;
import node.BlockItem;
import node.stmt.BlockStmt;
import node.stmt.Stmt;
import token.TokenType;

import java.util.ArrayList;

public class Block {
    // Block → '{' { BlockItem } '}'
    private ArrayList<BlockItem> blockItems;

    public Block(ArrayList<BlockItem> blockItems) {
        this.blockItems = blockItems;
    }

    public void print() {
        OutputHandler.printToken(TokenType.LBRACE);
        for (BlockItem blockItem : blockItems) {
            blockItem.print();
        }
        OutputHandler.printToken(TokenType.RBRACE);
        OutputHandler.println("<Block>");
    }
}
