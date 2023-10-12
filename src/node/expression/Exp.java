package node.expression;

import IO.OutputHandler;

public class Exp {
    private AddExp addExp;

    public Exp(AddExp addExp) {
        this.addExp = addExp;
    }

    public void print() {
        addExp.print();
        OutputHandler.println("<Exp>");
    }
}
