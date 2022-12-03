package code;

public class Node implements Comparable<Node>{
		String State;
		Node Parent;
		String Operator;
		int cost=0;
		int depth;
		int heur;
		boolean isGoal;
		// DFS and BFS
		public Node(String s, Node p,String o) {
			State=s;
			Parent=p;
			Operator=o;
			isGoal=false;
		}
		// IDS
		public Node(String s, Node p,String o,int d) {
			State=s;
			Parent=p;
			Operator=o;
			depth=d;
			isGoal=false;
		}
		// UC
		public Node(String s, Node p,String o,int d,int cost,int h) {
			State=s;
			Parent=p;
			Operator=o;
			depth=d;
			this.cost=cost;
			this.heur=h;
			isGoal=false;
		}
		@Override
		public int compareTo(Node arg0) {
			Node other=(Node)arg0;
			int oth=other.cost+other.heur;
			int mycost=this.cost+this.heur;
			//System.out.println("NODE:      OTHER:");
			//System.out.println(this+"         "+other);
			int diff=mycost-oth;
			//System.out.println("      "+diff);
			return diff;
		
		}
		
		public String toString() {
			return cost+"";
		}
}
