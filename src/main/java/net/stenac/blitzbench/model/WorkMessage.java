package net.stenac.blitzbench.model;

/**
 * Order sent by the master to the slave to make it work
 */
public class WorkMessage {
    public int concurrentUsers;
    public int requestsPerUser;
    
    public String workClass;
    
    public String baseURL;
}