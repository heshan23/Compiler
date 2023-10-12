package node;

import IO.OutputHandler;
import token.TokenType;

import java.util.ArrayList;

public class VarDecl implements Decl {
    //VarDecl → BType VarDef { ',' VarDef } ';'
    private BType bType;
    private ArrayList<VarDef> varDefs;

    public VarDecl(BType bType, ArrayList<VarDef> varDefs) {
        this.bType = bType;
        this.varDefs = varDefs;
    }

    public void print() {
        bType.print();
        varDefs.get(0).print();
        for (int i = 1; i < varDefs.size(); i++) {
            OutputHandler.printToken(TokenType.COMMA);
            varDefs.get(i).print();
        }
        OutputHandler.printToken(TokenType.SEMICN);
        OutputHandler.println("<VarDecl>");
    }
}
