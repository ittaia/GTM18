package artzi.gtm.securePush1.coffee;
   
import artzi.gtm.utils.format.FormatDate;
import artzi.gtm.utils.xlsx.ExTabRow;

public class DataPoint {
	String serial ; 
	String dateTime ; 
	String GPIOName ; 
	double pulses ; 
	double duration ; 
	double speed ; 
		
	public DataPoint(String line) { 
		String [] lineV = line.split(",") ;
		
		this.pulses = Double.parseDouble(lineV[3]) ; 
		this.duration = Double.parseDouble(lineV[4]) ; 		 
		this.speed = Double.parseDouble(lineV[5]) ; 
	}
	public DataPoint(ExTabRow row) { 
		this.serial =   row.fieldValue("IoTen Serial") ;
		this.dateTime = row.fieldValue("dateTime") ; 
		
		
		this.GPIOName = row.fieldValue("GPIO Name") ; 
		this.pulses = row.fieldIntValue("Pulses") ;
		this.duration =  row.fieldDoubleValue("Duration") ;
		this.speed = this.pulses/this.duration ; 	 
	}	 

	public String getSerial() {
		return serial;
	}
	public String getDateTime() {
		return dateTime;
	}
	public String getGPIOName() {
		return GPIOName;
	}
	public double getPulses() {
		return pulses;
	}

	public double getDuration() {
		return duration;
	}

	public double getSpeed() {
		return speed;
	}	
}