package project.versatile;
import project.versatile.flexid.FlexID;

public class ListItem {
    private String title;
    private String desc;
    private FlexID flexID;

    public ListItem(String title, String desc, String id) {
        this.title = title;
        this.desc = desc;
        this.flexID = new FlexID(id);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public FlexID getFlexID() { return flexID; }

    public void setFlexID(FlexID flexID) {
        this.flexID = flexID;
    }
}
