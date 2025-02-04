package com.himedia.sp_server.dao;

import com.himedia.sp_server.dto.Paging;
import com.himedia.sp_server.entity.Board;
import com.himedia.sp_server.entity.Reply;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BoardDao {

    @Autowired
    private EntityManager em;

    public List<Board> getBoardList(Paging paging) {
        String sql = "SELECT b FROM Board b ORDER BY b.id DESC";
        List<Board> list
                = em.createQuery(sql, Board.class)
                .setFirstResult(paging.getStartNum() - 1)
                .setMaxResults(paging.getDisplayRow())
                .getResultList();
        /*
        // from (Entity클래스이름) (별칭)
        // b.userid, b.title 로 특정 값 조회 가능
        List<Board> list = em.createQuery(sql).getResultList();
        // setFirstResult(10) : (조건)10번째 필드부터 가져오겠다 -> 페이징처리
        */
        return list;
    }

    public void insertBoard(Board board) {
        em.persist(board);
    }

    public void addReadCount(int num) {
        Board updateBoard = em.find(Board.class, num);
        updateBoard.setReadcount(updateBoard.getReadcount() + 1);
    }

    public Board getBoard(int num) {
        Board board = em.find(Board.class, num);
        return board;
    }

    public List<Reply> getReplyList(int boardnum) {
        String sql = "SELECT r FROM Reply r WHERE r.boardnum=:bnum ORDER BY r.id DESC";
        // :bnum - 지어낸 변수 이름,
        List<Reply> list = em.createQuery(sql, Reply.class)
                .setParameter("bnum", boardnum) // boardnum 이 sql 문에서 bnum 이란 이름으로 들어가도록 설정
                .getResultList();
        return list;
    }

    public void addReply(Reply reply) {
        em.persist(reply);
    }

    public void deleteReply(int replynum) {
        Reply delReply = em.find(Reply.class, replynum);
        em.remove(delReply);
    }

    public void updateBoard(Board board) {
        Board updateBoard = em.find(Board.class, board.getNum());
        updateBoard.setTitle(board.getTitle());
        updateBoard.setContent(board.getContent());
        updateBoard.setImage(board.getImage());
        updateBoard.setSavefilename(board.getSavefilename());
    }

    public void deleteBoard(int boardnum) {
        Board delBoard = em.find(Board.class, boardnum);
        em.remove(delBoard);
    }

    public int getAllCount() {
        //String sql = "SELECT COUNT(*) FROM Board";
        String sql = "SELECT COUNT(b) FROM Board b";
        Long count = (Long)em.createQuery(sql).getSingleResult();
        // int 의 범위를 넘어가는 개수가 나올 수 있음. 애초에 JPA 의 COUNT 함수는 Long 을 반환함
        return (int)count.longValue(); // 언박싱 후 int로 변환
        // Long 객체 타입, int 기본 타입이라 직접적 캐스팅 불가능
    }
}
