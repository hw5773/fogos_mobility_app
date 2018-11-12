package flexid;

import java.util.Hashtable;

public class AttrValuePairs {
    int numberOfAVPs;
    Hashtable<String, String> avps;

    public AttrValuePairs() {
        numberOfAVPs = 0;
        avps = new Hashtable<String, String>();
    }

    void addAttrValuePair(String attr, String value) {
        avps.put(attr, value);
    }

    String getValueByAttr(String attr) {
        return avps.get(attr);
    }
}
