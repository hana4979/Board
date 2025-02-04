package com.himedia.sp_server.controller;

import com.himedia.sp_server.entity.Board;
import com.himedia.sp_server.entity.Reply;
import com.himedia.sp_server.service.BoardService;
import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

@RestController
@RequestMapping("/board")
public class BoardController {

    @Autowired
    BoardService bs;

    /* 첫 페이지 테스트 */
    @GetMapping("/")
    public @ResponseBody String index(){
        return "<h1>Hello Security World</h1>";
    }

    @GetMapping("/getBoardList/{page}")
    public HashMap<String, Object> getBoardList(@PathVariable("page") int page) {
        //HashMap<String, Object> result = new HashMap<>();
        HashMap<String, Object> result = bs.getBoardList(page);
        //result.put("boardList", bs.getBoardList());
        return result;
    }

    @Autowired
    ServletContext context;

    @PostMapping("/fileupload")
    public HashMap<String, Object> fileload(@RequestParam("image") MultipartFile file) {
        HashMap<String, Object> result = new HashMap<>();
        String path = context.getRealPath("/images");
        // 파일 읽어오는 건 static 폴더, 저장되는 건 webapp - images 폴더

        Calendar today = Calendar.getInstance();
        long dt = today.getTimeInMillis();
        String filename = file.getOriginalFilename();
        String fn1 = filename.substring(0, filename.indexOf(".")); // . 왼쪽 파일이름
        String fn2 = filename.substring(filename.indexOf(".")); // . 오른쪽 확장자
        String uploadPath = path + "/" + fn1 + dt + fn2;

        try{
            file.transferTo(new File(uploadPath)); // 파일 저장
            result.put("image", filename);
            result.put("savefilename", fn1 + dt + fn2);
        }catch(IllegalStateException | IOException e){
            e.printStackTrace();
        }
        return result;
    }

    @PostMapping("/insertBoard")
    public HashMap<String, Object> insertBoard(@RequestBody Board board) {
        HashMap<String, Object> result = new HashMap<>();
        bs.insertBoard(board);
        result.put("msg", "ok");
        return result;
    }

    /*@PostMapping("/addReadCount")
    public HashMap<String, Object> addReadCount(@RequestBody Board board) {
        HashMap<String, Object> result = new HashMap<>();
        bs.addReadCount(board.getNum());
        result.put("msg", "ok");
        return result;
    }*/

    @PostMapping("/addReadCount")
    public HashMap<String, Object> addReadCount(@RequestParam("num") int num) {
        HashMap<String, Object> result = new HashMap<>();
        bs.addReadCount(num);
        result.put("msg", "ok");
        return result;
    }

    @GetMapping("/getBoard/{boardnum}")
    public HashMap<String, Object> getBoard(@PathVariable("boardnum") int num) {
        System.out.println("boardnum : " + num);
        HashMap<String, Object> result = new HashMap<>();
        result.put("board", bs.getBoard(num));
        return result;
    }

    @GetMapping("/getReply/{boardnum}")
    public HashMap<String, Object> getReply(@PathVariable("boardnum") int boardnum) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("replyList", bs.getReplyList(boardnum));
        return result;
    }

    @PostMapping("/addReply")
    public HashMap<String, Object> addReply(@RequestBody Reply reply) {
        HashMap<String, Object> result = new HashMap<>();
        bs.addReply(reply);
        result.put("msg", "ok");
        return result;
    }

    @DeleteMapping("/deleteReply/{replynum}")
    public HashMap<String, Object> deleteReply(@PathVariable("replynum") int replynum) {
        HashMap<String, Object> result = new HashMap<>();
        bs.deleteReply(replynum);
        result.put("msg", "ok");
        return result;
    }

    @PostMapping("/updateBoard")
    public HashMap<String, Object> updateBoard(@RequestBody Board board) {
        HashMap<String, Object> result = new HashMap<>();
        Board beforeBoard = bs.getBoard(board.getNum());
        if(!beforeBoard.getPass().equals(board.getPass())){
            result.put("msg", "not-ok");
        }else{
            bs.updateBoard(board);
            result.put("msg", "ok");
        }
        return result;
    }

    @PostMapping("/confirmPass")
    public HashMap<String, Object> confirmPass(@RequestBody Board board) {
        HashMap<String, Object> result = new HashMap<>();
        Board beforeBoard = bs.getBoard(board.getNum());
        if(!beforeBoard.getPass().equals(board.getPass())){
            result.put("msg", "not-ok");
        }else{
            result.put("msg", "ok");
        }
        return result;
    }

    @DeleteMapping("/deleteBoard/{boardnum}")
    public HashMap<String, Object> deleteBoard(@PathVariable("boardnum") int boardnum) {
        HashMap<String, Object> result = new HashMap<>();
        bs.deleteBoard(boardnum);
        result.put("msg", "ok");
        return result;
    }
}
