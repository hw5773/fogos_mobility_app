package project.versatile.flexid;

import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class FlexIDFactory implements FlexIDFactoryInterface {
    private DeviceID dev;
    private final String TAG = "FogOSFlexIDFactory";

    public FlexIDFactory() { this.dev = null; }
    public FlexIDFactory(DeviceID dev) {
        this.dev = dev;
    }

    @Override
    public FlexID getMyFlexID(FlexID peer) {
        String identity = "0x5555";
        String addr = getLocalIpAddress();
        AttrValuePairs avps = new AttrValuePairs();
        Locator loc = new Locator(InterfaceType.WIFI, addr, 3332);
        FlexID id = new FlexID(identity.getBytes(), FlexIDType.SERVICE, avps, loc);

        return id;
    }

    public String getLocalIpAddress() {
        /*
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.toString());
        }
        return null;
        */
        return "192.168.0.29";
    }

    @Override
    public FlexID setPeerFlexID(Locator loc, AttrValuePairs avps) {
        return null;
    }
}
