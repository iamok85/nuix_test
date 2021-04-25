package nuix;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.opencsv.CSVReader;





public class NuixMain {


	public static void main(String[] args) {
		
		AWSDownloadFile download=new AWSDownloadFile(Utils.AWS_ACCESSID,Utils.AWS_SECRETID,Utils.AWS_REGION);
		download.getDataFromS3(Utils.BUCKETNAME);
		
		UnzipFiles unzip=new UnzipFiles();
		ArrayList fileList=unzip.doUnzip(Utils.FILEPATH);
		

	}
   
	

}
