package controllers;

import Model.User;

public class Session {
	
	private static final int expireSeconds = 600;
	
	/**
	 * 새로운 새션을 생성한다.
	 * @param u 유저 정보가 담긴 객체
	 * @param remoteAddr 유저가 접속한 아이피 주소
	 */
	public static void makeNewSession(User u, String remoteAddr)
	{
		// 이곳에 세션을 생성하고 session 테이블에 저장하는 코드를 작성
		
		// User 객체에 위에서 만든 세션 키 삽입
		u.setSession("session key");
	}
	
	/**
	 * 세션이 유요한지 검사한다
	 * @param session 검사할 세션 값
	 * @param reniteAddr 접속한 IP
	 * @return 세션 유효 여부
	 */
	public static boolean isValideSession(String session, String remoteAddr)
	{
		// 
		// 해당 세션이 올바른 값인지 확인
		// 1. ezsession 테이블에 해당 session 값이 있는지?
		// 2. 디비에 저장된 ipaddress 와 remoteAddr 이 같은지?
		// 3. 현재 시간이 last_update 로 부터 600초 이내에 존재하는지?
		// 4. 이미 expired 된 것인지?
		return false;
	}
	
	/**
	 * 세션을 파기한다
	 * @param session 파기할 세션 값
	 */
	public static void expireSession(String session)
	{
		// 세션을 파기하는 내용을 구현한다.
		// 세션 파기는 expired 를 1 로 설정한다.
	}
	
	/**
	 * 세션의 last_update 값을 갱신한다.
	 * @param session last_update값을 갱신할 세션 값
	 */
	public static void updateLastUpdateTime(String session)
	{
		// session 의 last_update 값을 갱신하는 코드 작성
	}
}
