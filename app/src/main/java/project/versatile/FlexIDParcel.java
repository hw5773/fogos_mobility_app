package project.versatile;

import FlexID.FlexID;
import FlexID.AttrValuePairs;
import FlexID.InterfaceType;
import FlexID.Locator;
import FlexID.FlexIDType;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Hashtable;
import java.util.Iterator;

import FlexID.Value;

public class FlexIDParcel implements Parcelable {
    private static final String TAG="FogOSData";
    private FlexID id;

    public FlexIDParcel(FlexID id)
    {
        this.id = id;
    }

    public FlexIDParcel(Parcel src) {
        id = new FlexID();
        String sidentity;
        byte[] identity;
        byte[] priv;
        int len, numOfAVPs, io;
        AttrValuePairs avps = new AttrValuePairs();
        Hashtable<String, Value> table = avps.getTable();
        InterfaceType inf;
        Locator loc;

        len = src.readInt();
        if (len > 0) {
            identity = new byte[len];
            src.readByteArray(identity);
            id.setIdentity(identity);
        }

        len = src.readInt();
        if (len > 0) {
            priv = new byte[len];
            src.readByteArray(priv);
            id.setPriv(priv);
        }

        FlexIDType type = FlexIDType.valueOf(src.readString());
        id.setType(type);

        numOfAVPs = src.readInt();

        for (int i=0; i<numOfAVPs; i++) {
            Value tmpVal = new Value("test", "test");
            table.put(src.readString(), tmpVal);
        }
        
        io = src.readInt();
        
        if (io > 0) {
            inf = InterfaceType.valueOf(src.readString());
            if (inf == InterfaceType.WIFI || inf == InterfaceType.LTE || inf == InterfaceType.ETH) {
                Log.d(TAG, "Interface is wifi, lte, or eth");
                loc = new Locator(inf, src.readString(), src.readInt());
                id.setLocator(loc);
            } else {
                Log.d(TAG, "Cannot find the interface: " + inf.name());
            }
        }
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public FlexIDParcel createFromParcel(Parcel in) {
            return new FlexIDParcel(in);
        }

        public FlexIDParcel[] newArray(int size) {
            return new FlexIDParcel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.d(TAG, "Length of the identity: " + id.getIdentity().length);
        dest.writeInt(id.getIdentity().length);
        Log.d(TAG, "Identity: " + new String(id.getIdentity()));
        dest.writeByteArray(id.getIdentity());
        if (id.getPriv() != null) {
            dest.writeInt(id.getPriv().length);
            dest.writeByteArray(id.getPriv());
        }
        else {
            dest.writeInt(0);
        }

        Log.d(TAG, "Flex ID Type: " + id.getType());
        dest.writeString(id.getType().name());

        Log.d(TAG, "Number of AVPs: " + id.getAvps().getNumberOfAVPs());
        dest.writeInt(id.getAvps().getNumberOfAVPs());

        Hashtable<String, Value> table = id.getAvps().getTable();
        Iterator<String> itr = table.keySet().iterator();
        String key;
        while (itr.hasNext()) {
            key = itr.next();
            dest.writeString(key);
            dest.writeString(String.valueOf(table.get(key)));
        }

        Log.d(TAG, "AVPs are passed");

        Locator loc = id.getLocator();
        InterfaceType inf;

        if (loc != null) {
            Log.d(TAG, "Locator Type: " + loc.getType().toString());
            Log.d(TAG, "Locator Addr: " + loc.getAddr());
            Log.d(TAG, "Locator Port: " + loc.getPort());

            inf = loc.getType();
            dest.writeInt(1);
            dest.writeString(inf.name());

            if (inf == InterfaceType.WIFI || inf == InterfaceType.ETH || inf == InterfaceType.LTE) {
                dest.writeString(loc.getAddr());
                dest.writeInt(loc.getPort());
            }

            else if (inf == InterfaceType.BT || inf == InterfaceType.BLE) {
                dest.writeString(loc.getAddr());
            }
        } else {
            dest.writeInt(0);
        }

        Log.d(TAG, "Locator finished");
    }

    public FlexID getId() {
        return id;
    }
}
