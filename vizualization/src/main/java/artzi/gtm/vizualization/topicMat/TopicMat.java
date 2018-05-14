package artzi.gtm.vizualization.topicMat;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.Gson;

import artzi.gtm.topicModelInfra.trainedModel.TrainedMLModel;
import artzi.gtm.utils.aMath.KLDivergence;
import artzi.gtm.utils.clustering.PAM;
import artzi.gtm.utils.clustering.PAMResult;
import artzi.gtm.utils.elog.EL;
import artzi.gtm.utils.sortProb.CompareProb;
import artzi.gtm.utils.sortProb.IndxProb;
import artzi.gtm.vizualization.d3Objects.Link;
import artzi.gtm.vizualization.d3Objects.Node;
import artzi.gtm.vizualization.d3Objects.TopicTreeNode;
import mdsj.MDSJ;

public class TopicMat {
	TrainedMLModel tmodel ; 
	
	int numOfTopics ; 
	int numOfTerms ; 
	double [][] topicTermProb ; 
	double [][] topicMat ; 
	double[][] mdsMat ;
	static int numOfClusters = 10 ; 
	static int numOfTopTerms = 50 ; 
	PAMResult pamResult ; 
	ArrayList <TopicCluster> topicClusters ; 
	
	
	public TopicMat (String modelPath) throws IOException { 
		tmodel = TrainedMLModel.getInstance(modelPath) ; 
		System.out.println ( "Init Mat ")  ; 
		initMat (modelPath) ; 
		initMDS () ; 
		initClusters () ; 
		 
	}
	
	private void initMat(String modelPath) throws IOException {
		tmodel = TrainedMLModel.getInstance(modelPath) ; 
		topicTermProb = tmodel.getMultinomials().get(tmodel.getLevels()-1) ; 
		numOfTopics = topicTermProb.length ; 
		numOfTerms = topicTermProb[0].length ; 
		topicMat = new double[numOfTopics][numOfTopics] ; 
		for (int topicId = 0 ; topicId < numOfTopics ; topicId ++ ) { 
			topicMat [topicId][topicId] = 0 ;  
			for (int topic2 = topicId+1 ; topic2 < numOfTopics ; topic2 ++) { 
				topicMat [topicId][topic2] =  KLDivergence.getsym(topicTermProb[topicId] , topicTermProb[topic2]) ; 
				topicMat [topic2][topicId] = topicMat [topicId][topic2] ; 
			}
		}		
	}
	private void initMDS () { 
		mdsMat=MDSJ.classicalScaling(topicMat);		
	}
	
	private void initClusters() {
		PAM pam = new PAM (topicMat , numOfClusters) ; 
		pamResult = pam.getClusterAssignment() ; 
		topicClusters = new ArrayList <>() ; 
		for (int clusterId = 0 ; clusterId < numOfClusters ; clusterId ++) { 
			ArrayList <Integer> topicList = pamResult.getCluster(clusterId) ; 
			
			int [] topics = new int [topicList.size()]  ; 
			for (int i = 0 ; i < topics.length ; i ++ ) topics [i] = topicList.get(i) ; 
			IndxProb [] termProbArray  = new IndxProb [numOfTerms] ;
			for (int termIndx = 0 ; termIndx < numOfTerms ; termIndx ++ ) { 
				double prob = 0 ; 
				for (int topicId: topics) prob += topicTermProb[topicId] [termIndx] ;
				prob = prob / topics.length ; 
				termProbArray [termIndx] = new IndxProb (termIndx , prob) ; 
			}
			Arrays.sort ( termProbArray  ,  new CompareProb()) ; 
			String [] topTerms = new String [numOfTopTerms] ; 
			for (int i = 0 ; i < numOfTopTerms ; i ++) topTerms [i] = tmodel.getTerm(termProbArray[i].getIndx()) ; 
			topicClusters.add(new TopicCluster (clusterId , topics , topTerms)) ; 
			
		}
		
	}
	public void printTopicClusters() {
		for (int clusterId = 0 ; clusterId <numOfClusters ; clusterId ++ ) { 			
			EL.W(" ******* Topic cluster: " + clusterId   );			
			TopicCluster cluster = topicClusters.get(clusterId) ; 
			for (String term : cluster.getTopTerms()) { 
				EL.W("--- "+ term);
			}
			for (int topicId : cluster.getTopicIds()) { 
				EL.W("print topic - cluster: " + clusterId );
				tmodel.printTopic(topicId , 50);
			}
		}	
	}
 
