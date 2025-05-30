package chapter6.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.Message;
import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/deleteMessage" })
public class DeleteMessageServlet extends HttpServlet {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public DeleteMessageServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		String strDeleteMessageid = request.getParameter("deleteMessageId");
		List<String> errorMessages = new ArrayList<String>();
		HttpSession session = request.getSession();

		// パラメータの整合性チェック
		if (!isValidMessageId(strDeleteMessageid, errorMessages)) {
			session.setAttribute("errorMessages", errorMessages);
			response.sendRedirect("./");
			return;
		}

		int deleteMessageid = Integer.parseInt(strDeleteMessageid);
		new MessageService().delete(deleteMessageid);
		response.sendRedirect("./");
	}

	private boolean isValidMessageId(String messageId, List<String> errorMessages) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		// メッセージIDが数字か確認
		if (StringUtils.isNumeric(messageId)) {
			// メッセージIDが存在しているか確認
			int intMessageid = Integer.parseInt(messageId);
			Message checkMessageId = new MessageService().select(intMessageid);
			if (0 != checkMessageId.getId()) {
				// メッセージIDが数字かつ存在していればtrue
				return true;
			}
		}

		// メッセージIDが不正なケース
		errorMessages.add("不正なパラメータが入力されました");
		return false;
	}
}
