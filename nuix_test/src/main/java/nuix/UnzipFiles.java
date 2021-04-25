package nuix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipFiles {

	public ArrayList doUnzip(String destDir) {

		ArrayList fileList=new ArrayList();
		
		File f = new File(Utils.FILEPATH);
		if (f.exists()) {// Directory exist ??

			File[] matchingFiles = f.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith("zip");
				}
			});

			for (File file : matchingFiles) {

				return unzipFile(file, destDir) ;
					
					
				

			}
		}
		return fileList;

	}

	public ArrayList unzipFile(File file, String destDir) {
		ArrayList fileList=new ArrayList();
		System.out.println("Unzipping file " + file.getName());
		try {
			byte[] buffer = new byte[1024];
			ZipInputStream zis = new ZipInputStream(new FileInputStream(file));

			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {

				File newFile = new File(destDir, zipEntry.getName());
				// write file content
				if (!newFile.exists()) {
					FileOutputStream fos = new FileOutputStream(newFile);
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
				}
				else {
					System.out.println(newFile.getName()+" is already exists.");
				}
				fileList.add(newFile.getName());
				zipEntry = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
			
		}
		
		return fileList;
	}

}
