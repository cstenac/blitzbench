package net.stenac.blitzbench.slave;


import net.stenac.blitzbench.model.WorkMessage;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.CoreConnectionPNames;

import com.google.gson.Gson;

/** Thread emulating the behaviour of one client */
public class UserThread extends Thread {
    WorkMessage work;
    UserStats stats = new UserStats();
    int threadId;

    public void run()  {
        try {
            WorkScript script = (WorkScript) Class.forName(work.workClass).newInstance();

            script.stats = stats;
            script.message = work;
            script.init();
            
            script.work(work.baseURL);
            
            System.out.println(new Gson().toJson(stats));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}