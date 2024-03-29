package net.stenac.blitzbench.slave;


import java.util.ArrayList;
import java.util.List;

import net.stenac.blitzbench.model.WorkMessage;

public class WorkExecutor {
    SlaveStats stats = new SlaveStats();
    List<UserThread> userThreads = new ArrayList<UserThread>();

    public void start(WorkMessage message){
        for (int i = 0; i < message.concurrentUsers; i++) {
            UserThread thread = new UserThread();
            thread.work = message;
            thread.threadId = i;
            stats.users.add(thread.stats);
            userThreads.add(thread);
            System.out.println("Start thread");
            thread.start();
        }
    }
    
    public SlaveStats getStats() {
        return stats;
    }
    
    public void waitEnd() throws InterruptedException {
        for (UserThread thread : userThreads) {
            thread.join();
        }
    }
}
