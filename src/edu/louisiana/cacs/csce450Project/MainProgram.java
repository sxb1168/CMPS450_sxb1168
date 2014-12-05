package edu.louisiana.cacs.csce450Project;

import java.io.File;
import java.io.IOException;

public class MainProgram {
	
	

	public static void main(String[] args) {
		String fileName="";
		File location=new File(new File(".").getAbsolutePath());
		try {
			fileName=location.getCanonicalPath()+"\\data\\sample.txt";
			//System.out.println(filename);
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Parser p=new Parser(fileName);
		p.parse();

	}

}
