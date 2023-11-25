package IR.types;

public class ArrayType implements Type {
    private final Type elementType;
    private final int len;

    public ArrayType(Type elementType, int len) {
        this.elementType = elementType;
        this.len = len;
    }

    public int getLen() {
        return len;
    }

    public Type getElementType() {
        return elementType;
    }

    public boolean is1dimArray() {
        return this.elementType == IntegerType.i32;
    }

    @Override
    public String toString() {
        return String.format("[%d x %s]", len, elementType);
    }
}
