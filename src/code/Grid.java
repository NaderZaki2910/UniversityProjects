package code;

import java.util.ArrayList;

public class Grid {

	public int M;
	public int N;
	public int C;
	public int NeoDamage;
	public int deaths;
	public int kills;
	public Loc Neo;
	public Loc TB;
	public ArrayList<Loc> Agents;
	public ArrayList<Loc> Pills;
	public ArrayList<Pad> Pads;
	public ArrayList<Hos> Hostages;
	public ArrayList<Hos> Carry;
	public ArrayList<Loc> NewAgents;
	public int agentsKilled;
	public int removed;
	public int totPills;
	public boolean goodKill;
	int killed;
	int died;

	public Grid(int m, int n, int c, int dam, int death, int kill, Loc neo, Loc tb, ArrayList<Loc> agen,
			ArrayList<Loc> pil, ArrayList<Pad> pad, ArrayList<Hos> hos, ArrayList<Hos> car, ArrayList<Loc> newA,int rem,int p) {
		this.M = m;
		this.N = n;
		this.C = c;
		this.NeoDamage = dam;
		this.deaths = death;
		this.kills = kill;
		this.Neo = neo;
		this.TB = tb;
		this.Agents = agen;
		this.Pills = pil;
		this.Pads = pad;
		this.Hostages = hos;
		this.Carry = car;
		this.NewAgents = newA;
		this.removed=rem;
		this.totPills=p;
		goodKill=false;
		killed=0;
		died=0;

	}

	public Grid(String state) {
		killed=0;
		died=0;
		goodKill=false;
		Agents = new ArrayList<Loc>();
		Pills = new ArrayList<Loc>();
		Pads = new ArrayList<Pad>();
		Hostages = new ArrayList<Hos>();
		Carry = new ArrayList<Hos>();
		NewAgents = new ArrayList<Loc>();

		String[] x = state.split(";");

		// Grid Size
		String[] size = x[0].split(",");
		M = Integer.parseInt(size[0]);
		N = Integer.parseInt(size[1]);

		// C
		C = Integer.parseInt(x[1]);

		// Neo Location
		String[] Neo_List = x[2].split(",");
		Neo = new Loc(Integer.parseInt(Neo_List[0]), Integer.parseInt(Neo_List[1]));

		// Neo Damage
		NeoDamage = Integer.parseInt(x[10]);
		// Deaths
		deaths = Integer.parseInt(x[11]);
		// Kills
		kills = Integer.parseInt(x[12]);
		// agents killed
		agentsKilled = Integer.parseInt(x[13]);
		// removed hostages
		removed=Integer.parseInt(x[14]);		
		// tot pills
		totPills=Integer.parseInt(x[15]);
		// Telephone Booth
		String[] TB_List = x[3].split(",");
		TB = new Loc(Integer.parseInt(TB_List[0]), Integer.parseInt(TB_List[1]));
		/////// Agents
		String[] Agents_List = x[4].split(",");
		if (!(Agents_List[0].equals("_"))) {
			for (int i = 0; i < Agents_List.length - 1; i = i + 2) {
				Agents.add(new Loc(Integer.parseInt(Agents_List[i]), Integer.parseInt(Agents_List[i + 1])));
			}
		}
		/////// Pills
		String[] Pills_List = x[5].split(",");
		if (!(Pills_List[0].equals("_"))) {
			for (int i = 0; i < Pills_List.length - 1; i = i + 2) {
				Pills.add(new Loc(Integer.parseInt(Pills_List[i]), Integer.parseInt(Pills_List[i + 1])));
			}
		}
		/////// Pads
		String[] Pad_List = x[6].split(",");
		if (!(Pad_List[0].equals("_"))) {
			for (int i = 0; i < Pad_List.length - 1; i = i + 4) {
				Loc start = new Loc(Integer.parseInt(Pad_List[i]), Integer.parseInt(Pad_List[i + 1]));
				Loc finish = new Loc(Integer.parseInt(Pad_List[i + 2]), Integer.parseInt(Pad_List[i + 3]));
				Pads.add(new Pad(start, finish));
			}
		}
		/////// Hostages
		String[] Hostages_List = x[7].split(",");
		if (!(Hostages_List[0].equals("_"))) {
			for (int i = 0; i < Hostages_List.length; i = i + 3) {
				Loc l = new Loc(Integer.parseInt(Hostages_List[i]), Integer.parseInt(Hostages_List[i + 1]));
				Hostages.add(new Hos(Integer.parseInt(Hostages_List[i + 2]), l));
			}
		}
		////// Carry
		String[] Carry_List = x[8].split(",");
		if (!(Carry_List[0].equals("_"))) {
			for (int i = 0; i < Carry_List.length; i = i + 3) {
				Loc l = new Loc(Integer.parseInt(Carry_List[i]), Integer.parseInt(Carry_List[i + 1]));
				Carry.add(new Hos(Integer.parseInt(Carry_List[i + 2]), l));
			}
		}
		/// New Agents
		String[] NewAgents_List = x[9].split(",");
		if (!(NewAgents_List[0].equals("_"))) {
			for (int i = 0; i < NewAgents_List.length - 1; i = i + 2) {
				NewAgents.add(new Loc(Integer.parseInt(NewAgents_List[i]), Integer.parseInt(NewAgents_List[i + 1])));
			}
		}
	}

