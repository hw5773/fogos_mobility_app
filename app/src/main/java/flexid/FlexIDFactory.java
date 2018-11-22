package flexid;

public class FlexIDFactory implements FlexIDFactoryInterface {
    private DeviceID dev;

    public FlexIDFactory() { this.dev = null; }
    public FlexIDFactory(DeviceID dev) {
        this.dev = dev;
    }

    @Override
    public FlexID getMyFlexID(FlexID peer) {
        String identity = "0x5555";
        AttrValuePairs avps = new AttrValuePairs();
        Locator loc = new Locator(InterfaceType.WIFI, "10.7.1.3", 3332);
        FlexID id = new FlexID(identity.getBytes(), FlexIDType.SERVICE, avps, loc);

        return id;
    }

    @Override
    public FlexID setPeerFlexID(Locator loc, AttrValuePairs avps) {
        return null;
    }
}
