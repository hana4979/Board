package com.himedia.sp_server.service;

import com.himedia.sp_server.dao.BoardDao;
import com.himedia.sp_server.dto.Paging;
import com.himedia.sp_server.entity.Board;
import com.himedia.sp_server.entity.Reply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class BoardService {

    @Autowired
    BoardDao bdao;

    public HashMap<String, Object> getBoardList(int page){
        HashMap<String, Object> result = new HashMap<>();
        Paging paging = new Paging();
        paging.setPage(page);

        int count = bdao.getAllCount();
        paging.setTotalCount(count);
        paging.calPaing();

        result.put("boardList", bdao.getBoardList(paging));
        result.put("paging", paging);
        return result;
    }

    public void insertBoard(Board board) {
        bdao.insertBoard(board);
    }

    public void addReadCount(int num) {
        bdao.addReadCount(num);
    }

    public Board getBoard(int num) {
        return bdao.getBoard(num);
    }

    public List<Reply> getReplyList(int boardnum) {
        return bdao.getReplyList(boardnum);
    }

    public void addReply(Reply reply) {
        bdao.addReply(reply);
    }

    public void deleteReply(int replynum) {
        bdao.deleteReply(replynum);
    }

    public void updateBoard(Board board) {
        bdao.updateBoard(board);
    }

    public void deleteBoard(int boardnum) {
        bdao.deleteBoard(boardnum);
    }
}
