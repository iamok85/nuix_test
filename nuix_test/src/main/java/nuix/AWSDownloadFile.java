package nuix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class AWSDownloadFile {
	 AmazonS3 s3client = null;
	
	String AWS_ACCESSID="";
	String AWS_SECRETID="";
	String AWS_REGION="";
	


	public AWSDownloadFile(String AWS_ACCESSID, String AWS_SECRETID, String AWS_REGION) {
		
		this.AWS_ACCESSID=AWS_ACCESSID;
		this.AWS_SECRETID=AWS_SECRETID;
		this.AWS_REGION=AWS_REGION;
	}

	private  void downLoadObect(String keyName) {

		System.out.println("Downloading object " + keyName);
		try {
			File file = new File(Utils.FILEPATH + keyName);
			if (!file.exists()) {
				S3Object o = s3client.getObject(Utils.BUCKETNAME, keyName);
				S3ObjectInputStream s3is = o.getObjectContent();
				FileOutputStream fos = new FileOutputStream(file);
				byte[] read_buf = new byte[1024];
				int read_len = 0;
				while ((read_len = s3is.read(read_buf)) > 0) {
					fos.write(read_buf, 0, read_len);
				}
				s3is.close();
				fos.close();
				System.out.println("Object downloaded " + keyName);

			}else {
				
				System.out.println("Object " + keyName+" is already exist.");
				
			}
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			System.exit(1);
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}

	}

	public  void getDataFromS3(String BUCKETNAME) {
		try {

			System.out.println("Establishing connection to AWS");
			BasicAWSCredentials awsCreds = new BasicAWSCredentials(AWS_ACCESSID, AWS_SECRETID);

			s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds))
					.withRegion(Utils.AWS_REGION).build();

			System.out.println("Listing objects");

			ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(BUCKETNAME).withMaxKeys(1);
			ListObjectsV2Result result;

			do {
				result = s3client.listObjectsV2(req);

				for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
					System.out.printf(" - %s (size: %d)\n", objectSummary.getKey(), objectSummary.getSize());
					downLoadObect(objectSummary.getKey());
				}

				String token = result.getNextContinuationToken();
				req.setContinuationToken(token);
			} while (result.isTruncated());
		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which" + " means your request made it "
					+ "to Amazon S3, but was rejected with an error response" + " for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means" + " the client encountered "
					+ "an internal error while trying to " + "communicate with S3, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}

}
