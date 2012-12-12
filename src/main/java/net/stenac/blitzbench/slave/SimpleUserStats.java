package net.stenac.blitzbench.slave;


import java.util.HashMap;
import java.util.Map;

public class SimpleUserStats {
    static class PerTypeStats {
        
        int totalRequests;
        long totalHeaderTime, totalBodyTime;
        long maxHeaderTime, maxBodyTime;
        
    }
  
    Map<String, PerTypeStats> types = new HashMap<String, SimpleUserStats.PerTypeStats>();
    
    public void addRequest(String type, long headerTime, long bodyTime) {
        PerTypeStats pts = types.get(type);
        if (pts == null) {
            pts = new PerTypeStats();
            types.put(type, pts);
        }
        pts.totalHeaderTime += headerTime;
        pts.maxHeaderTime = Math.max(pts.maxHeaderTime, headerTime);
        pts.totalBodyTime += bodyTime;
        pts.maxBodyTime = Math.max(pts.maxBodyTime, bodyTime); 
        pts.totalRequests++;
    }
}