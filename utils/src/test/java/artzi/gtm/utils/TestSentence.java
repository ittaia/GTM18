package artzi.gtm.utils;

import artzi.gtm.utils.textUtils.Sentence;

public class TestSentence {

	public static void main(String[] args) {
		String text =  "A plant growing system for moving growing plants subject to positive I.B.M. control, "
				+ "the system including: a first guidance panel with a panel center and panel slots, "
				+ "a further guidance panel with a further guidance panel center and further guidance panel "
				+ "slots providing guiding tracks for a multitude of plant "
				+ "holding elements. The first and further guidance panels are coaxially arranged and "
				+ "adapted on top of each other to provide a rotational movement between each other around a "
				+ "common rotation axis. Plant openings for the plant holding elements are formed at "
				+ "intersections of the first and further guidance panel slots. Plant openings are distributed in a "
				+ "spiral-like pattern around the rotation axis showing a sense of rotation,"
				+ " wherein in the sense of rotation of the spiral-like pattern, several or all spiral-adjacent"
				+ "plant openings are arranged at a plant opening angle with respect to the rotation axis." ; 
		String [] lines = Sentence.TextIntoLines(text) ; 
		for (String line : lines) System.out.println (line) ; 
	
	}

}
