package node.stmt;

import IO.OutputHandler;
import token.TokenType;

public class BreakStmt implements Stmt {
    //'break' ';'
    @Override
    public void print() {
        OutputHandler.printToken(TokenType.BREAKTK);
        OutputHandler.printToken(TokenType.SEMICN);
        OutputHandler.println("<Stmt>");
    }
}
