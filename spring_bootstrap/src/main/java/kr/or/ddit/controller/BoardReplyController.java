package kr.or.ddit.controller;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.or.ddit.command.PageMaker;
import kr.or.ddit.command.ReplyRegistCommand;
import kr.or.ddit.command.SearchCriteria;
import kr.or.ddit.dto.ReplyVO;
import kr.or.ddit.service.ReplyService;

// url : /replies
// bno번 게시글의 페이징 처리된 댓글 목록
// /replies/{bno}/{page}     	 page list : GET방식
// /replies                  	 regist : POST방식 - 댓글입력
// /replies/{rno}            	 modify : PUT,PATCH방식, rno 댓글의 수정
// /replies/{bno}/{rno}/{page}    remove : DELETE 방식, rno 댓글 삭제
// /replies

@Controller
@RequestMapping("/replies")
public class BoardReplyController {

	@Autowired
	private ReplyService service;
	
	@RequestMapping(value="/{bno}/{page}", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> replyList(@PathVariable("bno") int bno, 
														 @PathVariable("page") int page)
																 		throws Exception{
		ResponseEntity<Map<String, Object>> entity = null;
		
		SearchCriteria cri = new SearchCriteria();
		cri.setPage(page);
		
		try {
			Map<String, Object> dataMap = service.getReplyList(bno, cri);
			entity = new ResponseEntity<Map<String, Object>>(dataMap,HttpStatus.OK);
		} catch (SQLException e) {
			e.printStackTrace();
			entity = new ResponseEntity<Map<String, Object>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		
		return entity;
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> register(@RequestBody ReplyRegistCommand replyReq)	
														throws Exception {
		
		ResponseEntity<String> entity=null;
		
		ReplyVO reply = replyReq.toReplyVO();
		try {
			service.registReply(reply);

			SearchCriteria cri = new SearchCriteria();

			Map<String, Object> dataMap = service.getReplyList(reply.getBno(),cri);
			PageMaker pageMaker = (PageMaker) dataMap.get("pageMaker");
			int realEndPage = pageMaker.getRealEndPage();

			entity = new ResponseEntity<String>(realEndPage+"",HttpStatus.OK);
		} catch (SQLException e) {
			e.printStackTrace();
			entity = new ResponseEntity<String>(e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return entity;
	}
}
