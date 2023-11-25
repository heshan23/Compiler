package IR.values.instructions.men;

import IR.types.ArrayType;
import IR.types.PointerType;
import IR.types.Type;
import IR.values.BasicBlock;
import IR.values.Value;
import IR.values.instructions.Operator;

import java.util.ArrayList;

public class GEPInst extends MemInst {
    private ArrayList<Value> indices;
    private Type target;

    public GEPInst(Value pointer, ArrayList<Value> indices, BasicBlock basicBlock) {
        super(new PointerType(myType(pointer, indices.size())), Operator.GEP, basicBlock);
        setName("%" + valNumber++);
        addOperands(pointer);
        this.indices = indices;
        this.target = ((PointerType) pointer.getType()).getTargetType();

    }

    private static Type myType(Value pointer, int dim) {
        Type ans = ((PointerType) pointer.getType()).getTargetType();
        if (ans instanceof ArrayType) {
            for (int i = 1; i < dim; i++) {
                ans = ((ArrayType) ans).getElementType();
            }
        }
        return ans;
    }

    private Value getPointer() {
        return getOperands().get(0);
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder(getName());
        res.append(" = getelementptr ");
        res.append(target).append(", ");
        res.append(getPointer().getType()).append(" ");
        res.append(getPointer().getName()).append(", ");
        res.append(indices.get(0).toString());
        for (int i = 1; i < indices.size(); i++) {
            res.append(", ").append(indices.get(i));
        }
        return res.toString();
    }
}
