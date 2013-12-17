import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;



/**
 * @author TYM
 *
 */
public class main {
	public static String TRAINING_SET_FILE = "training set.txt";
	public static String OUTPUT_PATH = "output/";
	public static String MODEL_FILE = OUTPUT_PATH + "model.txt";
	public static String PARSE_FILE = OUTPUT_PATH + "parse.txt";
	public static String DELI = " # "; 
	public static String FORMAT = "%1.5f";
	public static String[] sentence = {"A","boy","with","a","telescope","saw","a","girl"};
	private static ArrayList<Node> treeList = new ArrayList<>();
	private static ArrayList<Node> ruleList = new ArrayList<>();
	private static ArrayList<Integer> ruleCount = new ArrayList<>();
	private static float[] ruleProb;
	private static FileWriter parseWriter;
	
	public static void main(String[] args) {
		readTrainingSetFile();
		calRuleList();
		ruleProb = new float[ruleList.size()];
		normalizeRuleCount();
		sortRule();
		
		File path = new File(OUTPUT_PATH);
		path.mkdirs();
		
		writeModelFile();

		Node parseTree = new Node();
		
		for (int i=0; i<sentence.length-1; i++) {
			sentence[i] = sentence[i].toLowerCase();
		}
		
		parseTree=viterbi( "S", 0, sentence.length-1);
		
		File parseFile = new File(PARSE_FILE);
		try {
			if (parseFile.exists()) {
				System.out.println("Parse file already exists.");
			}
			parseWriter = new FileWriter(parseFile, false);
			
			writeParseTree(parseTree);
			writeProb();

			parseWriter.close();
		}
		catch (IOException ioe) {
			System.err.println(ioe);
		}
		
	}

	//璇诲彇training set.txt锛屽皢鏂囨湰杞寲涓烘爲锛屾坊鍔犲埌treeList涓�
	public static void readTrainingSetFile() {
		File ts = new File(TRAINING_SET_FILE); 
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(ts));
			
			String line = reader.readLine();
			while (null != line) {
				Node tree = createTree(line);
				treeList.add(tree);
				line = reader.readLine();
			}
			
