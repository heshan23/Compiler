package node;

import IO.OutputHandler;
import token.TokenType;

import java.util.ArrayList;

public class FuncFParams {
    //FuncFParams → FuncFParam { ',' FuncFParam }
    private ArrayList<FuncFParam> funcFParams;

    public FuncFParams(ArrayList<FuncFParam> funcFParams) {
        this.funcFParams = funcFParams;
    }

    public void print() {
        funcFParams.get(0).print();
        for (int i = 1; i < funcFParams.size(); i++) {
            OutputHandler.printToken(TokenType.COMMA);
            funcFParams.get(i).print();
        }
        OutputHandler.println("<FuncFParams>");
    }
}
