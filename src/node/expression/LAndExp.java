package node.expression;

import IO.OutputHandler;
import token.TokenType;

import java.util.ArrayList;

public class LAndExp {
    //LAndExp → EqExp | LAndExp '&&' EqExp
    private ArrayList<EqExp> eqExps;

    public LAndExp(ArrayList<EqExp> eqExps) {
        this.eqExps = eqExps;
    }

    public void print() {
        eqExps.get(0).print();
        for (int i = 1; i < eqExps.size(); i++) {
            OutputHandler.println("<LAndExp>");
            OutputHandler.printToken(TokenType.AND);
            eqExps.get(i).print();
        }
        OutputHandler.println("<LAndExp>");
    }
}
