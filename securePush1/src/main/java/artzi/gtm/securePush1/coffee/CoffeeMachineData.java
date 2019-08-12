package artzi.gtm.securePush1.coffee;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import artzi.gtm.utils.clustering.Kmean;
import artzi.gtm.utils.config.Config;
import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.xlsx.ExRead1;
import artzi.gtm.utils.xlsx.ExRow;
import artzi.gtm.utils.xlsx.ExTabRow;
import artzi.gtm.utils.xlsx.ExWriteTable;

public class CoffeeMachineData {
	
	static String runPath = "C:\\TestDir\\SecurePush" ; 
	static String xlsxDir = "C:\\Partners\\securePush\\cof1x"; 
	static String dataDir = "C:\\Partners\\securePush\\cof1"; 
	static String [] files = {"f1", "f2" }  ; 	 
	
	public static void main(String[] args) throws Exception {
		System.out.println (runPath) ; 
		Config config = Config.getInstance(runPath) ; 
		
		EL.W("load data from dir"+ xlsxDir);
		for (String fn: files) { 
			readXlsx (fn) ; 
		}
		System.out.println ("end") ; 
	}
	
	private static void readXlsx (String fileName) throws IOException {
		String LastGPIOName = "" ; 
	 
		String  filePath = new File (xlsxDir , fileName + ".xlsx").getAbsolutePath() ; 
		EL.W("load data from "+ filePath);
		ArrayList <DataPoint> dataPoints = new ArrayList <>() ;	
		ArrayList <DataPoint> dataPointsWater = new ArrayList <>() ;
		ExRead1  xt = new ExRead1 (filePath);		
		LinkedList <String> sl = xt.ExGetSheetList() ; 
		String sheetName =  sl.get(0) ;     
		int numOfLines = xt.getSheetLinesNumber(sheetName) ; 
		System.out.println(numOfLines) ; 
		int cnt = 0 ; 
		for  (int indx = 1 ; indx <= numOfLines ; indx ++ ) {			
			ExTabRow row = new ExTabRow (xt, sheetName, indx) ;
			DataPoint dataPoint = new DataPoint(row) ;
			if ( ! (LastGPIOName.equals(dataPoint.getGPIOName()))) {
				if (cnt > 0) {
					String outFile = fileName+("_"+LastGPIOName) ;
					EL.W(outFile + ": num of recs: "+ cnt + 
							" data: "+ dataPoints.size() + " water: "+ dataPointsWater.size() ) ;
					processCoffee(dataPoints, outFile) ; 
					processWater(dataPointsWater, outFile ) ; 
					dataPoints = new ArrayList <>() ;	
					dataPointsWater = new ArrayList <>() ;
					cnt =  0 ; 
				}				
			}
			cnt += 1 ; 
			if (dataPoint.getSpeed() < 12) {
				dataPoints.add(dataPoint) ;
			}
			else {
				dataPointsWater.add(dataPoint) ;
			}
			LastGPIOName = dataPoint.getGPIOName() ; 
		}
		if (cnt > 0) {
			String outFile = fileName+(LastGPIOName) ; 
			EL.W(outFile + ": num of recs: "+ cnt + 
					" data: "+ dataPoints.size() + " water: "+ dataPointsWater.size() ) ;
			processCoffee(dataPoints, outFile) ; 
			processWater(dataPointsWater, outFile ) ; 
		}			 
	}
	
