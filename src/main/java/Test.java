
import java.awt.Polygon;
import java.util.Hashtable;
import java.util.Random;

public class Test {

	public static void main(String[] args) throws DBAppException {
		int[] rand = new int[1000];

		String[] names = { "ahmed", "diab", "mark", "oba", "mar", "hesh", "hesh", "55", "56" };
		String strTableName = "Student";
		DBApp dbApp = new DBApp();

		Hashtable<String, String> htblColNameType = new Hashtable();
		htblColNameType.put("id", "java.lang.Integer");
		htblColNameType.put("name", "java.lang.String");
		htblColNameType.put("gpa", "java.lang.Double");
		htblColNameType.put("bool", "java.lang.Boolean");
		htblColNameType.put("pol", "java.awt.polygon");

		// dbApp.createTable(strTableName, "id", htblColNameType);
		// dbApp.createBTreeIndex(strTableName, "id");
		// dbApp.createRTreeIndex(strTableName, "pol");
		// dbApp.createBTreeIndex(strTableName,"name");
		// dbApp.printTable(strTableName);

		Random r = new Random();

		Hashtable<String, Object> htblColNameValue = new Hashtable();

		for (int i = 0; i < 0; i++) {
			htblColNameValue = new Hashtable();
			/*
			 * htblColNameValue.put("id", new Integer(845));
			 * dbApp.deleteFromTable(strTableName, htblColNameValue);
			 */
			int[] px;
			int[] py;
			int ran = r.nextInt(1000);
			htblColNameValue.put("id", new Integer(ran));
			// htblColNameValue.put("id", 177);
			try {
				int len = r.nextInt(4);
				px = new int[len];
				py = new int[len];
				for (int yy = 0; yy < len; yy++) {
					px[yy] = r.nextInt(20);
					py[yy] = r.nextInt(20);
				}
				htblColNameValue.put("pol", new Polygon(px, py, len));

				int p = r.nextInt(2);
				if (p == 0) {
					htblColNameValue.put("bool", new Boolean(false));
				} else {
					htblColNameValue.put("bool", new Boolean(true));
				}
				htblColNameValue.put("gpa", new Double(r.nextDouble()));
				int x = r.nextInt(names.length);
				htblColNameValue.put("name", new String(names[x]));
				System.out.println("INPUTTING :------- " + htblColNameValue);

				dbApp.insertIntoTable(strTableName, htblColNameValue);

				System.out.println("INPUTTING :------- " + htblColNameValue);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		htblColNameValue = new Hashtable();
		htblColNameValue.clear();
		/*
		 * int[] xx = { 3 }; int[] yy = { 6 }; htblColNameValue.put("pol", new
		 * Polygon(xx, yy, 1));
		 */
		htblColNameValue.put("id", 814);

		dbApp.deleteFromTable(strTableName, htblColNameValue);
		// dbApp.updateTable(strTableName, "(7,14),(7,0)", htblColNameValue);
		dbApp.printTable(strTableName);
		// System.out.println(DBApp.getIndices(strTableName));
		System.out.println("INPUTTING :------- " + htblColNameValue);

	}

}