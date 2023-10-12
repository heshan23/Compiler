package node;

import IO.OutputHandler;
import node.expression.ConstExp;
import token.Token;
import token.TokenType;

import java.util.ArrayList;

public class ConstDef {
    //ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal
    private Token Ident;
    private ArrayList<ConstExp> constExps;
    private ConstInitVal constInitVal;

    public ConstDef(Token Ident, ArrayList<ConstExp> constExps,
                    ConstInitVal constInitVal) {
        this.Ident = Ident;
        this.constInitVal = constInitVal;
        this.constExps = constExps;
    }

    public void print() {
        OutputHandler.printToken(Ident);
        for (ConstExp constExp : constExps) {
            OutputHandler.printToken(TokenType.LBRACK);
            constExp.print();
            OutputHandler.printToken(TokenType.RBRACK);
        }
        OutputHandler.printToken(TokenType.ASSIGN);
        constInitVal.print();
        OutputHandler.println("<ConstDef>");
    }
}
