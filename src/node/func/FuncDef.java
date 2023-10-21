package node.func;

import IO.OutputHandler;
import error.ErrorHandler;
import error.ErrorNode;
import error.ErrorType;
import node.Block;
import symbol.FuncParam;
import symbol.FuncSymbol;
import symbol.Type;
import token.Token;
import token.TokenType;

import java.util.ArrayList;

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

    public void checkError() {
        Token fType = funcType.getFuncType();
        Type type = (fType.getSymbol() == TokenType.INTTK) ? Type.INT : Type.VOID;
        if (ErrorHandler.getInstance().isInCurTable(Ident.getToken())) {
            ErrorHandler.getInstance().addError(new ErrorNode(ErrorType.b, Ident.getLine()));
            return;
        }
        ArrayList<FuncParam> funcParams = new ArrayList<>();
        for (FuncFParam funcFParam : funcFParams.getFuncFParams()) {
            funcParams.add(new FuncParam(funcFParam.getIdent().getToken(), Type.INT, funcFParam.getDimension()));
        }
        ErrorHandler.getInstance().addSymbol(new FuncSymbol(Ident.getToken(), type, funcParams));
        ErrorHandler.getInstance().addSymbolTable(type);
        if (funcFParams != null) {
            funcFParams.checkError();
        }
        block.checkError();
        ErrorHandler.getInstance().removeSymbolTable();
    }
}
