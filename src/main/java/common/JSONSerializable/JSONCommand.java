package common.JSONSerializable;

import com.alibaba.fastjson.annotation.JSONField;

public class JSONCommand {

    @JSONField
    private int id;

    @JSONField
    private String type;

    @JSONField
    private String[] params;

    @JSONField
    private final String obj_class = "JSONCommand";


    public JSONCommand(int id, String type, String... params) {
        super();
        this.setId(id);
        this.setType(type);
        this.setParams(params);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("command_id = " + getId() + "\n");
        stringBuilder.append("type = " + getType() + "\n");
        for(int i = 0; i < getParams().length; i++) {
            stringBuilder.append("param_" + (i + 1) + " = " + getParams()[i] + "\n");
        }
        return stringBuilder.toString();
    }

    // getters\setters need!

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }


    public String getObj_class() {
        return obj_class;
    }
}
