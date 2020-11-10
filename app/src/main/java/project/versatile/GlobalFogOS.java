package project.versatile;

import android.app.Application;
import FlexID.*;
import FogOSClient.FogOSClient;

public class GlobalFogOS extends Application {
    private FogOSClient fogos=null;


    @Override
    public void onCreate() {
        fogos=null;
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public FogOSClient getFogOSClient()
    {
        System.out.println("############ getFogOSClient ############");
        return this.fogos;
    }


    public void setFogOSClient(FogOSClient input)
    {
        System.out.println("############ setFogOSClient ############");
        this.fogos = input;
    }
}