	public void printCloseTopics() {
		for (int topicId = 0 ; topicId < numOfTopics ; topicId ++ ) { 
			IndxProb [] closeTopics = getCloseTopics (topicId) ; 
			EL.W("close topics - " + topicId );
			tmodel.printTopic(topicId , 50);
			for (int i = 0 ; i < numOfTopics ;  i ++ ) { 
				if (closeTopics [i].getProb() > 15 ) break ; 
				int topic2 = closeTopics[i].getIndx() ; 
				EL.W("close topics - " + topicId  + "- "+   topic2 + " - "+ closeTopics [i].getProb() + 
						" coordinates: "+ mdsMat[0][topic2]+" "+mdsMat[1][topic2] + tmodel.getHeader (topic2));
				//tmodel.printTopic(closeTopics[i].getIndx() , 50);
			}
		}		
	}
	
	public IndxProb [] getCloseTopics (int topicId) { 
		IndxProb [] topics = new IndxProb [numOfTopics] ; 
		for (int topic2 = 0 ; topic2 < numOfTopics ; topic2 ++ ) { 
			topics [topic2] = new IndxProb (topic2 , topicMat [topicId][topic2]) ;  
		}
		Arrays.sort(topics , (t1,t2) ->   Double.compare(t1.getProb(), t2.getProb()));
		return topics ; 		
	}
	public void writeGraph (String path) throws IOException { 
		ArrayList <Node> nodes = new ArrayList <>();
		ArrayList <Link> links = new ArrayList <>();
		for (int topicId = 0 ; topicId < numOfTopics ; topicId ++ ) { 
			Node node = new Node (topicId , mdsMat [0][topicId], mdsMat[1][topicId] , tmodel.getHeader1(topicId) , 
					tmodel.getTopTerms(topicId) , pamResult.getClusters()[topicId]) ; 
			
			node.setFxy();
			nodes.add(node) ; 
			for (int topic2 = topicId +1 ; topic2 < numOfTopics ; topic2++) { 
				if (topicMat [topicId][topic2] < 13 ) { 
					Link link = new Link (topicId , topic2 , 1.0/topicMat [topicId][topic2]) ; 
					links.add(link); 
				}
			}
		}
		Gson gson = new Gson () ; 
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)));		
		String s = "{\"nodes\":[" ; 
		out.write(s);
		out.newLine();
		for (int i = 0 ; i < nodes.size() ; i ++) { 
			Node node = nodes.get(i) ; 
			String json = gson.toJson(node ,Node.class) ; 
			if (i < nodes.size()-1) json += ","; 
			out.write(json);
			out.newLine();
		}
		s = "]," ; 
		out.write(s);
		out.newLine();
		s = "\"links\":[" ; 
		out.write(s);
		out.newLine();
		 
		for (int i = 0 ; i < links.size() ; i ++) { 
			Link link = links.get(i) ; 
			String json = gson.toJson(link ,Link.class) ; 
			if (i < links.size()-1) json += ",";  
			out.write(json);
			out.newLine();
		}
		s = "]}" ; 		
		out.write(s);
		out.close(); 
		System.out.println("Save "+ path);
	}
	public void writeHierarchy (String path) throws IOException { 	
		String []  noterms = {""} ; 
		int id = 0 ; 
		TopicTreeNode root = new TopicTreeNode  (id ,-1 , -1 , " root " , noterms , 100) ; 		 
		for (TopicCluster cluster : topicClusters) { 
			id += 1 ; 			 
			TopicTreeNode topicGroup = new TopicTreeNode  (id ,-1 , cluster.getClusterId() , " group;"+cluster.getClusterId() ,
			 cluster.getTopTerms() , 100) ;
			root.addChild(topicGroup);
			for (int topicId : cluster.getTopicIds() ) { 
				id += 1 ; 			 
				TopicTreeNode topic = new TopicTreeNode  (id ,topicId , cluster.getClusterId() , tmodel.getHeader1(topicId) , 
						tmodel.getTopTerms(topicId) , 100) ;
				topicGroup.addChild(topic);
			}
		}	
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)));		 
		for (String s : root.getJson()) { 			 
			out.write(s);
			out.newLine();
		}		
		out.close(); 
		System.out.println("Save "+ path);
	}
}