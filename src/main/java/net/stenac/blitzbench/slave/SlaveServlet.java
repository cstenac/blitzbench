package net.stenac.blitzbench.slave;


import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import net.stenac.blitzbench.model.WorkMessage;

import com.google.gson.Gson;

public class SlaveServlet extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String type = req.getParameter("type");
        
        if ("work".equals(type)) {
            WorkMessage wmsg = new Gson().fromJson(req.getParameter("work"), WorkMessage.class);
        }
    }
}
