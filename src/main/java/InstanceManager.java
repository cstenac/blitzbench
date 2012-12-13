

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
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

		AmazonEC2 ec2;
		RequestSpotInstancesResult reqResult;
		List<String> requestIds = new ArrayList<String>();
		List<String> publicIPs  = new ArrayList<String>();
		List<Boolean> ping = new ArrayList<Boolean>();
	}

	static class RequestedConfig {
		List<RequestedRegion> regions = new ArrayList<InstanceManager.RequestedRegion>();
	}

	public static void main(String[] args) throws Exception {
		List<RequestedRegion> requests = new ArrayList<InstanceManager.RequestedRegion>();

		{
			RequestedRegion r1 = new RequestedRegion();
			r1.region = "ec2.us-east-1.amazonaws.com";
			r1.requestedType = "c1.medium";
			r1.requestedInstances = 2;
			r1.spotPrice = "0.019";
			requests.add(r1);
			
			//r1.requestIds.add("sir-02e22a11"); r1.publicIPs.add(null); r1.ping.add(false);
			//r1.requestIds.add("sir-ad844e11"); r1.publicIPs.add(null); r1.ping.add(false);
		}

		AWSCredentials cred = new BasicAWSCredentials(
				FileUtils.readFileToString(new File("/Users/clement/Documents/ec2/access")).replace("\n", ""),
				FileUtils.readFileToString(new File("/Users/clement/Documents/ec2/secret")).replace("\n", ""));


		int totalInstances = 0;
		
		System.out.println("Requestsing ");
		for (RequestedRegion rregion : requests) {
			rregion.ec2 = new AmazonEC2Client(cred);
			rregion.ec2.setEndpoint(rregion.region);

			totalInstances += rregion.requestedInstances;
			
			RequestSpotInstancesRequest req = new RequestSpotInstancesRequest();
			req
			.withType(SpotInstanceType.OneTime)
			.withInstanceCount(rregion.requestedInstances)
			.withSpotPrice(rregion.spotPrice)
			.withLaunchSpecification(new LaunchSpecification().withInstanceType(InstanceType.C1Medium).withImageId("ami-1624987f"));

			rregion.reqResult = rregion.ec2.requestSpotInstances(req);
			for (SpotInstanceRequest s : rregion.reqResult.getSpotInstanceRequests()) {
				rregion.requestIds.add(s.getSpotInstanceRequestId());
				rregion.publicIPs.add(null);
				rregion.ping.add(false);
			}

		}


		while (true) {
			for (RequestedRegion rregion : requests) {
				DescribeSpotInstanceRequestsResult dres =
						rregion.ec2.describeSpotInstanceRequests(
								new DescribeSpotInstanceRequestsRequest().withSpotInstanceRequestIds(rregion.requestIds));
				int i = 0;
				for (SpotInstanceRequest sir : dres.getSpotInstanceRequests()) {
					if (sir.getState().equals("active") && rregion.publicIPs.get(i) == null) {
						DescribeInstancesRequest dir = new DescribeInstancesRequest()
						.withInstanceIds(sir.getInstanceId());
						DescribeInstancesResult res = rregion.ec2.describeInstances(dir);
						rregion.publicIPs.set(i, res.getReservations().get(0).getInstances().get(0).getPublicDnsName());
						System.out.println("" + new Gson().toJson(sir.getLaunchSpecification().getNetworkInterfaces()));
					}
					
					if (rregion.publicIPs.get(i) != null && rregion.ping.get(i) == false) {
						try {
							URL u = new URL("http://" + rregion.publicIPs.get(i) + ":8080/blitzbench/slave?type=stqts");
							HttpURLConnection uc = (HttpURLConnection) u.openConnection();
							uc.setConnectTimeout(800);
							uc.setReadTimeout(800);
							if (uc.getResponseCode() != 200) throw new Exception("Bad HTTP code");
							System.out.println("It pings");
						} catch (Exception e) {
							System.out.println("does not yet ping");
						}
					}
				
					System.out.println(rregion.region + " - " + sir.getSpotInstanceRequestId() +
							" " + sir.getState() + " - " + sir.getInstanceId() + " - " +rregion.publicIPs.get(i));
					
					i++;
				}
			}
			Thread.sleep(5000);
		}

//		AmazonEC2 ec2 = new AmazonEC2Client(cred);
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


		//        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(res));
	}
}

