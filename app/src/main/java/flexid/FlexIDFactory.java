package flexid;

public class FlexIDFactory implements FlexIDFactoryInterface {
    private DeviceID dev;

    public FlexIDFactory(DeviceID dev) {
        this.dev = dev;
    }

    @Override
    public FlexID getMyFlexID(FlexID peer) {
        return null;
    }

    @Override
    public FlexID setPeerFlexID(Locator loc, AttrValuePairs avps) {
        return null;
    }
}
