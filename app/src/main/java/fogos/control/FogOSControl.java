package fogos.control;

import org.json.JSONArray;
import project.versatile.flexid.FlexID;
import project.versatile.flexid.InterfaceType;
import project.versatile.flexid.Locator;

public class FogOSControl {
    public FogOSControl() {
    }

    public JSONArray queryMessage(String str, int index) {
        JSONArray jarray = null;
        index = index % 2;
        String jstr;

        if (index == 0) {
            jstr = "[{'id': '0x1234', 'title': '환호하는 손흥민', 'desc': '손흥민이 한독전에서 골을 넣고 환호하고 있다.'}," +
                    "{'id': '0x4567', 'title': '기뻐하는 김영권 영상', 'desc': '온국민을 환호하게 만든 김영권의 첫골을 다시 감상해 봅시다.'}," +
                    "{'id': '0x7890', 'title': '좌절에 빠진 독일 팬들', 'desc': '예기치 못한 패배에 독일 팬들은 모두 울상에 빠져 있습니다.'}," +
                    "{'id': '0x2468', 'title': '축구 정보 사이트', 'desc': ''}]";
        } else {
            jstr = "[{'id': '0x1111', 'title': '전송 보안 계층 (TLS)', 'desc': '전송 보안 계층이란 종단간 암호화를 보장하는 프로토콜이다.'}," +
                    "{'id': '0x2222', 'title': 'Network Security and Cryptography', 'desc': '이 책은 네트워크 보안에 입문하고 싶은 초급자가 읽기에 매우 좋다.'}," +
                    "{'id': '0x3333', 'title': '공개키 기반 구조 (PKI)', 'desc': '공개키 기반 구조는 신뢰하는 제 3자를 통해 상대를 인증하기 위한 기반 구조이다.'}," +
                    "{'id': '0x4444', 'title': '보안 동호회', 'desc': ''}]";
        }
        try {
            jarray = new JSONArray(jstr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jarray;
    }

    public FlexID requestConnection(FlexID id) {
        // TODO: Get the address from FogOS Module

        Locator locator = new Locator(InterfaceType.WIFI, "192.168.0.128", 3333);
        id.setLocator(locator);
        return id;
    }
}