	public void moveLeft() {
		Neo.y--;
	}

	public void moveRight() {
		Neo.y++;
	}

	public void moveUp() {
		Neo.x--;
	}

	public void moveDown() {
		Neo.x++;
	}

	public void applyDamageToHostages() {
		for (int i = Hostages.size() - 1; i >= 0; i--) {
			Hos cur = Hostages.get(i);
			if (cur.damage >= 98) {
				Hostages.remove(i);
				deaths++;
				died++;
				NewAgents.add(cur.loc);
			} else {
				cur.damage += 2;
			}
		}

		////// HANDLE CARRY
		for (int i = 0; i < Carry.size(); i++) {
			Hos cur = Carry.get(i);
			if (cur.damage != 100) {
				if (cur.damage >= 98) {
					cur.damage = 100;
					died++;
					deaths++;
				} else {
					cur.damage += 2;
				}
			}
		}

	}

	public boolean checkGoal() {
		if (NewAgents.size() != 0) {
			return false;
		}
		if (Hostages.size() != 0) {
			return false;
		}
		if (Carry.size() != 0) {
			return false;
		}
		if (!Neo.same(TB)) {
			return false;
		}
		return true;
	}

	public void takePill(int idx) {
		totPills++;
		Pills.remove(idx);
		// Hostages
		for (int i = 0; i < Hostages.size(); i++) {
			Hos cur = Hostages.get(i);
			if (cur.damage < 100) {
				cur.damage -= 20;
				if (cur.damage < 0) {
					cur.damage = 0;
				}
			}
		}
		// Carried
		for (int i = 0; i < Carry.size(); i++) {
			Hos cur = Carry.get(i);
			if (cur.damage < 100) {
				cur.damage -= 20;
				if (cur.damage < 0) {
					cur.damage = 0;
				}
			}
		}
		// Neo
		NeoDamage -= 20;
		if (NeoDamage < 0) {
			NeoDamage = 0;
		}
	}

	public void fly(int idx) {
		Pad cur = Pads.get(idx);
		Neo.x = cur.FinishLoc.x;
		Neo.y = cur.FinishLoc.y;
	}

	public void carryHostage(int idx) {
		Hos cur = Hostages.remove(idx);
		Carry.add(cur);
	}

	public void dropHostages() {
		removed+=Carry.size();
		Carry.clear();
	}

	public boolean killAgents() {
		boolean flag = false;
		//// Agents
		for (int i = Agents.size() - 1; i >= 0; i--) {
			Loc cur = Agents.get(i);
			// Check up or down
			if (cur.x == Neo.x) {
				if (cur.y == Neo.y - 1 || cur.y == Neo.y + 1) {
					Agents.remove(i);
					kills++;
					killed++;
					agentsKilled++;
					flag = true;
					continue;
				}
			}
			// Check right or left
			if (cur.y == Neo.y) {
				if (cur.x == Neo.x - 1 || cur.x == Neo.x + 1) {
					Agents.remove(i);
					kills++;
					killed++;
					agentsKilled++;
					flag = true;
					continue;
				}
			}
		}
		//// New Agents
		for (int i = NewAgents.size() - 1; i >= 0; i--) {
			Loc cur = NewAgents.get(i);
			// Check up or down
			if (cur.x == Neo.x) {
				if (cur.y == Neo.y - 1 || cur.y == Neo.y + 1) {
					NewAgents.remove(i);
					kills++;
					killed++;
					removed++;
					goodKill=true;
					flag = true;
					continue;
				}
			}
			// Check right or left
			if (cur.y == Neo.y) {
				if (cur.x == Neo.x - 1 || cur.x == Neo.x + 1) {
					NewAgents.remove(i);
					kills++;
					killed++;
					removed++;
					goodKill=true;
					flag = true;
					continue;
				}
			}
		}
		if (flag) {
			NeoDamage += 20;
		}
		return flag;
	}

