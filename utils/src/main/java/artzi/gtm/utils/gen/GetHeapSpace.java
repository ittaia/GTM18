package artzi.gtm.utils.gen;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

public class GetHeapSpace {
	public static long HS () {
		long usedHeapSpace = 0;		
		MemoryMXBean memBean = ManagementFactory.getMemoryMXBean() ;		 
		usedHeapSpace = memBean.getHeapMemoryUsage().getUsed();
		return usedHeapSpace ; 		 
	}

}