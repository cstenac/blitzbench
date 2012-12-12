import net.stenac.blitzbench.model.WorkMessage;
import net.stenac.blitzbench.slave.WorkExecutor;


public class Main {
    public static void main(String[] args) throws Exception {
        WorkExecutor we = new WorkExecutor();
        
        WorkMessage wm = new WorkMessage();
        wm.baseURL = "http://test1.dataiku.com:8082/dwt1/p.gif";
        wm.concurrentUsers = 50;
        wm.requestsPerUser = 1000;
        wm.workClass = "net.stenac.blitzbench.slave.WT1WorkScript";
        we.start(wm);
        we.waitEnd();
    }
}
