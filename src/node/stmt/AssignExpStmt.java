package node.stmt;

import IO.OutputHandler;
import node.expression.Exp;
import node.LVal;
import token.TokenType;

public class AssignExpStmt implements Stmt {
    //LVal '=' Exp ';'
    private LVal lVal;
    private Exp exp;

    public AssignExpStmt(LVal lVal, Exp exp) {
        this.lVal = lVal;
        this.exp = exp;
    }

    @Override
    public void print() {
        lVal.print();
        OutputHandler.printToken(TokenType.ASSIGN);
        exp.print();
        OutputHandler.printToken(TokenType.SEMICN);
        OutputHandler.println("<Stmt>");
    }
}
