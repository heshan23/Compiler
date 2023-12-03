package IR.values;


import IR.types.Type;

import java.util.ArrayList;

public class Value {
    private String name;
    private Type type;
    private final ArrayList<Use> useList;
    public static int valNumber = 0;

    public Value(String name, Type type) {
        this.name = name;
        this.type = type;
        this.useList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void addUse(Use use) {
        this.useList.add(use);
    }

    public ArrayList<Use> getUseList() {
        return useList;
    }

    public void replacedByNewVal(Value newVal) {
        for (Use use : useList) {
            use.getUser().replaceVal(use.getPos(), newVal);
            use.setValue(newVal);
            newVal.addUse(use);
        }
    }

    public boolean unUsed() {
        return useList.isEmpty();
    }

    @Override
    public String toString() {
        return type.toString() + ' ' + name;
    }
}
