package com.himedia.sp_server.security.filter;

import com.google.gson.Gson;
import com.himedia.sp_server.dto.MemberDTO;
import com.himedia.sp_server.security.util.CustomJWTException;
import com.himedia.sp_server.security.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
public class JWTCheckFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 점검 후
        String authHeaderStr = request.getHeader("Authorization"); // header 중 Authorization
        // { headers: {'Authorization' : `Bearer ${loginUser.accessToken}`} } -> accessToken 은 7자부터\
        System.out.println("authHeaderStr : " + authHeaderStr);
        String accessToken = authHeaderStr.substring(7);
        try{
            Map<String, Object> claims = JWTUtil.validateToken(accessToken);

            String userid = (String) claims.get("userid");
            String pwd = (String) claims.get("pwd");
            String name = (String) claims.get("name");
            String email = (String) claims.get("email");
            String phone = (String) claims.get("phone");
            String sns_id = (String) claims.get("sns_id");
            String provider = (String) claims.get("provider");
            String sns_user = (String) claims.get("sns_user");
            //Timestamp indate = (Timestamp) claims.get("indate"); // Long 형을 Timestamp 로 못 바꾼다 어쩌고 에러남
            Timestamp indate = null; // default now() 설정되어 있음

            List<String> list = new ArrayList<>();
            list.add("USER");

            MemberDTO memberdto = new MemberDTO(userid, pwd, name, email, phone, indate, provider, sns_id, sns_user, list);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(memberdto, pwd, memberdto.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);

        }catch (CustomJWTException e){
            log.error("JWT Check Error.........");
            log.error(e.getMessage());
            Gson gson = new Gson();
            String msg = gson.toJson(Map.of("error", "ERROR_ACCESS_TOKEN"));
            response.setContentType("application/json");
            PrintWriter printWriter = response.getWriter();
            printWriter.println(msg);
            printWriter.close();
        }
    }

    /* 토큰없이 요청을 수락해주어야 하는 요청들의 설정 */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        log.info("check uri.................." + path);

        // 요청 url 이 /member/login 로 시작되면 토큰 필터링 없이 요청을 수락함
        if(path.startsWith("/member/login"))
            return true;

        if(path.startsWith("/images/"))
            return true;

        if(path.startsWith("/member/idCheck"))
            return true;

        if(path.startsWith("/member/join"))
            return true;

        if(path.startsWith("/member/logout"))
            return true;

        if(path.startsWith("/member/refresh"))
            return true;

        return false;
    }

}
