package com.himedia.sp_server.service;

import com.himedia.sp_server.dao.MemberDao;
import com.himedia.sp_server.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional // 서비스 계층에서 발생하 ㄹ수 있는 데이터베이스 작업(트랜잭션) 오류를 안전하게 처리하기 위함
public class MemberService {

    @Autowired
    MemberDao mdao;

    public Member getMember(String userid){
        return mdao.getMember(userid);
    }

    public void insetMember(Member member) {
        mdao.insertMember(member);
    }

    public void updateMember(Member member) {
        mdao.updateMember(member);
    }

    public void deleteMember(String userid) {
        mdao.deleteMember(userid);
    }
}
