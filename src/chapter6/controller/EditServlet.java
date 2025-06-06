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
		Message checkMessage = null;

		// パラメータの整合性チェック
		// メッセージIDが数字か確認
		if (null != strMessageId) {
			if (strMessageId.matches("^[0-9]{1,}$")) {
				// メッセージIDが存在しているか確認
				int intMessageId = Integer.parseInt(strMessageId);
				checkMessage = new MessageService().select(intMessageId);
			}
		}

		if (null == checkMessage) {
			// メッセージIDが不正ならcheckMessageがnullなのでここで処理終了
			HttpSession session = request.getSession();
			errorMessages.add("不正なパラメータが入力されました");
			session.setAttribute("errorMessages", errorMessages);
			response.sendRedirect("./");
			return;
		}

		request.setAttribute("editMessage", checkMessage);// message型で統一		String editMessageText = new MessageService().select(checkMessage.getId()).getText();
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

		// message更新のために必要な情報を取得
		Message message = new Message();

		String strMessageId = request.getParameter("editMessageId");
		int messageid = Integer.parseInt(strMessageId);
		message.setId(messageid);

		String text = request.getParameter("editMessageText");
		message.setText(text);

		if (!isValid(text, errorMessages)) {
			request.setAttribute("editMessage", message);
			request.setAttribute("errorMessages", errorMessages);
			request.getRequestDispatcher("/edit.jsp").forward(request, response);
			return;
		}

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

}