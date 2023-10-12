package node;

import IO.OutputHandler;
import node.expression.ConstExp;
import token.Token;
import token.TokenType;

import java.util.ArrayList;

public class VarDef {
    //VarDef → Ident { '[' ConstExp ']' } // 包含普通变量、一维数组、二维数组定义
    //  | Ident { '[' ConstExp ']' } '=' InitVal
    //因为InitVal等价于ConstInitVal所以这里采用ConstInitVal
    private Token Ident;
    private ArrayList<ConstExp> constExps;
    private InitVal initVal;

    public VarDef(Token Ident, ArrayList<ConstExp> constExps, InitVal initVal) {
        this.Ident = Ident;
        this.constExps = constExps;
        this.initVal = initVal;
    }


    public void print() {
        OutputHandler.printToken(Ident);
        for (ConstExp constExp : constExps) {
            OutputHandler.printToken(TokenType.LBRACK);
            constExp.print();
            OutputHandler.printToken(TokenType.RBRACK);
        }
        if (initVal != null) {
            OutputHandler.printToken(TokenType.ASSIGN);
            initVal.print();
        }
        OutputHandler.println("<VarDef>");
    }

}
