package nuix;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedList;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.collections.map.Flat3Map;

import com.amazonaws.services.dynamodbv2.xspec.GetItemExpressionSpec;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class NuixMain {

	public static void main(String[] args) {
		
		AWSIOHandle awsHandle=new AWSIOHandle(Utils.AWS_ACCESSID,Utils.AWS_SECRETID,Utils.AWS_REGION);
		awsHandle.getDataFromS3(Utils.BUCKETNAME);
		
		UnzipFiles unzip=new UnzipFiles();
		ArrayList<String> fileList=unzip.doUnzip(Utils.FILEPATH);
		
		
		fileList.parallelStream().map(f->Utils.FILEPATH+f) // go through each csv file
               
              .map(f->{
				try {
					
					return Arrays.asList(new FileReader(f),f) ;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return null;
					
				}
			}).filter(x->x!=null && x instanceof List)
              
              .map(c->Arrays.asList(new CSVReader((FileReader)c.get(0)),c.get(1)))
              .filter(x->x!=null && x instanceof List)
              .map(reader->{
				try {
					return Arrays.asList(((CSVReader)reader.get(0)).readAll(),reader.get(1));   //CSVReader read all records
				} catch (IOException e) {
					
					e.printStackTrace();
					return null;
				}
			})
              .filter(x->x!=null && (x instanceof List))
              .map(x->{return Arrays.asList(((LinkedList<String[]>)x.get(0)).stream()  // go through each line of a csv file
            		  
            		  .filter(line->{
            			  
            			   String lineConcat=String.join(",",line);return lineConcat.toString().contains("ellipsis"); }) // get target records
        
            		  .collect(Collectors.toList()),x.get(1)) ;})
              .filter(element->element!=null && element instanceof List)
              .map(result -> {                                       // write data to new csv
				try {
				
					return csvWriter((List<String[]>)result.get(0),(String)result.get(1));
				} catch (Exception e) {
					
					e.printStackTrace();
					return null;
				}
			}).filter(element->element!=null && element instanceof String).forEach(fileName->{ // push file to AWS
				
				fileName=fileName+"";
				File file=new File(fileName);
				awsHandle.uploadDataToS3(Utils.BUCKETNAME, file.getName(), fileName);
			});
		     
	}
	
	private static String csvWriter(List<String[]> stringArray,String path) throws Exception {
		 
		 
	     CSVWriter writer = new CSVWriter(new FileWriter(path+".new"));
	     writer.writeAll(stringArray);
	     writer.close();
	     return path+".new";
	     
	}

}
