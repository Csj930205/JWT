## JWT(JSON WEB TOKEN) 이란

1. JWT는 일반적으로 클라이언트와 서버 통신 시 인증 / 인가를 위해 사용하는 토큰.
2. Cookie &Session의 자원 문제를 해결하기 위한 방법으로 JWT는 토큰 자체에 유저 정보를 담아서 암호화한 토큰이라고 생각하면 된다. **단, 암호화 된 내용은 디코딩 과정을 통해서 해석이 가능하다.**
3. JWT 와 Cookie &Session 의 가장 큰 차이점으로는 서버는 클라이언트의 상태를 완전하게 저장하지 않는 무상태성을 유지할 수 있다.

---

## 동작

1. 사용자는 ID/PW 를 통해 로그인을 요청
2. 서버에서는 계정 정보를 읽어들여 사용자를 확인 후, 사용자의 고유 ID 값을 부여한 후 기타 정보와 함께 payload 에 집어 넣는다.
3. JWT 의 토큰 유효기간을 설정
4. 암호화할 Secret Key 를 이용하여 Access Token 및 Refresh Token 을 발급한다.
5. 사용자는 Access Token 을 받아 저장 후, 인증이 필요한 요청마다 토큰을 헤더에 실어 보낸다.
6. 서버는 해당 토큰을 Secret Key 로 복호화 한 후, 조작 여부, 유효기간을 확인
7. 검증이 완료되었을 경우 payload를 디코딩하여 사용자가 요청한 데이터를 전달한다.

---

## 주요 사항

1. Refresh Token 은 새로운 Access Token 을 발급하기위한 토큰이다.
2.  기본적으로 Access Token 의 경우 외부 유출 문제로 인해 유효기간을 짧게 설정한다.
3. 유효기간이 끝난 Access Token에 대해 Refresh Token을 사용하여 새로운 Access Token을 발급 받을 수 있다.
4. 기본적으론 Refresh Token의 유효 기간은 Access Token의 유효기간보다 길게 설정해야 한다고 생각 할 수 있지만, 만약 Refresh Token이 유출되게 된다면 Access Token 의 충돌이 발생하므로 서버측에서는 두 토큰을 모두 폐기시켜야한다.
5. 국제 인터넷 표준 기구(IETF)에서는 이를 방지하기 위해 Refresh Token 도 Access Token 과 같은 유효기간을 가지도록 하여, 사용자가 한번 Refresh Token으로 Access Token 을 발급 받았다면, Refresh Token 역시 다시 발급받도록 하는 것을 권장한다.
