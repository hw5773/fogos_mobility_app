package flexid;

public class Locator {
    private InterfaceType type;
    private String addr;
    private int port;

    public Locator(InterfaceType type, String addr, int port) {
        this.type = type;
        this.addr = addr;
        this.port = port;
    }

    public InterfaceType getType() {
        return type;
    }

    public void setType(InterfaceType type) {
        this.type = type;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
