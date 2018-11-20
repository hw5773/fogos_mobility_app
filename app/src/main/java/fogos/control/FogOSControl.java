package fogos.control;

import org.json.JSONArray;

public class FogOSControl {
    public FogOSControl() {
    }

    public JSONArray queryMessage(String str) {
        JSONArray jarray = null;
        String jstr =
                "[{'id': '0x1234', 'title': '환호하는 손흥민', 'desc': '손흥민이 한독전에서 골을 넣고 환호하고 있다.'},"+
                        "{'id': '0x4567', 'title': '기뻐하는 김영권 영상', 'desc': '온국민을 환호하게 만든 김영권의 첫골을 다시 감상해 봅시다.'}," +
                        "{'id': '0x7890', 'title': '좌절에 빠진 독일 팬들', 'desc': '예기치 못한 패배에 독일 팬들은 모두 울상에 빠져 있습니다.'}," +
                        "{'id': '0x2468', 'title': '축구 정보 사이트', 'desc': ''}]";
        try {
            jarray = new JSONArray(jstr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jarray;
    }
}