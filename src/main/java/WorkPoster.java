import java.util.Collections;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;

import net.stenac.blitzbench.model.WorkMessage;
import net.stenac.blitzbench.slave.WorkExecutor;


public class WorkPoster {
    public static void doPostWork(String publicIP) throws Exception {
    	WorkMessage wm = new WorkMessage();
        wm.baseURL = "http://test1.dataiku.com:8082/dwt1/p.gif";
        wm.concurrentUsers = 4;
        wm.requestsPerUser = 1000;
        wm.workClass = "net.stenac.blitzbench.slave.WT1WorkScript";
        
        HttpClient client = new DefaultHttpClient();
        
        HttpPost post = new HttpPost("http://" + publicIP + ":8080/blitzbench/slave?type=work");

        NameValuePair nvp = new BasicNameValuePair("work", new Gson().toJson(wm));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(Collections.singletonList(nvp), "utf8");
        
        post.setEntity(entity);
        
        client.execute(post);
        
        //we.waitEnd();
    }
}
