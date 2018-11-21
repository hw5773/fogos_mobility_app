package project.versatile;

import android.util.Log;

public class LogItem {
    private LogType type;
    private double value;
    private String from;
    private String to;

    public LogItem(LogType type, double value, String from, String to) {
        this.type = type;
        this.value = value;
        this.from = from;
        this.to = to;
    }

    public LogType getType() {
        return type;
    }

    public void setType(LogType type) {
        this.type = type;
    }

    public String getValue() {
        String ret = null;

        if (type == LogType.DATA)
            ret = value + " bytes";
        else
            ret = value + " ms";

        return ret;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
