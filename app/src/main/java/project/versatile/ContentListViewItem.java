package project.versatile;

public class ContentListViewItem {

    private String name;
    private String path;
    private Boolean shared;

    public void setName(String text) {
        this.name = text;
    }

    public String getName() {
        return this.name;
    }

    public void setPath(String text) {
        this.path = text;
    }

    public String getPath() {
        return this.path;
    }

    public void setShared(Boolean bool) {
        this.shared = bool;
    }

    public Boolean getShared() {
        return this.shared;
    }

}
