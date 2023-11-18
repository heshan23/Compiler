package IR.types;

public class IntegerType implements Type {
    private int bit;

    /*
    i1
    i8
    i32
     */
    public IntegerType(int bit) {
        this.bit = bit;
    }

    public static final IntegerType i32 = new IntegerType(32);
    public static final IntegerType i8 = new IntegerType(8);
    public static final IntegerType i1 = new IntegerType(1);

    @Override
    public String toString() {
        return "i" + bit;
    }
}
