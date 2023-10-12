package node.expression;

import IO.OutputHandler;
import node.LVal;
import node.Number;
import token.Token;
import token.TokenType;

public class PrimaryExp {
    //PrimaryExp → '(' Exp ')' | LVal | Number
    private Exp exp;
    private LVal lVal;
    private Number number;

    public PrimaryExp(Exp exp) {
        this.exp = exp;
    }

    public PrimaryExp(LVal lVal) {
        this.lVal = lVal;
    }

    public PrimaryExp(Number number) {
        this.number = number;
    }

    public void print() {
        if (exp != null) {
            OutputHandler.printToken(TokenType.LPARENT);
            exp.print();
            OutputHandler.printToken(TokenType.RPARENT);
        } else if (lVal != null) {
            lVal.print();
        } else {
            number.print();
        }
        OutputHandler.println("<PrimaryExp>");
    }
}
