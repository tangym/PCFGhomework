import java.util.ArrayList;

/**
 * @author TYM
 *
 */
public class Node {
	public static String DELI = " ";
	public static int TERMINAL_LEAF = 0;
	public static int NONTERMINAL_LEAF = 1;
	public static int NONTERMINAL_NODE = 2;
	
	public Node left = null;
	public Node right = null;
	public String data = null;
	
	public Node() {}
	
	public Node(Node node) {
		if (null != node.left) {
			this.left = new Node(node.left);
		}
		else {
			this.left = null;
		}
		
		if (null != node.right) {
			this.right = new Node(node.right);
		}
		else {
			this.right = null;
		}
		
		if (null != node.data) {
			this.data = new String(node.data);
		}
		else {
			this.data = null;
		}
	}
	
	public String toString() { return toString(false,"");}
	
	public String toString(boolean recursive, String DELI) {
		if (recursive) {
			String rt = data;
			if (left != null) {
				rt = rt + DELI + left.data;
				if (right != null) {
					rt = rt + Node.DELI + right.data;
				}
			}
			return rt;
		}
		else {
			return data;
		}
	}

	public boolean equals(Node node) {
		
		if (this.getType() != node.getType()) {
			return false;
		}
		else {		
			//鍒ゆ柇鏁版嵁鍩熸槸鍚︾浉绛�
			if (node.data.equals(this.data)) {
				//瀵逛簬浜х敓寮忕殑鍒ゆ柇涓嶉渶瑕佸垽鏂瓙鏍戠殑瀛愭爲
				/*
				//鍒ゆ柇鍋氬瓙鏍戞槸鍚︾浉鍚�
				if ((node.left != null) && (this.left != null)) {
					if (node.left.equals(this.left)) {
						//鍒ゆ柇鍙冲瓙鏍戞槸鍚︾浉鍚�
						if ((node.right != null) && (this.right != null)) {
							return node.right.equals(this.right);
						}
						else if ((node.right == null) && (this.right == null)) {
							return true;
						}
						else {
							return false;
						}
					}
					else {
						return false;
					}
				} 
				else if ((null == node.left) && (null == this.left)) {
					return true;
				} 
				else {
					return false;
				}
				*/
				if (this.left != null) {
					if (this.left.data.equals(node.left.data)) {
						if (this.right != null) {
							if (this.right.data.equals(node.right.data)) {
								return true;
							}
							else {
								return false;
							}
						}
						else {
							return true;
						}
					}
					else {
						return false;
					}
				}
				else {
					return true;
				}
			}
			else {
				return false;
			}
		}
	}

	public int getType() {
		if (right != null) {
			return NONTERMINAL_NODE;
		}
		else if (left != null) {
			return NONTERMINAL_LEAF;
		}
		else {
			return TERMINAL_LEAF;
		}
	}

	public ArrayList<Node> traversal() {
		ArrayList<Node> nodeList = new ArrayList<>();
		addTraversal(nodeList, this);
		return nodeList;
	}
	
	private void addTraversal(ArrayList<Node> nodeList, Node node) {
		if (null != node) {
			nodeList.add(node);
			addTraversal(nodeList, node.left);
			addTraversal(nodeList, node.right);
		}
	}
}
