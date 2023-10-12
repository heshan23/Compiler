package node;

import IO.OutputHandler;
import token.Token;
import token.TokenType;

public class FuncDef {
    //FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
    private FuncType funcType;
    private Token Ident;
    private FuncFParams funcFParams;
    private Block block;

    public FuncDef(FuncType funcType, Token Ident, FuncFParams funcFParams, Block block) {
        this.funcType = funcType;
        this.Ident = Ident;
        this.funcFParams = funcFParams;
        this.block = block;
    }

    public void print() {
        funcType.print();
        OutputHandler.printToken(Ident);
        OutputHandler.printToken(TokenType.LPARENT);
        if (funcFParams != null) {
            funcFParams.print();
        }
        OutputHandler.printToken(TokenType.RPARENT);
        block.print();
        OutputHandler.println("<FuncDef>");
    }
}
