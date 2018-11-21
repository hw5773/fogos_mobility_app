package flexid;

public enum FlexIDType {
    DEVICE(0),
    SERVICE(1),
    CONTENT(2),
    GROUP(3);

    private final int type;

    FlexIDType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return new Integer(type).toString();
    }
}
