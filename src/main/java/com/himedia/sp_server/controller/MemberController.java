package com.himedia.sp_server.controller;

import com.himedia.sp_server.entity.Member;
import com.himedia.sp_server.security.util.CustomJWTException;
import com.himedia.sp_server.security.util.JWTUtil;
import com.himedia.sp_server.service.MemberService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    MemberService ms;
    
    // config.loginPage("/member/login"); 가 대신하게 됨ㅋ
    /*
    @PostMapping("/login")
    public HashMap<String, Object> login(@RequestBody Member member, HttpSession session) {
        System.out.println(member);
        HashMap<String, Object> result = new HashMap<>();

        Member mem = ms.getMember(member.getUserid());
        if(mem == null)
            result.put("msg", "아이디가 없습니다");
        else if(!mem.getPwd().equals(member.getPwd()))
            result.put("msg", "비밀번호가 일치하지 않습니다");
        else{
            session.setAttribute("loginUser", mem.getUserid());
            result.put("msg", "ok");
        }
        return result;
    }
    */

    @GetMapping("/getLoginUser")
    public HashMap<String, Object> getLoginUser(HttpSession session) {
        HashMap<String, Object> result = new HashMap<>();
        String userid = (String) session.getAttribute("loginUser");
        Member member = ms.getMember(userid);
        result.put("loginUser", member);
        return result;
    }

    @GetMapping("/logout")
    public HashMap<String, Object> logout(HttpSession session) {
        session.removeAttribute("loginUser");
        HashMap<String, Object> result = new HashMap<>();
        result.put("msg", "ok");
        return result;
    }

    @PostMapping("/idCheck")
    public HashMap<String, Object> idCheck(@RequestBody Member member) {
        HashMap<String, Object> result = new HashMap<>();
        Member mem = ms.getMember(member.getUserid());
        if(mem == null)
            result.put("msg", "ok");
        else
            result.put("msg", "not-ok");

        return result;
    }

    @PostMapping("/join")
    public HashMap<String, Object> join(@RequestBody Member member) {
        HashMap<String, Object> result = new HashMap<>();
        ms.insetMember(member);
        result.put("msg", "ok");
        return result;
    }

    @PostMapping("/updateMember")
    public HashMap<String, Object> updateMember(@RequestBody Member member) {
        HashMap<String, Object> result = new HashMap<>();
        ms.updateMember(member);
        result.put("msg", "ok");
        return result;
    }

    @DeleteMapping("/deleteMember/{userid}")
    public HashMap<String, Object> deleteMember(@PathVariable("userid") String userid) {
        HashMap<String, Object> result = new HashMap<>();
        //String userid = (String) session.getAttribute("loginUser");
        ms.deleteMember(userid);
        //session.removeAttribute("loginUser");
        result.put("msg", "ok");
        return result;
    }

    @GetMapping("/refresh/{refreshToken}")
    public HashMap<String, Object> refresh(
            @PathVariable("refreshToken") String refreshToken,
            @RequestHeader("Authorization") String authHeader
    ) throws CustomJWTException {
        HashMap<String, Object> result = new HashMap<>();

        // 리프레시 토큰이 없다면
        if(refreshToken == null) throw new CustomJWTException("NULL_REFRESH");
        // Authorization 을 담은 헤더가 없다면
        if(authHeader == null || authHeader.length() < 7)
            throw new CustomJWTException("INVALID_HEADER");

        // 추출한 내용의 7번째 글자부터 끝까지 추출
        String accessToken = authHeader.substring(7);

        // 유효시간이 지났는지 검사
        Boolean expAt = checkExpiredToken(accessToken);
        // 이 단계에서 accessToken 이 만료가 됐다면, 유저 정보에 접근을 못함
        // --> refreshToekn(?) 에서 claim 을 꺼내 accessToken 과 refreshToken 을 재발급함

        if(expAt){
            System.out.println("토큰 유효기간 아직 안 지났습니다. 계속 사용합니다");
            result.put("accessToken", accessToken);
            result.put("refreshToken", refreshToken);
        }else{
            // accessToken 기간 만료시 refresh 토큰으로 재검증하여 사용자 정보 추출
            Map<String, Object> claims = JWTUtil.validateToken(refreshToken);
            // refresh 토큰마저 하루가 지나서 만료되면.. 아래 유효시간 검사 로직 또 쓰면 됨. 로그인페이지로 가게한다던지의 처리를 해주면 될 것.

            // 토큰 교체
            String newAccessToken = JWTUtil.generateToken(claims, 1);
            System.out.println("access 토큰이 갱신되었습니다");

            // refresh 토큰이 만료가 되지 않은 상태라면? 굳이 바꿀 필요 없음. 시간 확인해보기
            // 리프레시 토큰의 exp 를 꺼내서 현재 시간과 비교
            Boolean expRt = checkTime((Integer)claims.get("exp"));
            /*
                QQQ. "exp" 라는 게 대체 뭐임?
                AAA. 토큰을 새로 만드는 generateToken(claims, (분값)); 명령에서 시간에 해당하는 부분임.
                이 부분은 generateToken 명령을 JWTUtil.java 에서 가져오는 만큼 JWTUtil.js 를 보면 나옴.
                토큰 제작 부분을 보면
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(i).toInstant()))
                이런 명령이 들어가있는 걸 볼 수 있는데, 해당 명령은 현재 시간에 전달된 시간(분)을 더하는 것임.
                그리고 이곳에 암묵적으로 exp 가 정의된다..고함.
            */
            String newRefreshToken = "";

            // 기존 리프레시 토큰의 유효기간이 한 시간도 안 남았다면 교체
            if(expRt) newRefreshToken = JWTUtil.generateToken(claims, 60*24);
            else newRefreshToken = refreshToken;
            
            result.put("accessToken", newAccessToken);
            result.put("refreshToken", newRefreshToken);
        }
        return result;
    }

    /* refreshToken 유효시간 검사 */
    private Boolean checkTime(Integer exp) {
        java.util.Date expDate = new java.util.Date((long)exp * (1000)); // 밀리초로 변환
        long gap = expDate.getTime() - System.currentTimeMillis(); // 현재 시간과의 차이 계산
        long leftMin = gap / (1000*60); // 분단위 변환
        // 1시간도 안 남았는지...
        return leftMin < 60;
    }

    /* accessToken 유효시간 검사 */
    private Boolean checkExpiredToken(String accessToken) {
        try{
            JWTUtil.validateToken(accessToken);
        }catch(CustomJWTException e){
            //throw new RuntimeException(e);
            /* catch(ExpiredJwtException expiredJwtException){
                throw new CustomJWTException("Expired"); */
            if(e.getMessage().equals("Expired")){
                return false;
            }
        }
        return true;
    }

}
