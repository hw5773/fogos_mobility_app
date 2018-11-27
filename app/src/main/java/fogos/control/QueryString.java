package fogos.control;

import org.json.JSONArray;
import versatile.flexid.CategoryType;
import versatile.flexid.FlexIDType;

public class QueryString {
    private FlexIDType type;
    private CategoryType ctype;
    private JSONArray jarray;

    public QueryString(FlexIDType type, CategoryType ctype, String str) {
        this.type = type;
        this.ctype = ctype;
        try {
            this.jarray = new JSONArray(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FlexIDType getType() {
        return type;
    }

    public void setType(FlexIDType type) {
        this.type = type;
    }

    public CategoryType getCtype() {
        return ctype;
    }

    public void setCtype(CategoryType ctype) {
        this.ctype = ctype;
    }

    public JSONArray getJarray() {
        return jarray;
    }

    public void setJarray(String str) {
        try {
            this.jarray = new JSONArray(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
