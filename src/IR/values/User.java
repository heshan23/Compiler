package IR.values;

import IR.types.Type;

import java.util.ArrayList;

public class User extends Value {
    private ArrayList<Value> operands;

    public User(String name, Type type) {
        super(name, type);
        this.operands = new ArrayList<>();
    }

    public ArrayList<Value> getOperands() {
        return operands;
    }

    public void addOperands(Value value) {
        this.operands.add(value);
    }
}
