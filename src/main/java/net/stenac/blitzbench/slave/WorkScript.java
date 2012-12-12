package net.stenac.blitzbench.slave;


import java.io.InputStream;

import net.stenac.blitzbench.model.WorkMessage;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.CoreConnectionPNames;

public abstract class WorkScript {
    WorkMessage message;
    UserStats stats;
    HttpClient httpclient;
    ClientConnectionManager cm;

    public void disconnect() {
        cm.shutdown();
        init();
    }

    public void init() {
        cm = new BasicClientConnectionManager();
        httpclient = new DefaultHttpClient(cm);
        httpclient.getParams()
        .setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 30000)
        .setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000)
        .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
        .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true);

    }

    public void performAndLogRequest(String type, HttpGet get) {
        try {
            long before = System.nanoTime();

            HttpResponse resp = httpclient.execute(get);
            int code = resp.getStatusLine().getStatusCode();

            long atCode = System.nanoTime();

            InputStream is = resp.getEntity().getContent();
            IOUtils.toByteArray(is);
            is.close();
            long atEnd = System.nanoTime();
            stats.addRequest(type, code==200, (atCode-before)/1000, (atEnd-atCode)/1000);
        } catch (Exception e) {
            stats.addRequest(type, false, 0, 0);
        }
    }

    public abstract void work(String baseURL) throws Exception;
}
