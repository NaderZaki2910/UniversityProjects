package code;

public class Loc {
	public int x;
	public int y;

	public Loc(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public boolean same(Loc l) {
		if (this.x == l.x && this.y == l.y) {
			return true;
		}
		return false;

	}
	
	public int dist(Loc l) {
		return Math.abs(this.x-l.x)+Math.abs(this.y-l.y);
	}
	public String toString() {
		return "X: " + this.x + " Y: " + this.y;
	}

	public String format() {
		return this.x + "," + this.y;
	}
	
	public Loc copy() {
		return new Loc(this.x,this.y);
	}
	
	public boolean near(Loc l) {
		if(l.x==this.x) {
			if(l.y==this.y+1 || l.y==this.y-1) {
				return true;
			}
		}
		if(l.y==this.y) {
			if(l.x==this.x+1 || l.x==this.x-1) {
				return true;
			}
		}
		
		return false;
	}

}
