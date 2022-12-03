package code;

import java.util.*;
import java.util.concurrent.TimeUnit;
import visualization.Visualize;

public class Matrix {
	static PriorityQueue<Node> pq;
	static ArrayList<Node> q;
	static Hashtable<String, Boolean> h;
	static Object view;
	static String strat;
	static boolean[][] dp;
	static int[][] matrix;
	public static int M;
	public static int N;
	static boolean[][] gridBool;
	static String[][] gridString;
	static String grid;

	static Hashtable<String, String> p = new Hashtable<String, String>();

	public static void eh(String s) {
		solve(p.get(s), "UC", true);
		// solve(p.get(s), "AS1", false);
		// solve(p.get(s), "AS2", true);
	}

	public static void lol() {
		Enumeration<String> e = p.keys();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			// System.out.println(key);
			//System.out.println(key + ":");
			solve(p.get(key), "AS1", false);
			//solve(p.get(key), "AS1", false);
			//solve(p.get(key), "AS2", false);
			//System.out.println("");
		}

	}
	
	public static void main(String[] args) {

		
		String grid1 = "5,5;1;1,4;1,0;0,4;0,0,2,2;3,4,4,2,4,2,3,4;0,2,32,0,1,38";
		String grid2 = "5,5;2;3,2;0,1;4,1;0,3;1,2,4,2,4,2,1,2,0,4,3,0,3,0,0,4;1,1,77,3,4,34";
		String grid3 = "5,5;1;0,4;4,4;0,3,1,4,2,1,3,0,4,1;4,0;2,4,3,4,3,4,2,4;0,2,98,1,2,98,2,2,98,3,2,98,4,2,98,2,0,1";
		//String grid4 = "5,5;1;0,4;4,4;0,3,1,4,2,1,3,0,4,1;4,0;2,4,3,4,3,4,2,4;0,2,98,1,2,98,2,2,98,3,2,98,4,2,98,2,0,98,1,0,98";
		String grid5 = "5,5;2;0,4;3,4;3,1,1,1;2,3;3,0,0,1,0,1,3,0;4,2,54,4,0,85,1,0,43";
		String grid6 = "5,5;2;3,0;4,3;2,1,2,2,3,1,0,0,1,1,4,2,3,3,1,3,0,1;2,4,3,2,3,4,0,4;4,4,4,0,4,0,4,4;1,4,57,2,0,46";
		String grid7 = "5,5;3;1,3;4,0;0,1,3,2,4,3,2,4,0,4;3,4,3,0,4,2;1,4,1,2,1,2,1,4,0,3,1,0,1,0,0,3;4,4,45,3,3,12,0,2,88";
		String grid8 = "5,5;2;4,3;2,1;2,0,0,4,0,3,0,1;3,1,3,2;4,4,3,3,3,3,4,4;4,0,17,1,2,54,0,0,46,4,1,22";
		String grid9 = "5,5;2;0,4;1,4;0,1,1,1,2,1,3,1,3,3,3,4;1,0,2,4;0,3,4,3,4,3,0,3;0,0,30,3,0,80,4,4,80";
		
//		p.put("0", grid0);
//		p.put("1", grid1);
//		p.put("2", grid2);
//		p.put("3", grid3);
//		p.put("4", grid4);
//		p.put("5", grid5);
//		p.put("6", grid6);
//		p.put("7", grid7);
//		p.put("8", grid8);
//		p.put("9", grid9);
//		p.put("10", grid10);
//		//lol();
//		String grid = genGrid();
//		System.out.println(grid);
//		String[] split = grid.split(";");
//		System.out.println(split.length);
		String grid0 = "5,5;2;3,4;1,2;0,3,1,4;2,3;4,4,0,2,0,2,4,4;2,2,91,2,4,62";
		String grid10 = "5,5;4;1,1;4,1;2,4,0,4,3,2,3,0,4,2,0,1,1,3,2,1;4,0,4,4,1,0;2,0,0,2,0,2,2,0;0,0,62,4,3,45,3,3,39,2,3,40";
		String grid4 = "5,5;1;0,4;4,4;0,3,1,4,2,1,3,0,4,1;4,0;2,4,3,4,3,4,2,4;0,2,98,1,2,98,2,2,98,3,2,98,4,2,98,2,0,98,1,0,98";
		
		//solve(grid0,"BF",false);
		//solve(grid0,"DF",false);
		//solve(grid0,"ID",false);
		//solve(grid0,"UC",false);
		//solve(grid0,"AS1",false);
		//solve(grid0,"AS2",false);
		//solve(grid0,"GR1",false);
		//solve(grid0,"GR2",false);
		System.out.println("");
		solve(grid0,"GR1",false);
		
		
	}

	
	public static int distHelper(int M,int N,int NeoX,int NeoY,Loc TB,ArrayList<Pad> pads) {
		if(TB.x==NeoX&&TB.y==NeoY) {
			return 0;
		}
		if(dp[NeoX][NeoY]) {
			return 9999999;
		}
		dp[NeoX][NeoY]=true;

		int up=Integer.MAX_VALUE;
		if(NeoX-1>=0) {
			up=distHelper(M,N,NeoX-1,NeoY,TB,pads);
		}
		int down=Integer.MAX_VALUE;
		if(NeoX+1<N) {
			down=distHelper(M,N,NeoX+1,NeoY,TB,pads);
		}
		int right=Integer.MAX_VALUE;
		if(NeoY+1<M) {
			right=distHelper(M,N,NeoX,NeoY+1,TB,pads);
		}
		int left=Integer.MAX_VALUE;
		if(NeoY-1>=0) {
			left=distHelper(M,N,NeoX,NeoY-1,TB,pads);
		}
		int fly=Integer.MAX_VALUE;
		for(int i=0;i<pads.size();i++) {
			Pad cur=pads.get(i);
			if(cur.StartLoc.x==NeoX&&cur.StartLoc.y==NeoY) {
					fly=distHelper(M,N,cur.FinishLoc.x,cur.FinishLoc.y,TB,pads);
			}
		}
		dp[NeoX][NeoY]=false;

		int fin=Integer.min(up, right);
		fin=Integer.min(fin, down);
		fin=Integer.min(fin, left);
		fin=Integer.min(fin, fly);
		return fin+1;
	}
	

	public static int calcPath(Grid oldGrid, Grid newGrid, String action,int depth) {
		if (strat.equals("DF") || strat.equals("BF") || strat.equals("GR1") || strat.equals("GR2")) {
			return 0;
		}
		return (newGrid.died*100000)+(newGrid.killed*1000)+depth+1;
	}
	
	public static int calculateHeur(Grid curGrid) {
		if (strat.equals("GR1")||strat.equals("AS1")) {
			return calculateH1(curGrid);
		}
		if (strat.equals("GR2")||strat.equals("AS2")) {
			return calculateH2(curGrid);
		}
		return 0;
	}
	
	public static int calculateH1(Grid curGrid) {
		int dist = curGrid.Neo.same(curGrid.TB) ? 0 : 1;
		return curGrid.Carry.size() + curGrid.Hostages.size() + curGrid.NewAgents.size()+dist;
	}
	
	public static int calculateH2(Grid curGrid) {
		int rem=curGrid.Carry.size() + curGrid.Hostages.size() + curGrid.NewAgents.size()==0?0:1;
		return matrix[curGrid.Neo.x][curGrid.Neo.y]+rem;
				//+curGrid.Carry.size() + curGrid.Hostages.size() + curGrid.NewAgents.size();
	}
	
	public static void fillMatrix(Grid g) {
		matrix=new int[g.N][g.M];
		for(int i=0;i<matrix.length;i++) {
			for(int j=0;j<matrix[i].length;j++) {
				matrix[i][j]=calcDist(g.M,g.N,i,j,g.Pads,g.TB);
			}
		}
	}
	public static int calcDist(int M,int N,int NeoX,int NeoY,ArrayList<Pad> Pads,Loc TB) {
		if(NeoX==TB.x&&NeoY==TB.y) {
			return 0;
		}
		dp=new boolean[N][M];
		return distHelper(M,N,NeoX,NeoY,TB,Pads);
	}
	
	
	public static String solve(String grid, String strategy, boolean visualize) {
		strat = strategy;
		h = new Hashtable<String, Boolean>();
		q = new ArrayList<Node>();
		String s = "";
		if (strategy.equals("BF")) {
			grid += ";_;_;0;0;0;0;0;0";
			h.put(grid, true);
			q.add(new Node(grid, null, "", 0, 0, 0));
			s = BFS();
		}
		if (strategy.equals("DF")) {
			grid += ";_;_;0;0;0;0;0;0";
			h.put(grid, true);
			q.add(new Node(grid, null, "", 0, 0, 0));
			s = DFS();
		}
		if (strategy.equals("ID")) {
			s = IDS(grid);
		}

		if (strategy.equals("UC")) {
			grid += ";_;_;0;0;0;0;0;0";
			h.put(grid, true);
			pq = new PriorityQueue<Node>();
			pq.add(new Node(grid, null, "", 0, 0, 0));
			s = UCS(grid);
		}
		if (strategy.equals("AS1") || strategy.equals("GR1")) {
			grid += ";_;_;0;0;0;0;0;0";
			h.put(grid, true);
			pq = new PriorityQueue<Node>();
			Grid g=new Grid(grid);
			pq.add(new Node(grid, null, "", 0, 0, calculateH1(g)));
			s = UCS(grid);
		}
		if (strategy.equals("AS2")||strategy.equals("GR2")) {
			
			grid += ";_;_;0;0;0;0;0;0";
			h.put(grid, true);
			Grid g=new Grid(grid);
			fillMatrix(g);
			pq = new PriorityQueue<Node>();
			pq.add(new Node(grid, null, "", 0, 0, 0));
			s = UCS(grid);
		}
		System.out.println(s);
		if (visualize) {
			new Visualize(grid, s);
		}
		return s;
	}

	public static String UCS(String initialGrid) {
		int nodes = 0;
		while (!pq.isEmpty()) {
			Node resolve = pq.poll();
			ArrayList<Node> states = resolveState(resolve);
			nodes++;
			if (!states.isEmpty()) {
				if (states.size() == 1) {
					if (states.get(0).isGoal) {
						String moves = extractPath(states.get(0));
						return moves + ";" + nodes;
					}
				}
				while (!states.isEmpty()) {
					Node n=states.remove(0);
					if(n==null) {
						System.out.println("NULL STATE------------------");
					}
					pq.add(n);
				}
			}
		}
		return "No Solution";
	}

	public static String IDS(String initialGrid) {
		initialGrid += ";_;_;0;0;0;0;0;0";
		Node root = new Node(initialGrid, null, "", 0);
		int maxLevel = -1;
		int curMaxLevel = -1;
		int nodes = 0;
		while (true) {
			if (curMaxLevel > maxLevel) {
				break;
			}
			curMaxLevel++;
			h.clear();
			q.clear();
			q.add(root);
			while (!q.isEmpty()) {
				Node resolve = q.remove(0);
				ArrayList<Node> states = resolveState(resolve);
				nodes++;
				if (!states.isEmpty()) {
					if (states.get(0).isGoal) {
						String moves = extractPath(states.get(0));
						return moves + ";" + nodes;
					}
					while (!states.isEmpty()) {
						Node n = states.remove(0);
						maxLevel = Math.max(n.depth, maxLevel);
						if (n.depth <= curMaxLevel) {
							q.add(n);
						}
					}
				}
			}
		}
		return "No Solution";
	}

	public static String DFS() {
		int nodes = 0;
		while (!q.isEmpty()) {
			Node resolve = q.remove(0);
			ArrayList<Node> states = resolveState(resolve);
			nodes++;
			if (!states.isEmpty()) {
				if (states.get(0).isGoal) {
					String moves = extractPath(states.get(0));
					return moves + ";" + nodes;
				}
				while (!states.isEmpty()) {
					q.add(0, states.remove(0));
				}
			}
		}
		return "No Solution";
	}

	public static String BFS() {
		int nodes = 0;
		while (!q.isEmpty()) {
			Node resolve = q.remove(0);
			ArrayList<Node> states = resolveState(resolve);
			nodes++;
			if (!states.isEmpty()) {
				if (states.get(0).isGoal) {
					String moves = extractPath(states.get(0));
					return moves + ";" + nodes;
				}
				while (!states.isEmpty()) {
					q.add(states.remove(0));
				}
			}
		}
		return "No Solution";
	}

	public static String extractPath(Node cur) {
		String state = cur.State;
		String[] split = state.split(";");
		String deaths = split[11];
		String kills = split[12];
		String moves = "";
		ArrayList<String> moves_list = new ArrayList<String>();
		while (cur != null) {
			//System.out.println(cur.Operator+" H: "+cur.heur+"  C: "+cur.cost);
			moves_list.add(cur.Operator);
			cur = cur.Parent;
		}
		for (int i = moves_list.size() - 1; i >= 0; i--) {
			moves = moves + "," + moves_list.get(i);
		}
		return moves.substring(2) + ";" + deaths + ";" + kills;
	}

	public static ArrayList<Node> resolveState(Node curNode) {
		ArrayList<Node> states = new ArrayList<Node>();
		String state = curNode.State;
		if (state == null) {
			System.out.println("STATE IS NULL");
			return states;
		}
		Grid grid = new Grid(state);
		boolean goal = grid.checkGoal();

		if (goal) {
			curNode.isGoal = true;
			states.add(curNode);
			return states;
		}
		String check = "";
		// NEW
		Grid drop = grid.copy();
		Grid pill = grid.copy();
		Grid carry = grid.copy();
		Grid fly = grid.copy();
		Grid right = grid.copy();
		Grid left = grid.copy();
		Grid up = grid.copy();
		Grid down = grid.copy();
		Grid kill = grid.copy();
		//// CARRY HOSTAGE
		if (carry.Carry.size() < carry.C) {
			int hostIdx = checkHostagePresent(carry.Neo, carry.Hostages);
			if (hostIdx != -1) {
				carry.carryHostage(hostIdx);
				carry.applyDamageToHostages();
				check = carry.encodeString();
				String[] s = check.split("!");
				if (!h.containsKey(s[1])) {
					h.put(s[1], true);
					int path = calcPath(grid, carry, "carry",curNode.depth);
					int heur = calculateHeur(carry);
					states.add(new Node(s[0], curNode, "carry", curNode.depth + 1, path+curNode.cost, heur));
				}
			}
		}
		///// TAKE PILL
		int pillIdx = checkPillPresent(pill.Neo, pill.Pills);
		if (pillIdx != -1) {
			pill.takePill(pillIdx);
			check = pill.encodeString();
			String[] s = check.split("!");
			if (!h.containsKey(s[1])) {
				h.put(s[1], true);
				int path = calcPath(grid, pill, "takePill",curNode.depth);
				int heur = calculateHeur(pill);
				states.add(new Node(s[0], curNode, "takePill", curNode.depth + 1, path+curNode.cost, heur));
			}
		}
		//// DROP HOSTAGES
		if (drop.Neo.same(drop.TB)) {
			if (drop.Carry.size() > 0) {
				drop.dropHostages();
				drop.applyDamageToHostages();
				check = drop.encodeString();
				String[] s = check.split("!");
				if (!h.containsKey(s[1])) {
					h.put(s[1], true);
					int path = calcPath(grid, drop, "drop",curNode.depth);
					int heur = calculateHeur(drop);
					states.add(new Node(s[0], curNode, "drop", curNode.depth + 1, path+curNode.cost, heur));
				}
			}
		}

		/// FlY
		int padIdx = checkPadPresent(fly.Neo, fly.Pads);
		if (padIdx != -1) {
			fly.fly(padIdx);
			fly.applyDamageToHostages();
			check = fly.encodeString();
			String[] s = check.split("!");
			if (!h.containsKey(s[1])) {
				h.put(s[1], true);
				int path = calcPath(grid, fly, "fly",curNode.depth);
				int heur = calculateHeur(fly);
				states.add(new Node(s[0], curNode, "fly", curNode.depth + 1, path+curNode.cost, heur));
			}
		}
		///// RIGHT
		right.moveRight();
		if (right.Neo.x >= 0 && right.Neo.x < right.N && right.Neo.y >= 0 && right.Neo.y < right.M) {
			if (checkAgentNotExist(right.Neo, right.Agents, right.NewAgents)) {
				if (checkHostageNotDie(right.Neo, right.Hostages)) {
					// System.out.println("right");
					right.applyDamageToHostages();
					check = right.encodeString();
					String[] s = check.split("!");
					if (!h.containsKey(s[1])) {
						h.put(s[1], true);
						int path = calcPath(grid, right, "right",curNode.depth);
						int heur = calculateHeur(right);
						states.add(new Node(s[0], curNode, "right", curNode.depth + 1, path+curNode.cost, heur));
					}
				}
			}
		}
		//// LEFT
		left.moveLeft();
		if (left.Neo.x >= 0 && left.Neo.x < left.N && left.Neo.y >= 0 && left.Neo.y < left.M) {
			if (checkAgentNotExist(left.Neo, left.Agents, left.NewAgents)) {
				if (checkHostageNotDie(left.Neo, left.Hostages)) {
					left.applyDamageToHostages();
					check = left.encodeString();
					String[] s = check.split("!");
					if (!h.containsKey(s[1])) {
						h.put(s[1], true);
						int path = calcPath(grid, left, "left",curNode.depth);
						int heur = calculateHeur(left);
						states.add(new Node(s[0], curNode, "left", curNode.depth + 1, path+curNode.cost, heur));
					}
				}
			}
		}
		//// UP
		up.moveUp();
		if (up.Neo.x >= 0 && up.Neo.x < up.N) {
			if (checkAgentNotExist(up.Neo, up.Agents, up.NewAgents)) {
				if (checkHostageNotDie(up.Neo, up.Hostages)) {
					up.applyDamageToHostages();
					check = up.encodeString();
					String[] s = check.split("!");
					if (!h.containsKey(s[1])) {
						h.put(s[1], true);
						int path = calcPath(grid, up, "up",curNode.depth);
						int heur = calculateHeur(up);
						states.add(new Node(s[0], curNode, "up", curNode.depth + 1, path+curNode.cost, heur));
					}
				}
			}
		}
		//// DOWN
		down.moveDown();
		if (down.Neo.x >= 0 && down.Neo.x < down.N) {
			if (checkAgentNotExist(down.Neo, down.Agents, down.NewAgents)) {
				if (checkHostageNotDie(down.Neo, down.Hostages)) {
					down.applyDamageToHostages();
					check = down.encodeString();
					String[] s = check.split("!");
					if (!h.containsKey(s[1])) {
						h.put(s[1], true);
						int path = calcPath(grid, down, "down",curNode.depth);
						int heur = calculateHeur(down);
						states.add(new Node(s[0], curNode, "down", curNode.depth + 1, path+curNode.cost, heur));
					}
				}
			}
		}
		///// Kill
		if (kill.NeoDamage < 80) {
			if (checkHostagePresentAndNotDie(kill.Neo, kill.Hostages)) {
				boolean killed = kill.killAgents();
				if (killed) {
					kill.applyDamageToHostages();
					check = kill.encodeString();
					String[] s = check.split("!");
					if (!h.containsKey(s[1])) {
						h.put(s[1], true);
						int path = calcPath(grid, kill, "kill",curNode.depth);
						int heur = calculateHeur(kill);
						states.add(new Node(s[0], curNode, "kill", curNode.depth + 1, path+curNode.cost, heur));
					}
				}
			}
		}
		return states;
	}

	public static int checkPadPresent(Loc l, ArrayList<Pad> pads) {
		for (int i = 0; i < pads.size(); i++) {
			Pad cur = pads.get(i);
			if (cur.StartLoc.same(l)) {
				return i;
			}
		}
		return -1;
	}

	public static int checkPillPresent(Loc l, ArrayList<Loc> pills) {
		for (int i = 0; i < pills.size(); i++) {
			Loc cur = pills.get(i);
			if (cur.same(l)) {
				return i;
			}
		}
		return -1;
	}

	public static boolean checkHostagePresentAndNotDie(Loc l, ArrayList<Hos> hostages) {
		for (int i = 0; i < hostages.size(); i++) {
			Hos cur = hostages.get(i);
			if (cur.loc.same(l)) {
				if (cur.damage >= 98) {
					return false;
				}
			}
		}
		return true;
	}

	public static int checkHostagePresent(Loc l, ArrayList<Hos> hostages) {
		for (int i = 0; i < hostages.size(); i++) {
			Hos cur = hostages.get(i);
			if (cur.loc.same(l)) {
				return i;
			}
		}
		return -1;
	}

	public static boolean checkHostageNotDie(Loc l, ArrayList<Hos> hostages) {
		for (int i = 0; i < hostages.size(); i++) {
			Hos cur = hostages.get(i);
			if (cur.loc.same(l)) {
				if (cur.damage >= 98) {
					return false;
				}
				return true;
			}
		}
		return true;
	}

	public static boolean checkAgentNotExist(Loc l, ArrayList<Loc> agents, ArrayList<Loc> newAgents) {
		for (int i = 0; i < agents.size(); i++) {
			Loc cur = agents.get(i);
			if (cur.same(l)) {
				return false;
			}
		}
		for (int i = 0; i < newAgents.size(); i++) {
			Loc cur = newAgents.get(i);
			if (cur.same(l)) {
				return false;
			}
		}
		return true;
	}

	public static String genGrid() {
		Random rand = new Random(); //instance of random class
	        //generate random values from 0-24 
		// Hostages 5 -10 Random damage 1-99
		// C <= 4
		// No. Pills = No. hostages
		// Neo kill agent damage increase = 20
		// Neo Takes pill damage decrease = 20 (Including all current hostages
		
		int[] temp = new int[2];
		M = rand.nextInt(11)+5;
		N = rand.nextInt(11)+5; 
		gridBool = new boolean[M][N];
		gridString = new String[M][N];
		int C = rand.nextInt(4)+1; //maximum no of hostages Neo can carry
		int NeoX = rand.nextInt(M);
		int NeoY = rand.nextInt(N);
		gridBool[NeoX][NeoY] = true;//NeoX and NeoY represent the x and y starting positions of Neo.
		gridString[NeoX][NeoY] = "Neo";
		temp = genInGrid("TB");
		int TelephoneX = temp[0];
		int TelephoneY = temp[1]; //TelephoneX and TelephoneY represent the x and y positions of the tele-
								   //phone booth.
		
		int noAgents = 0;
		int noPads = 0;
		int noHostages = rand.nextInt(8)+3;
		
		int[] arrHostages = new int[noHostages*3];
		int[] arrPills = new int[noHostages*2];
		for (int i=0;i<noHostages;i++) {
			temp = genInGrid("Hostage:" + ((int)(i+1)));
			arrHostages[i*3] = temp[0]; 
			arrHostages[i*3 +1] = temp[1];
			arrHostages[i*3 +2] = rand.nextInt(99)+1;
		}
		
		for (int i=0;i<noHostages;i++) {
			temp = genInGrid("Pill:" + ((int)(i+1)));
			arrPills[i*2] = temp[0]; 
			arrPills[i*2 +1] = temp[1];
		}
		
		int leftCells = M * N - (1+1+noHostages+noHostages);
		
		if (leftCells > (noHostages/C)+2) {
			noAgents = noHostages/C;
			noPads = rand.nextInt((leftCells-(noHostages/C))/2)+1;
		}
		else if(leftCells > 4) {
			noAgents = leftCells - 3;
			noPads = 1;
		}
		else {
			noAgents = leftCells - 2;
			noPads = 1;
		}
		
		int[] arrAgents = new int[noAgents*2];
		int[] arrStartPads = new int[noPads*2];
		int[] arrFinishPads = new int[noPads*2];
		
		for (int i=0;i<noAgents;i++) {
			temp = genInGrid("Agent:" + ((int)(i+1)));
			arrAgents[i*2] = temp[0]; 
			arrAgents[i*2 +1] = temp[1];
		}
		
		for (int i=0;i<noPads;i++) {
			temp = genInGrid("Start Pad:" + ((int)(i+1)));
			arrStartPads[i*2] = temp[0]; 
			arrStartPads[i*2 +1] = temp[1];
		}
		
		for (int i=0;i<noPads;i++) {
			temp = genInGrid("Finish Pad:" + ((int)(i+1)));
			arrFinishPads[i*2] = temp[0]; 
			arrFinishPads[i*2 +1] = temp[1];
		}
		
		 //AgentXi,AgentYi represent the x and y position of agent i where 1 ≤i ≤k
											  //and k is the total number of agents.
		     //PillXi,PillYi represent the x and y position of pill i where 1 ≤i ≤k and
											  //k is the total number of pills.
		
		 // StartPadXi,StartPadYi represent the x and y position of pad i where 1 ≤
														//	i ≤k and k is the total number of pads. Moreover FinishPadXi,FinishPadYi
														//	represent the x and y position of the target pad stated by StartPadXi and
														//	StartPadYi.
//		HostageX1,HostageY1,HostageDamage1, ...,
//		HostageXk,HostageYk,HostageDamagek;				// HostageXi,HostageYi,HostageDamagei represent the x and y position and
//														// current damage of hostage i where 1 ≤i ≤k and k is the total number of
														// hostages.
		
		// Insert M, N , C, NeoX, NeoY, TelephoneX, TelephoneY
		String grid=""+ M + ","+N+";"+C+";"+NeoX+","+NeoY+";"+TelephoneX+","+TelephoneY+";";
		
		//insertAgents
		for(int i =0;i<arrAgents.length-1;i++) {
			grid = grid + arrAgents[i] + ",";
		}
		grid =grid+arrAgents[arrAgents.length-1]+ ";";
		
		//insert Pills
		for(int i =0;i<arrPills.length-1;i++) {
			grid = grid + arrPills[i] + ",";
		}
		grid =grid+arrPills[arrPills.length-1]+ ";";
		
		//insert start and finishPads;
		for (int i = 0;i<noPads-1 ;i++) {
			grid = grid + arrStartPads[i*2] + "," +arrStartPads[i*2+1] + ","+ arrFinishPads[i*2] + "," +arrFinishPads[i*2+1]+",";
		}
		
		grid = grid + arrStartPads[arrStartPads.length-2] +"," +arrStartPads[arrStartPads.length-1] +","
		+ arrFinishPads[arrFinishPads.length-2] +","+arrFinishPads[arrFinishPads.length-1]+";";
		
		
		//insert Hostages
		for(int i =0;i<arrHostages.length-1;i++) {
			grid = grid + arrHostages[i] + ",";
		}
		grid =grid+arrHostages[arrHostages.length-1];
		
		
		return grid;
	}
	
	public static int[] genInGrid(String object) {
		int x;
		int y;
		Random rand = new Random();
		while (true) {
			x = rand.nextInt(M);
			y = rand.nextInt(N);
			if (!gridBool[x][y]) {
				break;
			}
		}
		gridBool[x][y]=true;
		gridString[x][y] = object;
		return new int[] {x,y} ;
	}
}

