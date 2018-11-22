package flexid;

public enum FlexIDType {
    ANY(0),
    DEVICE(1),
    SERVICE(2),
    CONTENT(3),
    GROUP(4);

    private final int type;

    FlexIDType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return new Integer(type).toString();
    }
}
