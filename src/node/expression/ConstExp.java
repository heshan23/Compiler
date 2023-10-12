package node.expression;

import IO.OutputHandler;

public class ConstExp {
    //ConstExp → AddExp
    private AddExp addExp;

    public ConstExp(AddExp addExp) {
        this.addExp = addExp;
    }

    public void print() {
        addExp.print();
        OutputHandler.println("<ConstExp>");
    }
}
