package node.expression;

import IO.OutputHandler;
import node.FuncRParams;
import token.Token;
import token.TokenType;

public class UnaryExp {
    //PrimaryExp | Ident '(' [FuncRParams] ')'
    // | UnaryOp UnaryExp
    private PrimaryExp primaryExp;
    private Token Ident;
    private FuncRParams funcRParams;
    private UnaryOp unaryOp;
    private UnaryExp unaryExp;

    public UnaryExp(PrimaryExp primaryExp) {
        this.primaryExp = primaryExp;
    }

    public UnaryExp(Token Ident, FuncRParams funcRParams) {
        this.Ident = Ident;
        this.funcRParams = funcRParams;
    }

    public UnaryExp(UnaryOp unaryOp, UnaryExp unaryExp) {
        this.unaryOp = unaryOp;
        this.unaryExp = unaryExp;
    }

    public void print() {
        if (primaryExp != null) {
            primaryExp.print();
        } else if (Ident != null) {
            OutputHandler.printToken(Ident);
            OutputHandler.printToken(TokenType.LPARENT);
            if (funcRParams != null) {
                funcRParams.print();
            }
            OutputHandler.printToken(TokenType.RPARENT);
        }else {
            unaryOp.print();
            unaryExp.print();
        }
        OutputHandler.println("<UnaryExp>");
    }
}
