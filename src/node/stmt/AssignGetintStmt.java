package node.stmt;

import IO.OutputHandler;
import node.LVal;
import token.TokenType;

public class AssignGetintStmt implements Stmt {
    //LVal '=' 'getint''('')'';'
    private LVal lVal;

    public AssignGetintStmt(LVal lVal) {
        this.lVal = lVal;
    }

    @Override
    public void print() {
        lVal.print();
        OutputHandler.printToken(TokenType.ASSIGN);
        OutputHandler.printToken(TokenType.GETINTTK);
        OutputHandler.printToken(TokenType.LPARENT);
        OutputHandler.printToken(TokenType.RPARENT);
        OutputHandler.printToken(TokenType.SEMICN);
        OutputHandler.println("<Stmt>");
    }
}
