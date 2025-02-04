package com.himedia.sp_server.security.service;

import com.himedia.sp_server.Repository.MemberRepository;
import com.himedia.sp_server.dto.MemberDTO;
import com.himedia.sp_server.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository mr;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // loadUserByUsername 역할은 전에 사용하던 getMember 메소드의 역할
        log.info("--------------- loadUserByUsername ---------------" + username);

        // 전송된 아이디로 멤버 조회
        Member member = mr.findByUserid(username);
        // 없으면 Not Found 처리
        if(member == null) {
            throw new UsernameNotFoundException(username + " - User Not Found"); // security 속 이름
        }

        List<String> list = new ArrayList<>();
        list.add("USER");

        // 검색결과가 있으면 Entity 데이터를 DTO 데이터로 옮김
        MemberDTO memberdto = new MemberDTO(
                member.getUserid(),
                member.getPwd(),
                member.getName(),
                member.getEmail(),
                member.getPhone(),
                member.getIndate(),
                member.getProvider(),
                member.getSns_id(),
                member.getSns_user(),
                list
        );

        System.out.println(memberdto);
        System.out.println(member);
        return memberdto;
    }
}
