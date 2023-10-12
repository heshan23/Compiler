package node;

import IO.OutputHandler;
import node.expression.Exp;
import token.Token;
import token.TokenType;

import java.util.ArrayList;

public class LVal {
    //LVal → Ident {'[' Exp ']'}
    private Token Ident;
    private ArrayList<Exp> exps;

    public LVal(Token Ident, ArrayList<Exp> exps) {
        this.Ident = Ident;
        this.exps = exps;
    }

    public void print() {
        OutputHandler.printToken(Ident);
        for (Exp exp : exps) {
            OutputHandler.printToken(TokenType.LBRACK);
            exp.print();
            OutputHandler.printToken(TokenType.RBRACK);
        }
        OutputHandler.println("<LVal>");
    }
}
