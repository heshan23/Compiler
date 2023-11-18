package IR.SymbolTable;

import IR.values.Value;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private ArrayList<HashMap<String, Value>> symbolTables;

    public SymbolTable() {
        this.symbolTables = new ArrayList<>();
    }

    public void addSymbolTable() {
        this.symbolTables.add(new HashMap<>());
    }

    public void rmSymbolTable() {
        this.symbolTables.remove(symbolTables.size() - 1);
    }

    public void addSymbol(String name, Value value) {
        this.symbolTables.get(symbolTables.size() - 1).put(name, value);
    }

    public Value getValue(String name) {
        for (int i = symbolTables.size() - 1; i >= 0; i--) {
            if (symbolTables.get(i).containsKey(name)) {
                return symbolTables.get(i).get(name);
            }
        }
        return null;
    }
}
