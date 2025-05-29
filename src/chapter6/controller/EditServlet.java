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

		int messageid = -1;
		if (strMessageId == null || strMessageId.length() == 0) {
			// パラメータからstrMessageIdが取得できなかったときの処理
			// 編集ページで更新ボタンが押下されたときの処理

			// トップから編集ボタン押下でページ遷移時にはnull、編集後は中身が入る
			String text = request.getParameter("text");
			if (!isValid(text, errorMessages)) {
				session.setAttribute("errorMessages", errorMessages);
				response.sendRedirect("./edit.jsp");
				return;
			}

			// message更新のために必要な情報を取得
			Message message = new Message();
			message.setText(text);

			User user = (User) session.getAttribute("loginUser");
			message.setUserId(user.getId());

			int messageId = (int) session.getAttribute("messageId");
			message.setId(messageId);

			new MessageService().update(message);
			response.sendRedirect("./");
			return;

		} else {
			messageid = Integer.parseInt(strMessageId);
			session.setAttribute("messageId", messageid);
		}

		String editMessageText = new MessageService().select(messageid).getText();
		request.setAttribute("editMessage", editMessageText);
		request.getRequestDispatcher("/edit.jsp").forward(request, response);
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

		// メッセージIDに数字以外が含まれているか確認
		char[] messageIdArray = messageId.toCharArray();
		for (int i = 0; i < messageId.length(); i++) {
			if (!Character.isDigit(messageIdArray[i])) {
				errorMessages.add("不正なパラメータが入力されました");
				return false;
			}
		}

		// メッセージIDが存在しているか確認
		int intMessageid = Integer.parseInt(messageId);
		Message checkMessageId = new MessageService().select(intMessageid);
		if (0 == checkMessageId.getId()) {
			errorMessages.add("不正なパラメータが入力されました");
		}

		if (errorMessages.size() != 0) {
			return false;
		}
		return true;
	}
}