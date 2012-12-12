package net.stenac.blitzbench.slave;


import java.net.URLEncoder;

import org.apache.http.client.methods.HttpGet;

/**
 * A script that tests the WT1 tracker
 */
public class WT1WorkScript extends WorkScript{
    @Override
    public void work(String baseURL) throws Exception {
        GenerationUtils g = new GenerationUtils();
        
        String u = baseURL + "?__wt1ty=event";
        
        u+= "&__wt1ref=" + URLEncoder.encode(g.randomString(30), "utf8");
        u+= "&__wt1tzo=60";
        
        
        HttpGet get = new HttpGet(u);
        get.setHeader("Referer", "http://" + URLEncoder.encode(g.randomString(40), "utf8"));
        
        
        for (int i = 0 ; i < message.requestsPerUser; i++) {
//            System.out.println("Perform");
            performAndLogRequest("track", get);
            if (i % 100 == 0) {
                System.out.println("Disconnect");
                disconnect();
            }
        }
    }

    
}
