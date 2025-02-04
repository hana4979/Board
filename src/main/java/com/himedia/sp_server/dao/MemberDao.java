package com.himedia.sp_server.dao;

import com.himedia.sp_server.entity.Member;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class MemberDao {

    @Autowired
    private EntityManager em;

    public Member getMember(String userid){
        // em.find() 기본키 필드를 대상으로 검색할 때 사용하는 메소드
        return em.find(Member.class, userid);
    }

    public void insertMember(Member member) {
        String pwd = member.getPwd();
        BCryptPasswordEncoder pe = new BCryptPasswordEncoder();
        member.setPwd(pe.encode(pwd));
        em.persist(member); // Entity 객체이기 때문에 가능
        // Entity : jpa 에서 레코드로 쓸 수 있는가 없는가. / 기존 dto : 그냥 자료형.
    }

    public void updateMember(Member member) {
        // insert 가 persist 메소드로 실행된다면,
        // update 는 수정할 레코드를 검색해서 또 다른 Entity 객체에 저장 후 각 멤버변수를 수정해 넣는 방식

        // 전달된 객체의 userid 로 레코드를 검색
        Member updateMember = em.find(Member.class, member.getUserid());
        // 검색되어 저장된 Entity 객체의 멤버변수들을 수정
        BCryptPasswordEncoder pe = new BCryptPasswordEncoder();
        updateMember.setPwd(pe.encode(member.getPwd()));
        updateMember.setName(member.getName());
        updateMember.setEmail(member.getEmail());
        updateMember.setPhone(member.getPhone());
    }

    public void deleteMember(String userid) {
        // 검색해서 Entity 객체에 넣고 remove 메소드로 삭제
        Member delMember = em.find(Member.class, userid);
        em.remove(delMember);
    }
}
