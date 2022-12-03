package code;

public class Hos {

	public int damage;
	public Loc loc;

	public Hos(int d, Loc l) {
		damage = d;
		loc = l;
	}

	public String toString() {
		return "X: " + this.loc.x + " Y: " + this.loc.y + "   Damage: " + this.damage;
	}

	public String format() {
		return loc.format() + "," + damage;
	}
	public String formatWithoutDamage() {
		return loc.format();
	}
	public Hos copy() {
		return new Hos(this.damage,this.loc.copy());
	}
}
