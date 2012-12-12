package net.stenac.blitzbench.slave;


import java.util.HashMap;
import java.util.Map;

public class UserStats {
    static class PerTypeStats {
        int totalRequests;
        int okRequests;
        int nokRequests;
        long totalHeaderTime, totalBodyTime;
        long maxHeaderTime, maxBodyTime;
    }

    Map<String, PerTypeStats> types = new HashMap<String, UserStats.PerTypeStats>();

    public void addRequest(String type, boolean ok, long headerTime, long bodyTime) {
        PerTypeStats pts = types.get(type);
        if (pts == null) {
            pts = new PerTypeStats();
            types.put(type, pts);
        }
        if (ok) {
            pts.totalHeaderTime += headerTime;
            pts.maxHeaderTime = Math.max(pts.maxHeaderTime, headerTime);
            pts.totalBodyTime += bodyTime;
            pts.maxBodyTime = Math.max(pts.maxBodyTime, bodyTime); 
            pts.okRequests++;
        } else {
            pts.nokRequests++;
        }
        pts.totalRequests++;
    }
}