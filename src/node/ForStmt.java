package node;

import IO.OutputHandler;
import node.expression.ConstExp;
import node.expression.Exp;
import token.TokenType;

public class ForStmt {
    //ForStmt → LVal '=' Exp
    private LVal lVal;
    private Exp exp;

    public ForStmt(LVal lVal, Exp exp) {
        this.lVal = lVal;
        this.exp = exp;
    }

    public void print() {
        lVal.print();
        OutputHandler.printToken(TokenType.ASSIGN);
        exp.print();
        OutputHandler.println("<ForStmt>");
    }
}
