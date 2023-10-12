package node.stmt;

import IO.OutputHandler;
import token.TokenType;

public class ContinueStmt implements Stmt {
    //'continue' ';'

    @Override
    public void print() {
        OutputHandler.printToken(TokenType.CONTINUETK);
        OutputHandler.printToken(TokenType.SEMICN);
        OutputHandler.println("<Stmt>");
    }
}
