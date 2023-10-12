package node;

import IO.OutputHandler;
import token.TokenType;

public class MainFuncDef {
    //MainFuncDef → 'int' 'main' '(' ')' Block
    private Block block;

    public MainFuncDef(Block block) {
        this.block = block;
    }

    public void print() {
        OutputHandler.printToken(TokenType.INTTK);
        OutputHandler.printToken(TokenType.MAINTK);
        OutputHandler.printToken(TokenType.LPARENT);
        OutputHandler.printToken(TokenType.RPARENT);
        block.print();
        OutputHandler.println("<MainFuncDef>");
    }
}
