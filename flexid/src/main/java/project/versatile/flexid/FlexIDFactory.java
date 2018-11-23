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
        String ip = "";

        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();

                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            Log.getStackTraceString(e);
        }

        return ip;
    }

    @Override
    public FlexID setPeerFlexID(Locator loc, AttrValuePairs avps) {
        return null;
    }
}
