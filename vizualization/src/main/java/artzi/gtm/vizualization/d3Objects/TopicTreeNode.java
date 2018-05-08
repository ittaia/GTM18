package artzi.gtm.vizualization.d3Objects;

import java.util.ArrayList;

import com.google.gson.Gson;

public class TopicTreeNode {
	TopicData topicData ; 
	ArrayList <TopicTreeNode> children ; 
	public TopicTreeNode (int id , int topicId , int group , String header , String [] terms ,int  size ) { 
		this.topicData = new TopicData (id ,topicId ,  group ,  header ,terms,  size  ) ; 
		children = new ArrayList <>() ; 
	}	
	public void addChild (TopicTreeNode treeNode) { 
		children.add(treeNode) ; 
	}
	public ArrayList <String> getJson () { 
		return getJsonr (0) ; 
	}
	private ArrayList <String> getJsonr (int level) {  
		Gson gson = new Gson () ; 
		ArrayList <String> js = new ArrayList <> () ; 
		String indent = "" ; for (int l = 0 ; l < level ; l++) indent+= "  "; 
		String s = indent+gson.toJson(topicData, TopicData.class) ;    
		if (children.size() == 0) { 
			js.add(s) ; 
			return js ; 	
		}
		else { 
			s=s.replace ("}" , ",") ; 
			js.add(s) ; 
			s = indent + "\"children\":[" ; 
			js.add(s) ; 
			for (int i = 0 ; i < children.size() ; i ++) {  
				TopicTreeNode child = children.get(i) ;  
				ArrayList <String> cj =child.getJsonr(level+1) ; 
				if (i < children.size()-1) { 
					cj.set(cj.size()-1, cj.get(cj.size()-1)+",") ; 
				}
				js.addAll(cj) ; 
			}
			s = indent + "]}" ; 
			js.add(s) ; 				
		}
		return js ; 
	}	
}