	private static void readcsv (String fileName) throws IOException { 
		String  filePath = new File (dataDir , fileName + ".txt").getAbsolutePath() ; 
		EL.W("load data from "+ filePath);
		ArrayList <DataPoint> dataPoints = new ArrayList <>() ;	
		ArrayList <DataPoint> dataPointsWater = new ArrayList <>() ;
		BufferedReader in = new BufferedReader(new FileReader(filePath));
		
		String line = in.readLine();
		int cnt = 0 ;  
		while (line != null){
			cnt += 1 ; 
			if (cnt >1) {  			 
				DataPoint dataPoint = new DataPoint(line) ; 
				if (dataPoint.getSpeed() < 12) {
					dataPoints.add(dataPoint) ;
				}
				else {
					dataPointsWater.add(dataPoint) ;
				}
			}
			line = in.readLine();
		}
		in.close();
		EL.W("num of recs: "+ cnt + " data: "+ dataPoints.size() + " water: "+ dataPointsWater.size() ) ;
		processCoffee(dataPoints, fileName) ; 
		processWater(dataPointsWater, fileName ) ; 
	}
	private static void processCoffee (ArrayList <DataPoint> dataPoints, String fileName) { 		
		ArrayList <double []> pulsesDots = new ArrayList<>() ; 
		ArrayList <double []> durationDots = new ArrayList<>() ; 
		ArrayList <double []> pulsesDurationDots = new ArrayList<>() ; 
		for (int dpIndx = 0; dpIndx < dataPoints.size(); dpIndx ++) {
			DataPoint dataPoint = dataPoints.get(dpIndx) ; 
			double [] dot = new double [1] ; 			
			dot[0] = dataPoint.getPulses() ; 		
			pulsesDots.add(dot) ;
			dot = new double [1] ; 			
			dot[0] = dataPoint.getDuration() ; 		
			durationDots.add(dot) ;
			dot = new double [2] ; 			
			dot[0] = dataPoint.getPulses() ; 
			dot[1] = dataPoint.getDuration() ; 
			pulsesDurationDots.add(dot) ;  
		}
		
		Kmean kmeanPulses = new Kmean(pulsesDots,1,6) ; 		
		kmeanPulses.printCenters();
		Kmean kmeanDuration = new Kmean(durationDots,1,6) ; 		
		kmeanDuration.printCenters();
		Kmean kmeanPulsesDuration = new Kmean(pulsesDurationDots,2,16) ; 		
		kmeanPulsesDuration.printCenters();
		Distances distPulses = new Distances(kmeanPulses, 20) ;  
		Distances distDuration = new Distances(kmeanDuration, 20) ;  
		Distances distPulsesDuration = new Distances(kmeanPulsesDuration, 20) ; 
		String xlsPath = new File (dataDir , fileName + ".xlsx").getAbsolutePath() ; 
		String [] SheetNames = {"Sheet1"} ;  
		String [] fieldNames = { "IoTen Serial ","-    DateTime     -","GPIO Name ","Pulses ","Duration ", "Speed ", 
				"Pulses cluster " ,"Pulses average " , "distance " , "distance mav ",
		/*		"Duration cluster" ,"Duration average " , "distance" , "distance mav", */
				"Pulse Duration cluster " ,"Pulse average " , "Duration average ", "distance " , "distance average 20"} ; 			 
		
		ExWriteTable slotsXls  = new ExWriteTable (SheetNames) ; 
		slotsXls.setSheetFieldNames(null, fieldNames) ;
		for (int i = 0 ; i < fieldNames.length ; i ++ ) {
			slotsXls.setColumnWidth(null, i, fieldNames[i].length()) ; 
		}		  
		  
		for (int dpIndx = 0; dpIndx < dataPoints.size(); dpIndx ++) {
			DataPoint dataPoint = dataPoints.get(dpIndx) ; 
			ExRow row = slotsXls.addNewRowToSheet(null) ; 
			row.setCell (0, dataPoint.getSerial()) ; 
			row.setCell (1, dataPoint.getDateTime()) ; 
			row.setCell (2, dataPoint.getGPIOName()) ; 
			row.setCell (3, dataPoint.getPulses()) ; 
			row.setCell (4, dataPoint.getDuration()) ; 
			row.setCell (5, dataPoint.getSpeed()) ;
			int center = kmeanPulses.getDotCenter()[dpIndx] ; 
			int start = 6 ; 
			row.setCell (start, center) ;
			row.setCell (start+1, kmeanPulses.getCenterList().get(center)[0]) ;
			row.setCell (start+2, distPulses.getDistance()[dpIndx]) ; 
			row.setCell (start+3, distPulses.getMovingAverage()[dpIndx]) ;
			/*
			start = 10 ; 
			center = kmeanDuration.getDotCenter()[dpIndx] ; 
			row.setCell (start, center) ;
			row.setCell (start+1, kmeanDuration.getCenterList().get(center)[0]) ;
			row.setCell (start+2, distDuration.getDistance()[dpIndx]) ; 
			row.setCell (start+3, distDuration.getMovingAverage()[dpIndx]) ;
			*/
			start = 10 ; 
			center = kmeanPulsesDuration.getDotCenter()[dpIndx] ; 
			row.setCell (start, center) ;
			row.setCell (start+1, kmeanPulsesDuration.getCenterList().get(center)[0]) ;
			row.setCell (start+2, kmeanPulsesDuration.getCenterList().get(center)[1]) ;
			row.setCell (start+3, distPulsesDuration.getDistance()[dpIndx]) ; 
			row.setCell (start+4, distPulsesDuration.getMovingAverage()[dpIndx]) ;
		}			 
		slotsXls.writeTable (xlsPath ) ;			
	}	
	private static void processWater (ArrayList <DataPoint> dataPoints, String fileName) { 
		ArrayList <double []> speedDots = new ArrayList<>() ; 
		for (int dpIndx = 0; dpIndx < dataPoints.size(); dpIndx ++) {
			DataPoint dataPoint = dataPoints.get(dpIndx) ; 
			double [] dot = new double [1] ; 			
			dot[0] = dataPoint.getSpeed() ; 		
			speedDots.add(dot) ;
		}
		
		Kmean kmeanSpeed = new Kmean(speedDots,1,1) ; 		
		kmeanSpeed.printCenters();
		Distances distSpeed = new Distances(kmeanSpeed, 10) ;  
		String xlsPath = new File (dataDir , fileName + "water" + ".xlsx").getAbsolutePath() ; 
		String [] SheetNames = {"Sheet1"} ;  
		String [] fieldNames = { "IoTen Serial ","-   DateTime    -","GPIO Name ","Pulses ","Duration ", "Speed ", 
				"Speed cluster " ,"Speed average " , "distance " , "distance average 10 "} ;  
		/*		"Duration cluster" ,"Duration average " , "distance" , "distance mav", */
		/*		"Pulse Duration cluster " ,"Pulse average " , "Duration average ", "distance " , "distance mav "} */ ; 			 
		
		ExWriteTable slotsXls  = new ExWriteTable (SheetNames) ; 
		slotsXls.setSheetFieldNames(null, fieldNames) ;
		for (int i = 0 ; i < fieldNames.length ; i ++ ) {
			slotsXls.setColumnWidth(null, i, fieldNames[i].length()) ; 
		}		  
		  
		for (int dpIndx = 0; dpIndx < dataPoints.size(); dpIndx ++) {
			DataPoint dataPoint = dataPoints.get(dpIndx) ; 
			ExRow row = slotsXls.addNewRowToSheet(null) ; 
			row.setCell (0, dataPoint.getSerial()) ; 
			row.setCell (1, dataPoint.getDateTime()) ; 
			row.setCell (2, dataPoint.getGPIOName()) ; 
			row.setCell (3, dataPoint.getPulses()) ; 
			row.setCell (4, dataPoint.getDuration()) ; 
			row.setCell (5, dataPoint.getSpeed()) ;
			int center = kmeanSpeed.getDotCenter()[dpIndx] ; 
			int start = 6 ; 
			row.setCell (start, center) ;
			row.setCell (start+1, kmeanSpeed.getCenterList().get(center)[0]) ;
			row.setCell (start+2, distSpeed.getDistance()[dpIndx]) ; 
			row.setCell (start+3, distSpeed.getMovingAverage()[dpIndx]) ;
			/*
			start = 10 ; 
			center = kmeanDuration.getDotCenter()[dpIndx] ; 
			row.setCell (start, center) ;
			row.setCell (start+1, kmeanDuration.getCenterList().get(center)[0]) ;
			row.setCell (start+2, distDuration.getDistance()[dpIndx]) ; 
			row.setCell (start+3, distDuration.getMovingAverage()[dpIndx]) ;
			 
			start = 10 ; 
			center = kmeanPulsesDuration.getDotCenter()[dpIndx] ; 
			row.setCell (start, center) ;
			row.setCell (start+1, kmeanPulsesDuration.getCenterList().get(center)[0]) ;
			row.setCell (start+2, kmeanPulsesDuration.getCenterList().get(center)[1]) ;
			row.setCell (start+3, distPulsesDuration.getDistance()[dpIndx]) ; 
			row.setCell (start+4, distPulsesDuration.getMovingAverage()[dpIndx]) ;
			*/
		}			 
		slotsXls.writeTable (xlsPath ) ;			
	}
	
}