			reader.close();
		}
		catch (FileNotFoundException fnfe) {
			System.err.println(fnfe.getStackTrace());
		}
		catch (IOException ioe) {
			System.err.println(ioe.getStackTrace());
		}
	}
	
	//灏嗕竴琛屾枃鏈琹ine杞寲涓烘爲
	public static Node createTree(String line) {
		
		
		StringTokenizer tokenizer = new StringTokenizer(line, "() ", true);
		Queue<String> qLine = new LinkedList<String>();
		while (tokenizer.hasMoreElements()) {
			qLine.offer(tokenizer.nextToken());
		}
		
		Node node = recurseCreate(qLine, null);
		
		return node;
	}
	
	//閫掑綊鐢熸垚鏍�
	private static Node recurseCreate(Queue<String> qLine, Node node) {
		//涓簭閫掑綊閬嶅巻
		if (!qLine.isEmpty()) {
			String token = qLine.peek();
			if (token.equals("(")) {
				qLine.poll();
				
				node = new Node();
				token = qLine.poll(); 
				node.data = token;
				node.left = recurseCreate(qLine, node.left);
				node.right = recurseCreate(qLine, node.right);
				if (")".equals(qLine.peek())) {
					qLine.poll();
					return node;
				}
				else {
					return null;
				}
			}
			//鍒嗛殧绗︿负绌烘牸鏃讹紝node鐨勭埗鑺傜偣娌℃湁鍙冲瓙鏍�
			else if (token.equals(" ")) {
				qLine.poll();
				
				node = new Node();
				token = qLine.poll();
				node.data = token.toLowerCase();
				return node;
			}
			else {
				return null;
			}
		}
		else {
			return null; 
		}
	} 

	public static void calRuleList() {
		for (int i=0; i<treeList.size(); i++) {
			//鍙栧嚭涓�５鏍戠殑鎵�湁鑺傜偣
			ArrayList<Node> nodeList = treeList.get(i).traversal();
			for (int j=0; j<nodeList.size(); j++) {
				Node node = nodeList.get(j);
				//褰撹妭鐐规槸鍙跺瓙鑺傜偣鏃讹紝涓嶆槸鑳借〃绀轰骇鐢熷紡锛岀洿鎺ュ幓闄�
				if (node.getType()==Node.TERMINAL_LEAF) {
					continue;
				}
				int k = 0;
				for (k=0; k<ruleList.size(); k++) {
					if (node.equals(ruleList.get(k))) {
						break;
					}
				}
				if (k >= ruleList.size()) {
					ruleList.add(node);
					
					k = ruleList.indexOf(node);
					ruleCount.add(k, 1);
				}
				else {
					ruleCount.set(k, ruleCount.get(k)+1);
				}
			}
		}
	}

	public static void normalizeRuleCount() {
		for (int i=0; i<ruleList.size(); i++) {
			int sum = 0;
			for (int j=0; j<ruleList.size(); j++) {
				//浜х敓寮忓乏閮ㄧ浉鍚�
				if (ruleList.get(i).data.equals(ruleList.get(j).data)) {
					sum += ruleCount.get(j);
				}
			}
			ruleProb[i] = (float) ruleCount.get(i) / sum;
		}
	}

	public static void sortRule() {
		for (int i=0; i<ruleList.size(); i++) {
			for (int j=i; j<ruleList.size(); j++) {
				if (ruleProb[i] < ruleProb[j]) {
					int nTemp = ruleCount.get(i);
					ruleCount.set(i, ruleCount.get(j));
					ruleCount.set(j, nTemp);
					
					Node oTemp = ruleList.get(i);
					ruleList.set(i, ruleList.get(j));
					ruleList.set(j, oTemp);
					
					float fTemp = ruleProb[i];
					ruleProb[i] = ruleProb[j];
					ruleProb[j] = fTemp;
				}
			}
		}
	}

	public static void writeModelFile() {
		File modelFile = new File(MODEL_FILE);
				
		try {
			if (modelFile.exists()) {
				System.out.println("Model file already exists.");
			}
			FileWriter writer = new FileWriter(modelFile, false);
			
			for (int i=0; i<ruleList.size(); i++) {
				writer.write(ruleList.get(i).toString(true, DELI) 
						+ DELI + ruleProb[i] + System.getProperty("line.separator"));
			}
			
			writer.close();
		}
		catch (IOException ioe) {
			System.err.println(ioe.getStackTrace());
		}
	}

	private static float delta(Node tree) {
		if (null != tree) {
			float rt = 0;
			for (int i=0; i<ruleList.size(); i++) {
				Node rule = ruleList.get(i);
				if (tree.equals(rule)) {
					rt = ruleProb[i];
					break;
				}
				else {
					continue;
				}
			}
			
			if (tree.getType() == Node.NONTERMINAL_NODE) {
				rt *= delta(tree.right);
				rt *= delta(tree.left);
			}
			return rt;
		}
		else {
			return 0;
		}
	}
	
 	public static Node viterbi(String A, int p, int q) {
		if (p > q) {
			return null;
		}
		else if (p == q) {
			for (int i=0; i<ruleList.size(); i++) {
				Node rule = ruleList.get(i);
				if (rule.data.equals(A) && rule.left.data.equals(sentence[p])) {
					return ruleList.get(i);
				}
				else {
					continue;
				}
			}			
			//瑙勫垯涓病鏈夎兘浜х敓璇ョ粨鏋勭殑浜х敓寮�
			return null;
		}
		//p<q鐨勬儏鍐�
		else {
			Node tree = null;			
			float max = 0;
			
			for (int i=0; i<ruleList.size(); i++) {
				Node rule = ruleList.get(i);
				
				if (rule.data.equals(A) && null!=rule.right) {
					Node tempNode = new Node();
					tempNode.data = rule.data;					
					for (int d=p; d<q; d++) {
						
						if (rule.getType() != Node.NONTERMINAL_LEAF) {
							tempNode.left = viterbi(rule.left.data, p, d);
							
							if (delta(tempNode.left) > 0) {
								tempNode.right = viterbi(rule.right.data, d+1, q);
							}
						}
						
						if (delta(tempNode) > max) {
							max = delta(tempNode);
							tree = new Node(tempNode);
						}
						else {
							continue;
						}
					}					
				}
			}
			
		
			/*
			// # DEBUG [START]
			if (maxIndex != -1) {
				System.out.println(ruleList.get(maxIndex).toString(true, " "));
			}
			else {
				//System.out.println("index is -1.");
			}
			// # DEBUG [END]
			 */
			
			return tree;
		}
	}

 	public static float outsideProb(String A, int p, int q) {
 		if (p > q) {
 			return 0;
 		}
 		else if (p==0 && q==sentence.length-1) {
 			return 1;
 		}
 		//p<q	
 		else {
 			float sum = 0;
 			for (int i=0; i<ruleList.size(); i++) {
 				Node rule = ruleList.get(i);
 				if (null != rule.right) {
 					if (rule.right.data.equals(A)) {
 						for (int e=0; e<p; e++) {
	 						sum += outsideProb(rule.data, e, q) * ruleProb[i] 
	 								* insideProb(rule.left.data, e, p-1);
 						}
 					}
 					else {
 						continue;
 					} 
 				}
 				else {
 					continue;
 				}
 			}
 			
 			for (int i=0; i<ruleList.size(); i++) {
 				Node rule = ruleList.get(i);
 				if (rule.left.data.equals(A)) {
 					for (int e=q+1; e<sentence.length; e++) {
 						sum += outsideProb(rule.data, p, e) * ruleProb[i] 
 								* insideProb(rule.right.data, q+1, e);
 					}
 				}
 			}
 			return sum;
 		}
 	}
 	
 	public static float insideProb(String A, int p, int q) {
		if (p > q) {
			return 0;
		}
		else if (p == q) {
			for (int i=0; i<ruleList.size(); i++) {
				Node rule = ruleList.get(i);
				if (rule.data.equals(A) && rule.left.data.equals(sentence[p])) {
					return ruleProb[i];
				}
				else {
					continue;
				}
			}			
			//瑙勫垯涓病鏈夎兘浜х敓璇ョ粨鏋勭殑浜х敓寮�
			return 0;
		}
		//p<q鐨勬儏鍐�
		else {
			float sum = 0;
			
			for (int i=0; i<ruleList.size(); i++) {
				Node rule = ruleList.get(i);
				if (rule.data.equals(A) && null!=rule.right) {
					for (int d=p; d<q; d++) {
						
						float delta = ruleProb[i];
						float leftDelta = insideProb(ruleList.get(i).left.data, p, d);
						if (0 < leftDelta) {
							float rightDelta = insideProb(ruleList.get(i).right.data, d+1, q);
							delta = delta * leftDelta * rightDelta;
						}
						else {
							delta = 0;
						}
						sum += delta;
					}					
				}
			}
			return sum;			
		}
	}

 	public static void writeParseTree(Node parseTree) throws IOException {
 		parseWriter.write("(" + getTreeString(parseTree) + ")"
 								+ System.getProperty("line.separator"));
		parseWriter.write(String.format(FORMAT, delta(parseTree)) 
								+ System.getProperty("line.separator"));
 	}
 	
 	private static String getTreeString(Node tree) {
 		if (tree.getType() == Node.NONTERMINAL_LEAF) {
 			return tree.toString(true, " ");
 		}
 		else if (tree.getType() == Node.NONTERMINAL_NODE) {
 			return tree.data + "(" + getTreeString(tree.left) + ")" 
 							 + "(" + getTreeString(tree.right) + ")";
 		}
 		else {
 			return "?";
 		}
 	} 

 	public static void writeProb() throws IOException {
 		//鍋锋噿鍋氭硶
 		
 		ArrayList<String> nonterminalList = new ArrayList<>();
 		for (int i=0; i<ruleList.size(); i++) {
 			String symbol = ruleList.get(i).data;
 			int j=0;
 			for (j=0; j<nonterminalList.size(); j++) {
 				if (symbol.equals(nonterminalList.get(j))) {
 					break;
 				}
 				else {
 					continue;
 				}
 			}
 			if (j < nonterminalList.size()) {
 				continue;
 			}
 			else {
 				nonterminalList.add(symbol);
 			}
 		}
 		
		for (int p=0; p<sentence.length; p++) {
			for (int q=p; q<sentence.length; q++) {
		 		for (int i=0; i<nonterminalList.size(); i++) {
 					float inside = insideProb(ruleList.get(i).data, p, q);
 					if (0 != inside) {
	 					float outside = outsideProb(ruleList.get(i).data, p, q);
	 					if (0 != outside) {
		 					String line = ruleList.get(i).data 
		 								+ DELI + p + DELI + q 
		 								+ DELI + String.format(FORMAT, inside) 
		 								+ DELI + String.format(FORMAT, outside) 
		 								+ System.getProperty("line.separator");
							parseWriter.write(line);
	 					}
 					}
 				}
 			}
 		}
 	}
}