	public void print() {
		System.out.println("Grid Size: " + M + " x " + N);
		System.out.println("C: " + C);
		System.out.println("Neo Location:");
		System.out.println(Neo);
		System.out.println("Booth Location:");
		System.out.println(TB);
		///// Agents
		System.out.println("Agents: ");
		if (Agents.size() == 0) {
			System.out.println("None");
		} else {
			for (int i = 0; i < Agents.size(); i++) {
				System.out.println(Agents.get(i));
			}
		}

		////// Pills
		System.out.println("Pills: ");
		if (Pills.size() == 0) {
			System.out.println("None");
		} else {
			for (int i = 0; i < Pills.size(); i++) {
				System.out.println(Pills.get(i));
			}
		}

		///// Pads
		System.out.println("Pads:");
		if (Pads.size() == 0) {
			System.out.println("None");
		} else {
			for (int i = 0; i < Pads.size(); i++) {
				System.out.println(Pads.get(i));
			}
		}
		//// Hostages
		System.out.println("Hostages:");
		if (Hostages.size() == 0) {
			System.out.println("None");
		} else {
			for (int i = 0; i < Hostages.size(); i++) {
				System.out.println(Hostages.get(i));
			}
		}

		// Carry
		System.out.println("Carry:");
		if (Carry.size() == 0) {
			System.out.println("None");
		} else {
			for (int i = 0; i < Carry.size(); i++) {
				System.out.println(Carry.get(i));
			}

		}
		// New Agents
		System.out.println("New Agents:");
		if (NewAgents.size() == 0) {
			System.out.println("None");
		} else {
			for (int i = 0; i < NewAgents.size(); i++) {
				System.out.println(NewAgents.get(i));
			}
		}
	}

	public String encodeString() {
		String withDamage = "";
		String withoutDamage = "";
		// M, N, C ,Neo , TB
		withDamage += M + "," + N + ";" + C + ";" + Neo.format() + ";" + TB.format() + ";";
		withoutDamage += M + "," + N + ";" + C + ";" + Neo.format() + ";" + TB.format() + ";";
		//// Agents
		if (Agents.size() == 0) {
			withDamage += "_;";
			withoutDamage += "_;";
		} else {
			for (int i = 0; i < Agents.size() - 1; i++) {
				withDamage += Agents.get(i).format() + ",";
				withoutDamage += Agents.get(i).format() + ",";
			}
			withDamage += Agents.get(Agents.size() - 1).format() + ";";
			withoutDamage += Agents.get(Agents.size() - 1).format() + ";";
		}

		//// Pills
		if (Pills.size() == 0) {
			withDamage += "_;";
			withoutDamage += "_;";
		} else {
			for (int i = 0; i < Pills.size() - 1; i++) {
				withDamage += Pills.get(i).format() + ",";
				withoutDamage += Pills.get(i).format() + ",";
			}
			withDamage += Pills.get(Pills.size() - 1).format() + ";";
			withoutDamage += Pills.get(Pills.size() - 1).format() + ";";
		}

		//// Pads
		if (Pads.size() == 0) {
			withDamage += "_;";
			withoutDamage += "_;";
		} else {
			for (int i = 0; i < Pads.size() - 1; i++) {
				withDamage += Pads.get(i).format() + ",";
				withoutDamage += Pads.get(i).format() + ",";
			}
			withDamage += Pads.get(Pads.size() - 1).format() + ";";
			withoutDamage += Pads.get(Pads.size() - 1).format() + ";";
		}

		//// Hostages
		if (Hostages.size() == 0) {
			withDamage += "_;";
			withoutDamage += "_;";
		} else {
			for (int i = 0; i < Hostages.size() - 1; i++) {
				withDamage += Hostages.get(i).format() + ",";
				withoutDamage += Hostages.get(i).formatWithoutDamage() + ",";
			}
			withDamage += Hostages.get(Hostages.size() - 1).format() + ";";
			withoutDamage += Hostages.get(Hostages.size() - 1).formatWithoutDamage() + ";";

		}

		//// Carry
		if (Carry.size() == 0) {
			withDamage += "_;";
			withoutDamage += "_;";
		} else {
			for (int i = 0; i < Carry.size() - 1; i++) {
				withDamage += Carry.get(i).format() + ",";
				withoutDamage += Carry.get(i).formatWithoutDamage() + ",";
			}
			withDamage += Carry.get(Carry.size() - 1).format() + ";";
			withoutDamage += Carry.get(Carry.size() - 1).formatWithoutDamage() + ";";
		}

		//// New Agents
		if (NewAgents.size() == 0) {
			withDamage += "_;";
			withoutDamage += "_;";
		} else {
			for (int i = 0; i < NewAgents.size() - 1; i++) {
				withDamage += NewAgents.get(i).format() + ",";
				withoutDamage += NewAgents.get(i).format() + ",";
			}
			withDamage += NewAgents.get(NewAgents.size() - 1).format() + ";";
			withoutDamage += NewAgents.get(NewAgents.size() - 1).format() + ";";
		}
		withDamage += NeoDamage + ";";
		withDamage += deaths + ";";
		withDamage += kills + ";";
		withDamage += agentsKilled+";";
		withDamage +=removed+";";
		withDamage += totPills;

		/// TEST REMOVING NEO DAMAGE //////
		withoutDamage += NeoDamage + ";";
		withoutDamage += deaths + ";";
		withoutDamage += kills + ";";
		withoutDamage += agentsKilled+";";
		withoutDamage += removed+";";
		withoutDamage += totPills;
		

		return withDamage + "!" + withoutDamage;
	}

