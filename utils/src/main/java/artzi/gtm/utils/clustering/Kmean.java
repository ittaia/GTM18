package artzi.gtm.utils.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Kmean {
	int numCenters ; 
	int dimention ; 
	ArrayList<ArrayList<double []>> kmList ; 
	ArrayList<double []> centerList ; 
	
	public  HashMap<Integer, ArrayList<double[]>> kMean(ArrayList<double []> dotList, int numCenters){
		dimention = dotList.get(0).length; 
		
		createCenters(numCenters, centerList);
		initKmList(kmList, numCenters);
		dotToCenter(dotList, centerList, kmList);
		boolean change = true;
		while (change) {
			updateCenter(centerList, kmList);
			change = updateDot(centerList, kmList);
		}		
		return kmList;		
	}
	private  void createCenters(int numCenters, HashMap<Integer, Xa> centerList){
		double x;
		double y;
		for (int i = 0; i < numCenters; i++) {
			x = Math.random()*100;
			y = Math.random()*100;
			centerList.put(i,new Xa(x,y));
		}	
		System.out.println(centerList);
	}
	
	private void initKmList( HashMap<Integer, ArrayList<Xa>> kmList, int numCenters){
		for (int i = 0; i < numCenters; i++) {
			kmList.put(i, new ArrayList<Xa>());
		}
		System.out.println(kmList);
	}
	
	public void dotToCenter(Xa[] dotList, HashMap<Integer, Xa> centerList, HashMap<Integer, ArrayList<Xa>> kmList){
		Set<Integer> centerKeys = centerList.keySet();
		double min = -1;
		double dis;
		int minKey = -1;
		ArrayList<Xa> tempList;
		for (Xa dot : dotList) {
			for (Integer i : centerKeys) {
				dis = distance(centerList.get(i), dot);
				if (dis<min||min==-1){
					min = dis;
					minKey = i; 
				}
			}
			min = -1;
			tempList= kmList.get(minKey);
			tempList.add(dot);
			//kmList.replace(minKey, tempList);
		}
		System.out.println("dottocenter"+kmList);
	}
	public double distance (Xa center, Xa dot){
		double xDif = center.getX()-dot.getX();
		double yDif = center.getY()-dot.getY();
		double power = Math.pow(xDif,2)+Math.pow(yDif, 2);
		double rv = Math.sqrt(power);
		return rv;
	}
	
	public void updateCenter(HashMap<Integer, Xa> centerList, HashMap<Integer, ArrayList<Xa>> kmList){
		Set<Integer> centerKeys =centerList.keySet();
		double sumX=0;
		double sumY=0;
		Xa newCenter= new Xa(0,0);
		ArrayList<Xa> tempList;
		for (Integer i : centerKeys) {
			tempList = kmList.get(i);
			for (Xa dot : tempList){
				sumX+=dot.getX();
				sumY+=dot.getY();
			}
			newCenter.setX(sumX/kmList.get(i).size());
			newCenter.setY(sumY/kmList.get(i).size());
			//centerList.replace(i, newCenter);
			sumX=0;
			sumY=0;
		}
		System.out.println("updateCenter"+kmList);
	}
	
	public boolean updateDot(HashMap<Integer, Xa> centerList, HashMap<Integer, ArrayList<Xa>> kmList){
		boolean rv = false;
		Set<Integer> centerKeys = centerList.keySet();
		Set<Integer> kmKeys = kmList.keySet();
		double dis;
		double min = -1;
		int minKey = -1;
		HashMap<Integer, Xa> removeMap = new HashMap<>();
		HashMap<Integer, Xa> addMap = new HashMap<>();
		for (Integer dotInt : kmKeys) {			
			for (Xa dot : kmList.get(dotInt)) {
				for (Integer centerInt : centerKeys) {
					dis = distance(centerList.get(centerInt), dot);
					if (dis<min||min==-1) {
						min = dis;
						minKey = centerInt;
					}
				}
				min = -1;
				if (minKey != dotInt ) {
					removeMap.put(dotInt, dot);
					addMap.put(minKey, dot);
					rv = true;
//					System.out.println(removeMap);
//					System.out.println(addMap);
				}
				minKey = -1;
			}
			
		} 
		ArrayList<Xa> tempList;
		Xa tempDot;
		for(Integer removeKey : removeMap.keySet()){
			tempList = kmList.get(removeKey);
			tempDot = removeMap.get(removeKey);
			tempList.remove(tempDot);
			//kmList.replace(removeKey, tempList);
		}
		for(Integer addKey : addMap.keySet()){
			tempList = kmList.get(addKey);
			tempDot = addMap.get(addKey);
			tempList.add(tempDot);
			//kmList.replace(addKey, tempList);
		}			
		return rv;
	}
}