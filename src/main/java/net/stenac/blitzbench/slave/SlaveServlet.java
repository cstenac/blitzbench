package net.stenac.blitzbench.slave;


import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import net.stenac.blitzbench.model.WorkMessage;

import com.google.gson.Gson;

public class SlaveServlet extends HttpServlet {
	 WorkExecutor we = new WorkExecutor();
     
	 @Override
	public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String type = req.getParameter("type");
        
        if ("work".equals(type)) {
            WorkMessage wmsg = new Gson().fromJson(req.getParameter("work"), WorkMessage.class);
            we.start(wmsg);
        } else if ("stats".equals(type)) {
        	resp.setContentType("application/json");
        	resp.getWriter().write(new Gson().toJson(we.getStats()));
        }
    }
}
