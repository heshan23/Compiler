package node;

import IO.OutputHandler;
import node.expression.ConstExp;
import token.Token;
import token.TokenType;

import java.util.ArrayList;

public class FuncFParam {
    //FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
    private BType bType;
    private Token Ident;
    private ArrayList<ConstExp> constExps;
    private boolean isArray;

    public FuncFParam(BType bType, Token Ident,
                      ArrayList<ConstExp> constExps, boolean isArray) {
        this.bType = bType;
        this.Ident = Ident;
        this.constExps = constExps;
        this.isArray = isArray;
    }

    public void print() {
        bType.print();
        OutputHandler.printToken(Ident);
        if (isArray) {
            OutputHandler.printToken(TokenType.LBRACK);
            OutputHandler.printToken(TokenType.RBRACK);
            for (ConstExp constExp : constExps) {
                OutputHandler.printToken(TokenType.LBRACK);
                constExp.print();
                OutputHandler.printToken(TokenType.RBRACK);
            }
        }
        OutputHandler.println("<FuncFParam>");
    }
}
