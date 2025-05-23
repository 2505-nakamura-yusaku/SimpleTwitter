package chapter6.controller;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());




		String strMessageId = request.getParameter("editMessageId");
		int messageid = 0;

		if (strMessageId == null || strMessageId.length() == 0) {
			// パラメータからstrMessageIdが取得できなかったとき
			messageid = -1;
		} else {
			try {
				messageid = Integer.parseInt(strMessageId);
			} catch (NumberFormatException e) {
				messageid = -1;
			}
		}

		// String editMessage = request.getParameter("editMessage"); //削除予定
		String editMessageText = new MessageService().select(messageid).getText();
		request.setAttribute("editMessage", editMessageText);
		request.getRequestDispatcher("/edit.jsp").forward(request, response);
	}
}