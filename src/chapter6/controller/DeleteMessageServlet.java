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
		int intMessageId = 0;
		Message checkMessage = null;

		// パラメータの整合性チェック
		// メッセージIDが数字か確認
		if (null != strDeleteMessageid) {
			if (strDeleteMessageid.matches("^[0-9]{1,}$")) {
				// メッセージIDが存在しているか確認
				intMessageId = Integer.parseInt(strDeleteMessageid);
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

		new MessageService().delete(intMessageId);
		response.sendRedirect("./");
	}
}
