package node.stmt;

import IO.OutputHandler;

import node.expression.Exp;
import token.TokenType;

public class ReturnStmt implements Stmt {
    //'return' [Exp] ';'
    private Exp exp;

    public ReturnStmt(Exp exp) {
        this.exp = exp;
    }

    @Override
    public void print() {
        OutputHandler.printToken(TokenType.RETURNTK);
        if (exp != null) {
            exp.print();
        }
        OutputHandler.printToken(TokenType.SEMICN);
        OutputHandler.println("<Stmt>");
    }
}
