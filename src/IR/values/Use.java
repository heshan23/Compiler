package IR.values;

public class Use {
    private Value value;
    private User user;
    private int pos;

    public Use(Value value, User user, int pos) {
        this.value = value;
        this.user = user;
        this.pos = pos;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public User getUser() {
        return user;
    }

    public int getPos() {
        return pos;
    }
}
