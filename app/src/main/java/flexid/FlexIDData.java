package flexid;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Hashtable;
import java.util.Iterator;

public class FlexIDData implements Parcelable {
    private FlexID id;

    public FlexIDData(FlexID id)
    {
        this.id = id;
    }

    public FlexIDData(Parcel src) {
        id = new FlexID();
        byte[] identity;
        byte[] priv;
        int len;

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
/*
        FlexIDType type = new FlexIDType(src.readInt());
        id.setType(type);
*/
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id.getIdentity().length);
        dest.writeByteArray(id.getIdentity());
        if (id.getPriv() != null) {
            dest.writeInt(id.getPriv().length);
            dest.writeByteArray(id.getPriv());
        }
        else {
            dest.writeInt(0);
        }
        dest.writeString(id.getType().toString());

        dest.writeInt(id.getAvps().getNumberOfAVPs());
        Hashtable<String, String> table = id.getAvps().getAvps();
        Iterator<String> itr = table.keySet().iterator();
        String key;
        while (itr.hasNext()) {
            key = itr.next();
            dest.writeString(key);
            dest.writeString(table.get(key));
        }

        Locator loc = id.getLocator();
        InterfaceType inf;

        if (loc != null) {
            inf = loc.getType();
            dest.writeInt(1);
            dest.writeString(inf.toString());

            if (inf == InterfaceType.WIFI || inf == InterfaceType.ETH || inf == InterfaceType.LTE) {
                dest.writeString(loc.getAddr());
                dest.writeInt(loc.getPort());
            }

            else if (inf == InterfaceType.BT || inf == InterfaceType.BLE) {
                dest.writeString(loc.getAddr());
            }
        }
    }

    public FlexID getId() {
        return id;
    }
}
