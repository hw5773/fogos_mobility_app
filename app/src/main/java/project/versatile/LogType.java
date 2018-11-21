package project.versatile;

public enum LogType {
    DATA("Data"),
    REBINDING("Rebinding");

    private String type;

    LogType(String type) {
        this.type = type;
    }

    @Override public String toString() { return type; }
}
