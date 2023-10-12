package node.expression;

import IO.OutputHandler;
import token.Token;

import java.util.ArrayList;

public class MulExp {
    //MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
    private ArrayList<UnaryExp> unaryExps;
    private ArrayList<Token> ops;

    public MulExp(ArrayList<UnaryExp> unaryExps, ArrayList<Token> ops) {
        this.unaryExps = unaryExps;
        this.ops = ops;
    }

    public void print() {
        unaryExps.get(0).print();
        for (int i = 1; i < unaryExps.size(); i++) {
            OutputHandler.printToken(ops.get(i - 1));
            unaryExps.get(i).print();
        }
        OutputHandler.println("<MulExp>");
    }
}
