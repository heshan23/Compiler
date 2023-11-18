package IR.values;


import IR.types.IntegerType;
import IR.types.Type;

public class ConstInt extends Const {
    private final int val;
    public static final ConstInt ZERO = new ConstInt(0);

    public ConstInt(int val) {
        super(String.valueOf(val), new IntegerType(32));
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    @Override
    public String toString() {
        return "i32 " + val;
    }
}
