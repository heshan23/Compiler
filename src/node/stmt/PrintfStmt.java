package node.stmt;

import IO.OutputHandler;
import node.expression.Exp;
import token.Token;
import token.TokenType;

import java.util.ArrayList;

public class PrintfStmt implements Stmt {
    //| 'printf''('FormatString{','Exp}')'';' // 1.有Exp 2.无Exp
    private Token formatString;
    private ArrayList<Exp> exps;

    public PrintfStmt(Token formatString, ArrayList<Exp> exps) {
        this.formatString = formatString;
        this.exps = exps;
    }

    @Override
    public void print() {
        OutputHandler.printToken(TokenType.PRINTFTK);
        OutputHandler.printToken(TokenType.LPARENT);
        OutputHandler.printToken(formatString);
        for (Exp exp : exps) {
            OutputHandler.printToken(TokenType.COMMA);
            exp.print();
        }
        OutputHandler.printToken(TokenType.RPARENT);
        OutputHandler.printToken(TokenType.SEMICN);
        OutputHandler.println("<Stmt>");
    }
}
