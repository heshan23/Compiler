package node;

import IO.OutputHandler;
import node.expression.Exp;

import java.util.ArrayList;

public class FuncRParams {
    //FuncRParams → Exp { ',' Exp }
    private ArrayList<Exp> exps;

    public FuncRParams(ArrayList<Exp> exps) {
        this.exps = exps;
    }

    public void print() {
        exps.get(0).print();
        for (int i = 1; i < exps.size(); i++) {
            exps.get(i).print();
        }
        OutputHandler.println("<FuncRParams>");
    }
}
