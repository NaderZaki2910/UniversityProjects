package code;

public class Pad {
	public Loc StartLoc;
	public Loc FinishLoc;
	public boolean vis;

	public Pad(Loc start, Loc finish) {
		this.StartLoc = start;
		this.FinishLoc = finish;
		this.vis=false;
		
	}

	public String toString() {
		return "X: " + this.StartLoc.x + " Y: " + StartLoc.y + "   ---->>>> " + "X: " + this.FinishLoc.x + " Y: "
				+ FinishLoc.y;
	}

	public String format() {
		return this.StartLoc.format() + "," + this.FinishLoc.format();
	}
	public Pad copy() {
		return new Pad(StartLoc.copy(),FinishLoc.copy());
	}

}
