package net.stenac.blitzbench.slave;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.stenac.blitzbench.slave.UserStats.PerTypeStats;

public class SlaveStats {
    static class PerTypeSlaveStats {
        public long avgHeaderTime;
        public long avgBodyTime;
        
        public long curAvgHeaderTime;
        public long curAvgBodyTime;
        public double curQPS;

        
        private transient long prevTime;
        private transient long prevTotalHeaderTime;
        private transient long prevTotalBodyTime;
        private transient long prevTotalRequests;
    }
    
    
    public Map<String, PerTypeSlaveStats> types = new HashMap<String, PerTypeSlaveStats>();
    
 
    public void compute() {
        /* Fill missing entries in the global types map */
        for (UserStats us : users) {
            for (String type : us.types.keySet()) {
                if (!types.containsKey(type)) types.put(type, new PerTypeSlaveStats());
            }
        }
        
        long now = System.currentTimeMillis();
        
        for (String type : types.keySet()) {
            PerTypeSlaveStats out = types.get(type);

            long totalBodyTime = 0, totalHeadersTime = 0, totalRequests = 0;

            for (UserStats user : users) {
                PerTypeStats pts = user.types.get(type);
                if (pts == null) continue;
                
                totalBodyTime += pts.totalBodyTime;
                totalHeadersTime += pts.totalHeaderTime;
                totalRequests += pts.totalRequests;
            }
            
            if (out.prevTotalBodyTime != 0 && (totalRequests != out.prevTotalRequests)) {
                out.curAvgBodyTime = (totalBodyTime - out.prevTotalBodyTime) / (totalRequests - out.prevTotalRequests);
                out.curAvgHeaderTime = (totalHeadersTime - out.prevTotalHeaderTime) / (totalRequests - out.prevTotalRequests);
                out.curQPS = (double)(totalRequests - out.prevTotalRequests) / (double)(now - out.prevTime) * 1000f; 
            } else {
                out.curAvgBodyTime = 0;
                out.curAvgHeaderTime = 0;
                out.curQPS = 0.0;
            }
            
            out.avgBodyTime = totalBodyTime / totalRequests;
            out.avgHeaderTime = totalHeadersTime / totalRequests;
            out.prevTime = now;
            
            out.prevTotalBodyTime =totalBodyTime;
            out.prevTotalHeaderTime = totalHeadersTime;
            out.prevTotalRequests = totalRequests;
        }
    }
    
    public List<UserStats> users = new ArrayList<UserStats>();
}