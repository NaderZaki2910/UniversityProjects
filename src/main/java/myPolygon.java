
import java.awt.Dimension;
import java.awt.Polygon;

public class myPolygon extends Polygon implements Comparable<myPolygon> {
	public myPolygon(int[] x, int[] y, int n) {
		super(x, y, n);
	}

	@Override
	public int compareTo(myPolygon o) {
		Dimension d1 = this.getBoundingBox().getSize();
		int s1 = d1.height * d1.width;
		Dimension d2 = o.getBoundingBox().getSize();
		int s2 = d2.height * d2.width;
		if (s1 > s2) {
			return 1;
		}
		if (s2 > s1) {
			return -1;
		}
		return 0;
	}

	public boolean equalsto(myPolygon o) {
		// equal in terms of points
		if (o.npoints != this.npoints) {
			return false;
		}

		for (int i = 0; i < this.xpoints.length; i++) {
			if (this.xpoints[i] != o.xpoints[i]) {
				return false;
			}
			if (this.ypoints[i] != o.ypoints[i]) {
				return false;
			}
		}
		return true;

	}

	public String toString() {
		String s = "";
		for (int i = 0; i < this.npoints; i++) {
			int x = this.xpoints[i];
			int y = this.ypoints[i];
			s += "(" + x + "," + y + ")";

			if (i != this.npoints - 1) {
				s += ",";
			}

		}
		Dimension d2 = this.getBoundingBox().getSize();
		int s2 = d2.height * d2.width;
		s += "--" + s2 + "--";

		return s;

	}
}
