package com.himedia.sp_server.security.handler;

import com.google.gson.Gson;
import com.himedia.sp_server.dto.MemberDTO;
import com.himedia.sp_server.security.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class APILoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        // 로그인에 성공을 했으니 로그인 사용자 정보와 토큰을 같이 묶어서 전송해주는 역할
        // 매개변수 authentication 객체로 로그인이 성공한 정보들이 담겨서 전송됨
        // 그 객체에서 MemberDTO 를 꺼내고
        MemberDTO memberDTO = (MemberDTO) authentication.getPrincipal();
        // memberDTO 에서 getClaims 로 사용자 정보를 Map 형식으로 추출
        Map<String, Object> claims = memberDTO.getClaims();

        // claims 를 이용해서 액세스 토큰과 리프레시 토큰을 발급하고
        String accessToken = JWTUtil.generateToken(claims, 1); // 1분
        String refreshToken = JWTUtil.generateToken(claims, 60*24);

        // 그 둘을 합해서 JSON 형식으로 클라이언트로 전송
        claims.put("accessToken", accessToken); // claims 안에는 이미 사용자 정보가 존재, 거기에 더해서 토큰으로 암호화된 사용자 정보가 또 입력되는 셈
        claims.put("refreshToken", refreshToken);

        Gson gson = new Gson();
        String jsonStr = gson.toJson(claims);
        System.out.println("jsonStr : " + jsonStr);
        response.setContentType("application/json");

        response.setCharacterEncoding("UTF-8");
        // response 에서 Writer 를 얻어서 print 함 --> 클라이언트에게 출력 형식으로 전송
        PrintWriter printWriter = response.getWriter();
        printWriter.println(jsonStr);
        printWriter.close();
    }
}
