

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.CoreConnectionPNames;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsRequest;
import com.amazonaws.services.ec2.model.DescribeSpotInstanceRequestsResult;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryRequest;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryResult;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.LaunchSpecification;
import com.amazonaws.services.ec2.model.RequestSpotInstancesRequest;
import com.amazonaws.services.ec2.model.RequestSpotInstancesResult;
import com.amazonaws.services.ec2.model.SpotInstanceRequest;
import com.amazonaws.services.ec2.model.SpotInstanceType;
import com.amazonaws.services.ec2.model.SpotPrice;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class InstanceManager {
    static class RequestedRegion {
        String region;
        String requestedType;
        int requestedInstances;
        String spotPrice;
        
        List<String> requestIds = new ArrayList<String>();
    }
    
    static class RequestedConfig {
        
    }
    
    public static void main(String[] args) throws Exception {
        
        
        
        AWSCredentials cred = new BasicAWSCredentials(FileUtils.readFileToString(new File("/data/homes/stenac/ec2/access")).replace("\n", ""),
                FileUtils.readFileToString(new File("/data/homes/stenac/ec2/secret")).replace("\n", ""));
        AmazonEC2 ec2 = new AmazonEC2Client(cred);
        //ec2.setEndpoint("ec2.eu-west-1.amazonaws.com");	    

        /*
        {
            DescribeSpotPriceHistoryResult res = ec2.describeSpotPriceHistory(
                    new DescribeSpotPriceHistoryRequest().withInstanceTypes("c1.medium")
                    .withProductDescriptions("Linux/UNIX")
                    .withStartTime(new Date(System.currentTimeMillis() - 86400*1000))
                    );

            Collections.sort(res.getSpotPriceHistory(), new Comparator<SpotPrice>() {
                @Override
                public int compare(SpotPrice o1, SpotPrice o2) {
                    int a = o1.getAvailabilityZone().compareTo(o2.getAvailabilityZone());
                    if (a != 0 ) return a;
                    return o1.getTimestamp().compareTo(o2.getTimestamp());
                }
            });
            for (SpotPrice sp : res.getSpotPriceHistory()) {
                System.out.println(sp.getAvailabilityZone() + "\t" + sp.getSpotPrice() + "\t" + sp.getTimestamp());
            }
        }
        */

        RequestSpotInstancesRequest req = new RequestSpotInstancesRequest();
        req
        .withType(SpotInstanceType.OneTime)
        .withSpotPrice("0.019")
        .withLaunchSpecification(new LaunchSpecification().withInstanceType(InstanceType.C1Medium).withImageId("ami-1624987f"));

        RequestSpotInstancesResult res = ec2.requestSpotInstances(req);
        
        String id = res.getSpotInstanceRequests().get(0).getSpotInstanceRequestId();
        
        System.out.println("REQUEST IS " + id);
        
        while (true) {
            DescribeSpotInstanceRequestsResult dres =
                    ec2.describeSpotInstanceRequests(new DescribeSpotInstanceRequestsRequest().withSpotInstanceRequestIds(id));
            SpotInstanceRequest sir = dres.getSpotInstanceRequests().get(0);
            System.out.println(sir.getState() + " - " + sir.getInstanceId());
            Thread.sleep(5000);
        }
        //        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(res));
    }
}

