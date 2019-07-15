package artzi.gtm.securePush1.coffee;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import artzi.gtm.utils.clustering.Kmean;
import artzi.gtm.utils.config.Config;
import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.format.FormatNum;

public class Coffee {
	
	static String path = "C:\\TestDir\\SecurePush" ;   
	static String coffeeDir = "C:\\Partners\\securePush\\coffee"; 
	static String dataPath = new File (coffeeDir , "SN_c8df8441fee7_GR2.csv").getAbsolutePath() ; 
	static String outPath = new File (coffeeDir , "out1.csv").getAbsolutePath() ; 
	 
	
	public static void main(String[] args) throws Exception {
		System.out.println (path) ; 
		Config config = Config.getInstance(path) ; 
		
		EL.W("load data from "+ path);
		ArrayList <double []> dots = new ArrayList<>() ;
		ArrayList <Integer> lineDotIndx  = new ArrayList<>() ; 
		String header = "" ; 
		ArrayList <String> lines = new ArrayList <>() ; 
		BufferedReader in = new BufferedReader(new FileReader(dataPath));
		
		String line = in.readLine();
		int cnt = 0 ;  
		while (line != null){
			cnt += 1 ; 
			if (cnt ==1) header = line ; 
			else {
				lines.add(line) ; 
				double [] p = getData(line) ;
				if (p == null) {
					EL.W(" 0 duration " + line + "-" + lines.size());
					lineDotIndx.add(null) ; 
				}  
				else {
					lineDotIndx.add (dots.size()) ; 
					dots.add(p) ; 
				}
			}
			line = in.readLine();
		}
		in.close();
		EL.W("num of recs: "+ cnt + " legal data: "+ dots.size()) ; 		 
		Kmean kmean = new Kmean(dots,1,20) ; 
		kmean.printCenters();
		ArrayList<double []> centers = kmean.getCenterList() ; 
		int [] dotCenter = kmean.getDotCenter() ;
		BufferedWriter out = new BufferedWriter(new FileWriter(outPath));
		header += ",durationOk,speed,cluster,cluster center,diff" +"\n" ; 
		out.write(header);	
		for (int lineIndx = 0; lineIndx < lines.size(); lineIndx ++) {
			line = lines.get(lineIndx) ; 
			Integer dotIndx = lineDotIndx.get(lineIndx) ; 
			if (dotIndx == null) {
				line+= ",no,-1,-1,-1,-1"+ "\n" ; 
			}
			else {
				double speed = dots.get(dotIndx)[0] ; 
				int cluster = dotCenter[dotIndx] ; 
				double clusterCenter = centers.get(cluster)[0] ; 
				double diff = Math.abs(speed-clusterCenter) ; 
				line += ",yes," + FormatNum.format0(speed) + 
						"," + cluster + 
						"," + FormatNum.format0(clusterCenter) +
						"," + FormatNum.format0(diff) + "\n" ; 
			}
			out.write(line);
			 
		}
		out.close() ; 	 
		System.out.println("end ") ; 
	}

	private static double [] getData(String str) {
		String [] v = str.split(",") ;
		
		double  pulses = Double.parseDouble(v[5]) ; 
		double  duration = Double.parseDouble(v[6]) ; 
		if (duration <= 0) {
			return null ; 
		}
		double speed = pulses/duration ; 
		double [] dot = new double [3] ; 
		dot[0] = speed ; 
		dot[1] = duration ; 
		dot[2] = pulses ; 
		return dot ; 	
	}
}