	public Grid copy() {
		Loc neo = Neo.copy();
		Loc tb = TB.copy();
		/// AGENTS
		ArrayList<Loc> agents = new ArrayList<Loc>();
		for (int i = 0; i < Agents.size(); i++) {
			agents.add(Agents.get(i).copy());
		}
		// Pills
		ArrayList<Loc> pills = new ArrayList<Loc>();
		for (int i = 0; i < Pills.size(); i++) {
			pills.add(Pills.get(i).copy());
		}
		// Pads
		ArrayList<Pad> pads = new ArrayList<Pad>();
		for (int i = 0; i < Pads.size(); i++) {
			pads.add(Pads.get(i).copy());
		}
		// Hostages
		ArrayList<Hos> hostages = new ArrayList<Hos>();
		for (int i = 0; i < Hostages.size(); i++) {
			hostages.add(Hostages.get(i).copy());
		}
		// Carry
		ArrayList<Hos> carry = new ArrayList<Hos>();
		for (int i = 0; i < Carry.size(); i++) {
			carry.add(Carry.get(i).copy());
		}
		// NewAgents
		ArrayList<Loc> newAgents = new ArrayList<Loc>();
		for (int i = 0; i < NewAgents.size(); i++) {
			newAgents.add(NewAgents.get(i).copy());
		}

		return new Grid(this.M, this.N, this.C, this.NeoDamage, this.deaths, this.kills, this.Neo.copy(),
				this.TB.copy(), agents, pills, pads, hostages, carry, newAgents, this.removed,this.totPills);

	}
	
	public void executeMove(String move) {
		//System.out.println(move);
		switch (move) {
		case "left":
			this.moveLeft();
			this.applyDamageToHostages();
			break;
		case "right":
			this.moveRight();
			this.applyDamageToHostages();
			break;
		case "up":
			this.moveUp();
			this.applyDamageToHostages();
			break;
		case "down":
			this.moveDown();
			this.applyDamageToHostages();
			break;
		case "kill":
			this.kill();
			this.applyDamageToHostages();
			break;
		case "fly":
			this.teleport();
			this.applyDamageToHostages();
			break;
		case "carry":
			this.carry();
			this.applyDamageToHostages();
			break;
		case "takePill":
			this.take();
			//this.applyDamageToHostages();
			break;
		case "drop":
			this.drop();
			this.applyDamageToHostages();
			break;
		default:
			break;
		}
	}
	public void drop() {
		Carry.clear();
	}
	
	public void kill() {
		for(int i=Agents.size()-1;i>=0;i--) {
			Loc cur=Agents.get(i);
			if(cur.near(Neo)) {
				Agents.remove(i);
				kills++;
			}
		}
		for(int i=NewAgents.size()-1;i>=0;i--) {
			Loc cur=NewAgents.get(i);
			if(cur.near(Neo)) {
				NewAgents.remove(i);
				kills++;
			}
		}
		NeoDamage+=20;
		
	}
	public void teleport() {
		for(int i=0;i<Pads.size();i++) {
			Pad cur=Pads.get(i);
			if(cur.StartLoc.same(Neo)) {
				Neo=cur.FinishLoc.copy();
				return;
			}
		}
	}
	public void carry() {
		for(int i=0;i<Hostages.size();i++) {
			Hos cur=Hostages.get(i);
			if(cur.loc.same(Neo)) {
				Carry.add(cur);
				Hostages.remove(i);
				return;
			}
		}
	}
	
	public void take() {
		for(int i=0;i<Pills.size();i++) {
			Loc cur=Pills.get(i);
			if(cur.same(Neo)) {
				NeoDamage-=20;
				if(NeoDamage<0) {
					NeoDamage=0;
				}
				Pills.remove(i);
				break;
			}
		}
		for(int i=0;i<Hostages.size();i++) {
			Hos cur=Hostages.get(i);
			if(cur.damage==100) {
				continue;
			}
			cur.damage-=20;
			if(cur.damage<0) {
				cur.damage=0;
			}
		}
		for(int i=0;i<Carry.size();i++) {
			Hos cur=Carry.get(i);
			if(cur.damage==100) {
				continue;
			}
			cur.damage-=20;
			if(cur.damage<0){
				cur.damage=0;
			}
		}
	}
}