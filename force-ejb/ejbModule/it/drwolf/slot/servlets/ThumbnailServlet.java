package it.drwolf.slot.servlets;

import it.drwolf.slot.alfresco.AlfrescoWrapper;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Lifecycle;

public class ThumbnailServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3293774599105200687L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Lifecycle.beginCall();
		AlfrescoWrapper aw = (AlfrescoWrapper) Component
				.getInstance("alfrescoWrapper");
		Object data = aw.getThumbnail(req.getParameter("id"),
				req.getParameter("name"));
		resp.setHeader("Content-Type", "image/png");
		if (data != null) {
			byte[] bytes = (byte[]) data;
			resp.getOutputStream().write(bytes);
			resp.getOutputStream().close();
			Lifecycle.endCall();
		}
	}
}
