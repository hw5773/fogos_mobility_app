package project.versatile.flexid;

/**
 * This enum class defines interfaces
 */
public enum InterfaceType {
    WIFI("wifi") {
        public void printInterface() {
            System.out.println("This interface is wifi");
        }
    },
    ETH("eth") {
        public void printInterface() {
            System.out.println("Ethernet is used");
        }
    },
    LTE("lte") {
        public void printInterface() {
            System.out.println("This interface is LTE");
        }
    },
    BT("bluetooth") {
        public void printInterface() {
            System.out.println("This is bluetooth");
        }
    },
    BLE("ble") {
        public void printInterface() {
            System.out.println("This is bluetooth low energy");
        }
    };

    private final String inf;

    InterfaceType(String inf) { this.inf = inf; }

    @Override public String toString() { return inf; }
    public abstract void printInterface();
}
