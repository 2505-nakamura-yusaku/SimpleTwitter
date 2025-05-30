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
import chapter6.beans.User;
import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/edit" })
public class EditServlet extends HttpServlet {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public EditServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		String strMessageId = request.getParameter("editMessageId");
		List<String> errorMessages = new ArrayList<String>();
		HttpSession session = request.getSession();

		// パラメータの整合性チェック
		if (!isValidMessageId(strMessageId, errorMessages)) {
			session.setAttribute("errorMessages", errorMessages);
			response.sendRedirect("./");
			return;
		}

		int messageid = Integer.parseInt(strMessageId);
		request.setAttribute("editMessageId", messageid);

		String editMessageText = new MessageService().select(messageid).getText();
		request.setAttribute("editMessage", editMessageText);
		request.getRequestDispatcher("/edit.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		List<String> errorMessages = new ArrayList<String>();
		HttpSession session = request.getSession();

		String text = request.getParameter("text");
		if (!isValid(text, errorMessages)) {
			session.setAttribute("editMessage", text);
			session.setAttribute("errorMessages", errorMessages);
			response.sendRedirect("./edit.jsp");
			return;
		}

		// message更新のために必要な情報を取得
		Message message = new Message();
		message.setText(text);

		User user = (User) session.getAttribute("loginUser");
		message.setUserId(user.getId());

		String strMessageId = request.getParameter("editMessageId");
		// パラメータの整合性チェック
		if (!isValidMessageId(strMessageId, errorMessages)) {
			session.setAttribute("errorMessages", errorMessages);
			response.sendRedirect("./");
			return;
		}
		int messageid = Integer.parseInt(strMessageId);
		message.setId(messageid);

		new MessageService().update(message);
		response.sendRedirect("./");

	}

	private boolean isValid(String text, List<String> errorMessages) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		if (StringUtils.isBlank(text)) {
			errorMessages.add("メッセージを入力してください");
		} else if (140 < text.length()) {
			errorMessages.add("140文字以下で入力してください");
		}

		if (errorMessages.size() != 0) {
			return false;
		}
		return true;
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