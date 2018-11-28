package project.versatile;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

public class WifiReceiver extends BroadcastReceiver {

    final String TAG = "FogOSWifi";

    @Override
    public void onReceive(Context context, Intent intent) {
        long start, end;
        start = System.currentTimeMillis();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            Log.d(TAG, "Have Wifi Connection");
            MobilityActivity.setReady(true);
            Log.d(TAG, "Ready is set to true in WifiReceiver");
        } else {
            Log.d(TAG, "Don't have Wifi Connection");
            MobilityActivity.setReady(false);
            WifiManager wifiManager = (WifiManager) context.getSystemService(Activity.WIFI_SERVICE);
            wifiManager.setWifiEnabled(true);

            Log.d(TAG, "Wifi Enabled");

            boolean configured = false;
            String networkSSID = "mmlab2";
            String networkPass = "mmlab2017";

            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";
            conf.preSharedKey = "\"" + networkPass + "\"";

            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();

                    break;
                }
            }
        }
        end = System.currentTimeMillis();
        Log.d(TAG, "Elapsed Time: " + (end - start));
    }
}
