
import java.awt.*;
import java.io.*;
import java.util.*;

public class DBApp {

	static int maxRow;
	static int nodeSize;

	public static void main(String[] args) {
		DBApp db = new DBApp();

	}

	/**
	 *
	 * takes an array of sql terms and an array of operators and after executing the sql terms
	 * it uses the operators on them and returns an iterator with the final result
	 *
	 * @param arrSQLTerms
	 * @param strarrOperators
	 * @return iterator
	 * @throws DBAppException
	 */

	public Iterator selectFromTable(SQLTerm[] arrSQLTerms, String[] strarrOperators) throws DBAppException {
		try{
			LinkedList[] results = new LinkedList[arrSQLTerms.length];
			for (int i = 0; i < arrSQLTerms.length; i++) {
				results[i] = search(arrSQLTerms[i]);
			}
			for (int i = 0; i < strarrOperators.length; i++) {
				LinkedList<Hashtable> temp = new LinkedList<>();
				if (strarrOperators[i].toLowerCase().equals("and")) {
					if (results[i].size() < results[i + 1].size()) {
						for (int j = 0; j < results[i].size(); i++) {
							if (results[i + 1].contains(results[i].get(j))) {
								temp.add((Hashtable) results[i].get(j));
							}
						}
						results[i + 1] = temp;
					} else {
						for (int j = 0; j < results[i + 1].size(); j++) {
							if (results[i].contains(results[i + 1].get(j))) {
								temp.add((Hashtable) results[i + 1].get(j));
							}
						}
						results[i + 1] = temp;
					}
				} else {
					if (strarrOperators[i].toLowerCase().equals("or")) {
						for (int j = 0; j < results[i].size(); i++) {
							if (!results[i + 1].contains(results[i].get(j))) {
								results[i + 1].add(results[i].get(j));
							}
						}
					} else {
						if (strarrOperators[i].toLowerCase().equals("xor")) {
							for (int j = 0; j < results[i].size(); i++) {
								if (!results[i + 1].contains(results[i].get(j))) {
									temp.add((Hashtable) results[i].get(j));
								}
							}
							for (int j = 0; j < results[i].size(); i++) {
								if (results[i].contains(results[i + 1].get(j))) {
									temp.add((Hashtable) results[i + 1].get(j));
								}
							}
							results[i + 1] = temp;
						}
					}
				}
			}
			return results[results.length - 1].iterator();
		}
		catch (Exception e){
			throw new  DBAppException("Exception happened in select from table");
		}
	}

	/**
	 *
	 * searches for the result of an sql term and returns a linked list containing the result
	 *
	 * @param sql
	 * @return linkedlist
	 * @throws DBAppException
	 */

	public static LinkedList search(SQLTerm sql) throws DBAppException {
		try{
			Table t = readTable(sql._strTableName);
			if (getIndices(t.name).contains(sql._strColumnName)) {
				if (t.colNameType.get(sql._strColumnName).toLowerCase().equals("java.awt.polygon")) {
					RTree b = (RTree) readBPTree(t.name + "_" + sql._strColumnName);
					Dimension d1 = ((myPolygon) sql._objValue).getBoundingBox().getSize();
					int area = d1.height * d1.width;
					return b.search(area, (myPolygon) sql._objValue, sql._strOperator);
				} else {
					BTree b = (BTree) readBPTree(t.name + "_" + sql._strColumnName);
					return b.search((Comparable) sql._objValue, sql._strOperator);
				}
			} else {
				LinkedList list = new LinkedList<>();
				String tblName = sql._strTableName;
				String colName = sql._strColumnName;
				String op = sql._strOperator;
				Object val = sql._objValue;

				t = readTable(tblName);

				String curP;
				Vector curV;
				Hashtable<String, Object> curE;
				for (int i = 0; i < t.Pages.size(); i++) {
					curP = t.Pages.get(i);
					curV = loadPage(curP);
					for (int j = 0; j < curV.size(); j++) {
						curE = (Hashtable<String, Object>) curV.get(j);
						if (compareOper(t.colNameType.get(colName), curE.get(colName), val, op)) {
							list.add(curE);
						}

					}

				}

				return list;
			}
		}
		catch (Exception e){
			throw new DBAppException("Exception happened at search method");
		}
	}

	/**
	 *
	 * checks type then executes the operation
	 *
	 *
	 * @param type
	 * @param o1
	 * @param o2
	 * @param op
	 * @return
	 * @throws DBAppException
	 */

	public static boolean compareOper(String type, Object o1, Object o2, String op) throws DBAppException {
		int x = compare(type, o1, o2);
		if (type.toLowerCase().equals("java.awt.polygon")) {
			if (op.equals("=")) {
				myPolygon p1 = (myPolygon) o1;
				myPolygon p2 = (myPolygon) o2;
				return p1.equalsto(p1);
			}
			if (op.equals("!=")) {
				myPolygon p1 = (myPolygon) o1;
				myPolygon p2 = (myPolygon) o2;
				return !p1.equalsto(p1);
			}
		}
		switch (op) {
		case ">":
			if (x > 0) {
				return true;
			} else {
				return false;
			}

		case ">=":
			if (x >= 0) {
				return true;
			} else {
				return false;
			}
		case "<":
			if (x < 0) {
				return true;
			} else {
				return false;
			}

		case "<=":
			if (x <= 0) {
				return true;
			} else {
				return false;
			}
		case "!=":
			if (x != 0) {
				return true;
			} else {
				return false;
			}
		case "=":
			if (x == 0) {
				return true;

			} else {
				return false;
			}
		}
		throw new DBAppException("NO OPERATOR MATCHED");
	}

	/**
	 *
	 * creates a b+ tree if the column is a suitable one
	 *
	 * @param strTableName
	 * @param strColName
	 * @throws DBAppException
	 */

	public void createBTreeIndex(String strTableName, String strColName) throws DBAppException {
		try{
			Table t = readTable(strTableName);
			boolean flag = true;
			if (getIndices(strTableName).contains(strColName))
				flag = false;
			String type = t.colNameType.get(strColName);
			BTree bTree = null;
			if (type == null || type.toLowerCase().equals("java.awt.polygon"))
				throw new DBAppException("Invalid Data type for index");
			if (type.toLowerCase().equals("java.lang.integer") && flag)
				bTree = new BTree<Integer, Hashtable>(t, strColName, nodeSize);
			if (type.toLowerCase().equals("java.lang.string") && flag)
				bTree = new BTree<String, Hashtable>(t, strColName, nodeSize);
			if (type.toLowerCase().equals("java.lang.double") && flag)
				bTree = new BTree<Double, Hashtable>(t, strColName, nodeSize);
			if (type.toLowerCase().equals("java.lang.date") && flag)
				bTree = new BTree<Date, Hashtable>(t, strColName, nodeSize);
			if (type.toLowerCase().equals("java.lang.boolean") && flag)
				bTree = new BTree<Boolean, Hashtable>(t, strColName, nodeSize);
			if (!flag)
				throw new DBAppException("Index already made");
			t = readTable(strTableName);
			setIndexTrue(strTableName, strColName);
			for (int i = 0; i < t.maxPage && bTree != null; i++) {
				Vector v = loadPage(t.Pages.get(i));
				for (int j = 0; j < v.size(); j++) {
					Hashtable h = (Hashtable) v.get(j);
					bTree.insert((Comparable) h.get(bTree.getColKey()), h);
				}
			}
		}
		catch (Exception e){
			throw new DBAppException(e.getMessage());
		}
	}

	/**
	 *
	 * reads the b+/r tree
	 *
	 * @param name
	 * @return tree(As an object)
	 */

	public static Object readBPTree(String name) {
		try {
			FileInputStream fileIn = new FileInputStream("data" + "\\" + name + ".class");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			Object b = (Object) in.readObject();

			System.out.println(name + "       Object Deserialized");
			in.close();
			fileIn.close();
			return b;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 *
	 * prints the values of each leaf node in all trees in a table
	 *
	 * @param name
	 * @throws DBAppException
	 */

	public void readIndices(String name) throws DBAppException {
		try{
			Table t = readTable(name);
			t.printAllNodes();
			t.writeTable();
		}
		catch (Exception e){
			throw new DBAppException("Exception happened while reading the indices of a table");
		}
	}

	public DBApp() {
		init();
	}

	/**
	 * Reads the DBApp.config file
	 */
	public void init() {
		try {

			int[] z = new int[2];
			int i = 0;
			File f = new File("config\\DBApp.config");
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			while (br.ready()) {
				int y;
				String[] x = br.readLine().split("=");
				try {

					y = Integer.parseInt(x[1]);

					if (i == 0) {
						maxRow = y;
						System.out.println("Max Row Count is: " + maxRow);
						i++;
					} else {
						nodeSize = y;
						System.out.println("Max Node Size is: " + nodeSize);
					}
				} catch (NumberFormatException e) {
					try {
						y = Integer.parseInt(x[1].substring(1));
						if (i == 0) {
							maxRow = y;
							System.out.println("Max Row Count is: " + maxRow);
							i++;
						} else {
							nodeSize = y;
							System.out.println("Max Node Size is: " + nodeSize);
						}

					} catch (Exception ex) {
						System.out.println("PROBLEM IN CONFIG INPUT FORMAT");
					}

				} catch (Exception ex) {
					System.out.println("PROBLEM IN CONFIG INPUT FORMAT");
				}

			}
		} catch (FileNotFoundException ep) {
			System.out.println("PROBLEM READING CONFIG FILE (NOT FOUND)");
			ep.printStackTrace();

		} catch (IOException e) {
			System.out.println("PROBLEM READING CONFIG FILE/IOEXCEPTION");

			e.printStackTrace();
		}

	}

	/**
	 * Display Table info and all records
	 * 
	 * @param tableName The Table to Display info and all records
	 * @throws DBAppException
	 */
	public static void printTable(String tableName) throws DBAppException {
		try{
			Table t = readTable(tableName);
			System.out.println("TABLE NAME::" + t.name);
			System.out.println("CLUSTERING KEY::" + t.clusteringKey);
			System.out.println("COLUMN NAMES/TYPES::" + t.colNameType);
			t.viewTable();
		}
		catch (Exception e){
			throw new DBAppException("Error while printing table");
		}
	}

	/**
	 * 
	 * Creates a new table
	 * 
	 * @param strTableName
	 * @param strClusteringKeyColumn
	 * @param htblColNameType
	 * @throws DBAppException
	 */
	public void createTable(String strTableName, String strClusteringKeyColumn,
			Hashtable<String, String> htblColNameType) throws DBAppException {

		for (String x : htblColNameType.keySet()) {
			String type = htblColNameType.get(x).toLowerCase();
			if (!checkSupportedType(type)) {
				throw new DBAppException("UNSOPPORTED DATA TYPE INSERTED");
			}
		}

		Table t = new Table(strTableName, strClusteringKeyColumn, htblColNameType, maxRow);

	}

	/**
	 *
	 * creates an r tree if the column given is a polygon
	 *
	 * @param strTableName
	 * @param strColName
	 * @throws DBAppException
	 */

	public void createRTreeIndex(String strTableName, String strColName) throws DBAppException {
		try {
			Table t = readTable(strTableName);
			boolean flag = true;
			RTree r = null;
			if (getIndices(strTableName).contains(strColName))
				flag = false;
			if (t.colNameType.get(strColName) != null
					&& t.colNameType.get(strColName).toLowerCase().equals("java.awt.polygon") && flag) {
				r = new RTree(t, strColName, nodeSize);
			} else {
				throw new DBAppException("Invalid data type for R-Tree");
			}
			if (!flag)
				throw new DBAppException("Index already created");
			t = readTable(strTableName);
			setIndexTrue(strTableName, strColName);
			for (int i = 0; i < t.maxPage && r != null; i++) {
				Vector v = loadPage(t.Pages.get(i));
				for (int j = 0; j < v.size(); j++) {
					Hashtable h = (Hashtable) v.get(j);
					r.insert((Comparable) h.get(r.getColKey()), h);
				}
			}
		}
		catch (Exception e){
			throw new DBAppException("Error while creating R tree");
		}
	}

	/**
	 * Returns a Table object which contains all info of table from metadata and
	 * binary file
	 * 
	 * @param n Table Name
	 * @return Table object with all info of table
	 * @throws DBAppException
	 */
	public static Table readTable(String n) throws DBAppException {
		try {

			FileInputStream fileIn = new FileInputStream("data" + "\\" + n + ".class");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			Table t = (Table) in.readObject();
			t.clusteringKey = readKey(n);
			t.colNameType = readColNameType(n);

			// System.out.println(t.name + " Object Deserialized");
			in.close();
			fileIn.close();

			return t;

		} catch (Exception e) {
			throw new DBAppException("PROBLEM READING TABLE: " + n);

		}

	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * Inserts the Row into the table
	 * 
	 * @param strTableName     Table name
	 * @param htblColNameValue Table Columns names Values
	 * @throws DBAppException
	 */

	public static void insertIntoTable(String strTableName, Hashtable<String, Object> htblColNameValue)
			throws DBAppException {
		try{
			Table t = readTable(strTableName);
			// System.out.println("INPUT TABLE ISSSS: " + htblColNameValue);
			checkInput(t.clusteringKey, t.colNameType, htblColNameValue);
			// DAH HABAL// BA8AYAR EL INPUT BETA3 POLYGON LE myPolygon 3ashan implements
			// comparable
			for (String s : t.colNameType.keySet()) {
				if (t.colNameType.get(s).toLowerCase().equals("java.awt.polygon")) {
					Polygon p = (Polygon) htblColNameValue.get(s);
					myPolygon mp = new myPolygon(p.xpoints, p.ypoints, p.npoints);
					htblColNameValue.put(s, mp);
				}
			}
			//// 8AYAR EL COMPARE!!!!!!!!!!
			RTree r = null;
			BTree b = null;
			if (!getIndices(t.name).contains(t.clusteringKey)) {
				if (!getIndices(t.name).isEmpty()) {
					ArrayList<Object> trees = new ArrayList<>();
					LinkedList<String> list = getIndices(t.name);
					for (int i = 0; i < list.size(); i++) {
						trees.add(readBPTree(t.name + "_" + list.get(i)));
					}
					for (int i = 0; i < trees.size(); i++) {
						int index;
						if (t.colNameType.get(list.get(i)).toLowerCase().equals("java.awt.polygon")) {
							String col = ((RTree) trees.get(i)).getColKey();
							index = ((RTree) trees.get(i)).insert((Comparable) htblColNameValue.get(col), htblColNameValue);
						} else {
							String col = ((BTree) trees.get(i)).getColKey();
							index = ((BTree) trees.get(i)).insert((Comparable) htblColNameValue.get(col), htblColNameValue);
						}
						System.out.println("");
						System.out.println("************************************");
						System.out.println("INPUTTING :------- " + htblColNameValue);
						System.out.println("key inserted at: " + index);
						System.out.println("************************************");
						System.out.println("");
					}
				}
				String start = insertIntoTable1(strTableName, htblColNameValue);
				if (start == null) {
					return;
				} else {
					treatOverflow(strTableName, start);
				}
			} else {
				if (!getIndices(t.name).isEmpty()) {
					ArrayList<Object> trees = new ArrayList<>();
					LinkedList<String> list = getIndices(t.name);
					for (int i = 0; i < list.size(); i++) {
						trees.add(readBPTree(t.name + "_" + list.get(i)));
					}
					for (int i = 0; i < trees.size(); i++) {
						String col;
						int index;
						if (t.colNameType.get(list.get(i)).toLowerCase().equals("java.awt.polygon")) {
							col = ((RTree) trees.get(i)).getColKey();
							htblColNameValue.put("TouchDate", new Date());
							index = ((RTree) trees.get(i)).insert((Comparable) htblColNameValue.get(col), htblColNameValue);
						} else {
							col = ((BTree) trees.get(i)).getColKey();
							htblColNameValue.put("TouchDate", new Date());
							index = ((BTree) trees.get(i)).insert((Comparable) htblColNameValue.get(col), htblColNameValue);
						}
						System.out.println("");
						System.out.println("************************************");
						System.out.println("INPUTTING :------- " + htblColNameValue);
						System.out.println("key inserted at: " + index);
						System.out.println("************************************");
						System.out.println("");
						if (col.equals(t.clusteringKey)) {
							System.out.println("++++++++++++++++++++++++++++++++");
							System.out.println("i arrived here :)");
							System.out.println("++++++++++++++++++++++++++++++++");
							insertAtIndex(t.name, htblColNameValue, index);
						}
					}
				}
			}

		}
		catch (Exception e)
		{
			throw new DBAppException("error while inserting into table");
		}
	}

	/**
	 *
	 * takes a number and calculates the page and inserts accordingly
	 *
	 * @param tblName
	 * @param in
	 * @param idx
	 * @throws DBAppException
	 */

	public static void insertAtIndex(String tblName, Hashtable<String, Object> in, int idx) throws DBAppException {
		try{
			Table t = readTable(tblName);

			if (t.Pages.size() == 0) {
				t.maxPage++;
				String pageName = t.name + "_" + t.maxPage + ".class";
				t.Pages.addLast(pageName);
				Vector v = new Vector();
				v.add(in);
				savePage(pageName, v);
				t.writeTable();
				return;
			}
			String curP;
			Vector curV;
			int i = 0;
			for (i = 0; i < t.Pages.size(); i++) {
				curP = t.Pages.get(i);
				curV = loadPage(curP);
				idx -= curV.size();
				if (idx <= 0) {
					int loc = bin(curV, in.get(t.clusteringKey), t.colNameType.get(t.clusteringKey), t.clusteringKey);
					in.put("TouchDate", new Date());
					curV.add(loc, in);
					savePage(curP, curV);
					t.writeTable();
					treatOverflow(tblName, curP);
					return;

				} else {

				}
			}

			if (i == t.Pages.size()) {
				curP = t.Pages.get(i - 1);
				curV = loadPage(curP);
				in.put("TouchDate", new Date());
				curV.add(in);

				savePage(curP, curV);

				t.writeTable();
				treatOverflow(tblName, curP);
			}

		}
		catch (Exception e){
			throw new DBAppException("Error while inserting");
		}
	}

	/**
	 *
	 * handles overflow in pages
	 *
	 * @param strTableName
	 * @param start
	 * @throws DBAppException
	 */

	public static void treatOverflow(String strTableName, String start) throws DBAppException {
		try{
			Table t = readTable(strTableName);
			int max = t.maxRowCount;
			String curP;
			Vector curV;
			int i = t.Pages.indexOf(start);
			if (i == -1) {
				throw new DBAppException("PROBLEM TREATING OVERFLOW: STARTING PAGE IS NOT FOUND");
			}

			Object x = null;
			for (; i < t.Pages.size(); i++) {

				curP = t.Pages.get(i);
				curV = loadPage(curP);
				if (x != null) {
					curV.add(0, x);

				}

				if (curV.size() > max) {
					x = curV.remove(curV.size() - 1);

				} else {
					savePage(curP, curV);
					return;
				}
				savePage(curP, curV);

			}
			if (x != null) {
				t.maxPage++;
				String pageName = t.name + "_" + t.maxPage + ".class";
				t.Pages.addLast(pageName);
				Vector v = new Vector();
				((Hashtable<String, Object>) (x)).put("TouchDate", new Date());
				v.add(x);
				savePage(pageName, v);
				t.writeTable();

			}

		}
		catch (Exception e){
			throw new DBAppException("Error while treating overflow");
		}
	}

	/**
	 *
	 * inserts into table
	 *
	 * @param strTableName
	 * @param htblColNameValue
	 * @return
	 * @throws DBAppException
	 */

	public static String insertIntoTable1(String strTableName, Hashtable<String, Object> htblColNameValue)
			throws DBAppException {
		try{
			Table t = readTable(strTableName);
			String clust = t.clusteringKey;

			if (t.Pages.size() == 0) {
				t.maxPage++;
				String pageName = t.name + "_" + t.maxPage + ".class";
				t.Pages.addLast(pageName);
				Vector v = new Vector();
				htblColNameValue.put("TouchDate", new Date());
				v.add(htblColNameValue);
				savePage(pageName, v);
				t.writeTable();
				return null;
			}

			String leftP, curP, rightP;
			Vector leftV, curV, rightV;
			int i;
			for (i = 0; i < t.Pages.size(); i++) {
				leftP = (i == 0) ? null : t.Pages.get(i - 1);
				curP = t.Pages.get(i);
				rightP = (i == t.Pages.size() - 1) ? null : t.Pages.get(i + 1);
				leftV = loadPage(leftP);
				curV = loadPage(curP);
				rightV = loadPage(rightP);

				// LAST ELEMENT IN PAGE IS EQUAL TO INPUT INSERT INTO PAGE AND RETURN IT FOR
				// OVERFLOW
				if (compare(t.colNameType.get(clust), htblColNameValue.get(clust),
						((Hashtable<String, Object>) curV.lastElement()).get(clust)) == 0) {
					// INSERT INTO PAGE AND RETURN IT FOR OVERFLOW
					htblColNameValue.put("TouchDate", new Date());
					curV.add(htblColNameValue);
					savePage(leftP, leftV);
					savePage(curP, curV);
					savePage(rightP, rightV);
					t.writeTable();
					return curP;

					// END OF EQUAL
				}

				//////////////////// IF LAST ELEMENT IS LARGER THAT INPUT ELEMENT
				if (compare(t.colNameType.get(clust), ((Hashtable<String, Object>) curV.lastElement()).get(clust),
						htblColNameValue.get(clust)) >= 1) {
					// CHECK IF INPUT IS SAMLLER OR EQUAL THAN FIRST ELEMENT
					// IF SO INSERT IT INTO POSITION OF LEFT PAGE IF EXISTS
					if (compare(t.colNameType.get(clust), htblColNameValue.get(clust),
							((Hashtable<String, Object>) curV.firstElement()).get(clust)) <= 0) {
						// THERE IS NO LEFT PAGE INSERT INTO FIRST POSITIO OF CURRENT PAGE
						if (leftV == null) {
							htblColNameValue.put("TouchDate", new Date());
							curV.add(0, htblColNameValue);
							savePage(leftP, leftV);
							savePage(curP, curV);
							savePage(rightP, rightV);
							t.writeTable();
							return curP;

						} // THERE IS A LEFT PAGE: INSERT INTO LAST POSITION OF THIS LEFT PAGE AND RETURN
						// IT FOR OVERFLOW
						else {
							htblColNameValue.put("TouchDate", new Date());
							leftV.add(htblColNameValue);
							savePage(leftP, leftV);
							savePage(curP, curV);
							savePage(rightP, rightV);
							t.writeTable();
							return leftP;

						}
					}
					// INPUT IS LARGER THAN FIRST ELEMENT: INSERT USING BINARY SEARCH
					else {
						int idx = bin(curV, htblColNameValue.get(clust), t.colNameType.get(clust), clust);
						htblColNameValue.put("TouchDate", new Date());
						curV.add(idx, htblColNameValue);
						savePage(leftP, leftV);
						savePage(curP, curV);
						savePage(rightP, rightV);
						t.writeTable();
						return curP;
					}

				} //// END OF LARGER THAN
			}

			//////////////////////////////////////////

			if (i == t.Pages.size()) {
				curP = t.Pages.get(i - 1);
				curV = loadPage(curP);
				htblColNameValue.put("TouchDate", new Date());
				curV.add(htblColNameValue);

				savePage(curP, curV);

				t.writeTable();
				return curP;
			}

			return null;
		}
		catch (Exception e){
			throw new DBAppException("error while inserting");
		}


	}

	/**
	 *
	 * binary search
	 *
	 * @param v
	 * @param in
	 * @param type
	 * @param clust
	 * @return
	 * @throws DBAppException
	 */

	public static int bin(Vector v, Object in, String type, String clust) throws DBAppException {
		try{
			int h = v.size() - 1;
			int l = 0;
			int m = (h + l) / 2;
			while (l <= h) {
				m = (h + l) / 2;

				Object mid = ((Hashtable<String, Object>) (v.get(m))).get(clust);
				if (compare(type, in, mid) == 0) {
					return m;
				}
				if (compare(type, in, mid) >= 1) {
					l = m + 1;
				} else {
					h = m - 1;
				}

			}

			return l;

		}
		catch (Exception e){
			throw new DBAppException("error while doing binary search");
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * Type casts the input Objects into the type that is given and then calls the
	 * compareTo method on them
	 * 
	 * @param type Original type of the objects
	 * @param o1   The Object already in the table
	 * @param o2   The Object being inserted
	 * @return returns -1 if o2 is bigger that o1 0 if equal 1 if o1 is bigger that
	 *         o2
	 * @throws DBAppException
	 */
	public static int compare(String type, Object o1, Object o2) throws DBAppException {
		if (type.toLowerCase().equals("java.lang.integer")) {
			Integer i1 = (Integer) o1;
			Integer i2 = (Integer) o2;
			return i1.compareTo(i2);
		}

		if (type.toLowerCase().equals("java.lang.string")) {
			String s1 = (String) o1;
			String s2 = (String) o2;
			return s1.compareTo(s2);

		}
		if (type.toLowerCase().equals("java.lang.double")) {
			Double s1 = (Double) o1;
			Double s2 = (Double) o2;
			return s1.compareTo(s2);

		}
		if (type.toLowerCase().equals("java.util.date")) {
			Date s1 = (Date) o1;
			Date s2 = (Date) o2;
			return s1.compareTo(s2);

		}
		if (type.toLowerCase().equals("java.lang.boolean")) {
			Boolean b1 = (Boolean) o1;
			Boolean b2 = (Boolean) o2;
			if (b1 && b2) {
				return 0;
			}
			if ((!b1) && (!b2)) {
				return 0;
			}
			if (!b1 && b2) {
				return -1;
			}
			return 1;
		}
		if (type.toLowerCase().equals("java.awt.polygon")) {
			Polygon t1 = (Polygon) o1;
			Polygon t2 = (Polygon) o2;
			myPolygon p1 = new myPolygon(t1.xpoints, t1.ypoints, t1.npoints);
			myPolygon p2 = new myPolygon(t2.xpoints, t2.ypoints, t2.npoints);
			return p1.compareTo(p2);

		}

		throw new DBAppException("No Matching types in Comparison");
	}

	/**
	 * Given a page name deserializes and returns the vector inside the page
	 * 
	 * @param pageName Page name on disk
	 * @return
	 * @throws DBAppException
	 */
	public static Vector loadPage(String pageName) throws DBAppException {
		if (pageName == null) {
			return null;
		}
		FileInputStream fileIn;
		ObjectInputStream in;
		try {
			fileIn = new FileInputStream("data" + "\\" + pageName);
			in = new ObjectInputStream(fileIn);

			Vector v = (Vector) in.readObject();

			// System.out.println("Page" + pageName + " Deserialized");
			in.close();
			fileIn.close();
			return v;
		} catch (FileNotFoundException e) {
			throw new DBAppException("problem finding/reading file: " + pageName);
		} catch (IOException e) {
			throw new DBAppException("IO Problem: " + pageName);
		} catch (ClassNotFoundException e) {
			throw new DBAppException("Class Not Found Exception: " + pageName);
		}
	}

	/**
	 * Serializes a vector and stores it inside a file with the given page name
	 * 
	 * @param pageName
	 * @param v
	 * @throws DBAppException
	 */
	public static void savePage(String pageName, Vector v) throws DBAppException {
		if (pageName == null) {
			return;

		}
		try {
			if (new File("data" + "\\" + pageName).createNewFile()) {
				// System.out.println("File " + pageName + " created successfully");
			} else {
				// System.out.println("File " + pageName + " not created");
			}

			try {

				FileOutputStream file = new FileOutputStream("data" + "\\" + pageName);
				ObjectOutputStream out = new ObjectOutputStream(file);

				// Method for serialization of object
				out.writeObject(v);

				out.close();
				file.close();

				// System.out.println(pageName + "Object has been serialized");
			} catch (Exception e) {
				e.printStackTrace();

			}

		} catch (IOException e) {
			throw new DBAppException("Problem Serializing and writing to file: " + pageName);
		}
	}

	/**
	 * checks if the input given has correct types and no missing columns
	 * 
	 * @param clusteringKey
	 * @param tblColType
	 * @param inVal
	 * @return
	 * @throws DBAppException
	 */
	public static void checkInput(String clusteringKey, Hashtable<String, String> tblColType,
			Hashtable<String, Object> inVal) throws DBAppException {

		if (inVal.get(clusteringKey) == null) {
			throw new DBAppException("PROBLEM WITH INPUT: NULL CLUSTERING KEY");
		}
		if (tblColType.size() != inVal.size()) {
			throw new DBAppException("PROBLEM WITH INPUT: INPUT COLUMNS ARE MISSING." + " INPUT SIZE IS:  "
					+ inVal.size() + "  TABLE SIZE IS: " + tblColType.size());
		}

		for (String key : tblColType.keySet()) {
			if (key.equals("TouchDate")) {
				continue;
			}
			String tblType = tblColType.get(key).toLowerCase();
			String inType = inVal.get(key).getClass().getCanonicalName().toLowerCase();
			if (!checkSupportedType(inType)) {
				throw new DBAppException("PROBLEM WITH INPUT: INPUT COLUMN IS NOT SUPPORTED");
			}
			if (!(tblType.equals(inType))) {
				throw new DBAppException("PROBLEM WITH INPUT: INPUT VALUE AND COLUMN TYPES MISMATCHED");
			}
		}

	}

	/**
	 * 
	 * }checks if a type is within the accepted types
	 * 
	 * @param type
	 * @return
	 */
	public static boolean checkSupportedType(String type) {
		String[] types = { "java.lang.integer", "java.lang.string", "java.lang.double", "java.lang.boolean",
				"java.util.date", "java.awt.polygon" };
		for (String x : types) {
			if (x.equals(type)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Updates all rows in the table which contain the same clustering key
	 * 
	 * @param strTableName
	 * @param strClusteringKey
	 * @param htblColNameValue
	 * @throws DBAppException
	 */
	public void updateTable(String strTableName, String strClusteringKey, Hashtable<String, Object> htblColNameValue)
			throws DBAppException {
		try{
			Table t = readTable(strTableName);
			if (!checkUpdateInput(t.colNameType, htblColNameValue)) {
				throw new DBAppException("WRONG UPDATE INPUT");
			}
			for (String s : htblColNameValue.keySet()) {
				if (t.colNameType.get(s).toLowerCase().equals("java.awt.polygon")) {
					Polygon p = (Polygon) htblColNameValue.get(s);
					myPolygon mp = new myPolygon(p.xpoints, p.ypoints, p.npoints);
					htblColNameValue.put(s, mp);
				}
			}

			LinkedList ind = getIndices(strTableName);
			if (ind.contains(t.clusteringKey)) {
				t.writeTable();
				Object clust = getObjectType(t.colNameType.get(t.clusteringKey), strClusteringKey);
				LinkedList<Hashtable> updated = new LinkedList<>();
				if (t.colNameType.get(t.clusteringKey).equals("java.awt.polygon")) {
					RTree b = (RTree) readBPTree(t.name + "_" + t.clusteringKey);
					RTreeLeafNode leaf = b.findLeafNodeShouldContainKey((Comparable) clust);
					int index = leaf.search((Comparable) clust);
					if (index != -1)
						updated = naderUpdate(strTableName, strClusteringKey, htblColNameValue, index);
					if (!ind.isEmpty() && !updated.isEmpty()) {
						for (int i = 0; i < ind.size(); i++) {
							b = (RTree) readBPTree(t.name + "_" + ind.get(i));
							Hashtable temp1 = htblColNameValue;
							for (int j = 0; j < updated.size(); j++) {
								Enumeration<String> enumeration1 = updated.get(j).keys();
								Enumeration<String> enumeration2 = temp1.keys();
								LinkedList<String> updatedKeys = new LinkedList<>();
								while (enumeration2.hasMoreElements()) {
									updatedKeys.add(enumeration2.nextElement());
								}
								// iterate using enumeration object
								while (enumeration1.hasMoreElements()) {
									String keys = enumeration1.nextElement();
									if (!updatedKeys.contains(keys)) {
										temp1.put(keys, updated.get(j).get(keys));
									}
								}
								b.update((Comparable) updated.get(j).get(b.colKey), (Comparable) temp1.get(b.colKey),
										updated.get(j), temp1);
							}
						}
					}
				} else {
					BTree b = (BTree) readBPTree(t.name + "_" + t.clusteringKey);
					BTreeLeafNode leaf = b.findLeafNodeShouldContainKey((Comparable) clust);
					int index = leaf.search((Comparable) clust);
					if (index != -1)
						updated = naderUpdate(strTableName, strClusteringKey, htblColNameValue, index);
					if (!ind.isEmpty() && !updated.isEmpty()) {
						for (int i = 0; i < ind.size(); i++) {
							b = (BTree) readBPTree(t.name + "_" + ind.get(i));
							Hashtable temp1 = htblColNameValue;
							for (int j = 0; j < updated.size(); j++) {
								Enumeration<String> enumeration1 = updated.get(j).keys();
								Enumeration<String> enumeration2 = temp1.keys();
								LinkedList<String> updatedKeys = new LinkedList<>();
								while (enumeration2.hasMoreElements()) {
									updatedKeys.add(enumeration2.nextElement());
								}
								// iterate using enumeration object
								while (enumeration1.hasMoreElements()) {
									String keys = enumeration1.nextElement();
									if (!updatedKeys.contains(keys)) {
										temp1.put(keys, updated.get(j).get(keys));
									}
								}
								b.update((Comparable) updated.get(j).get(b.colKey), (Comparable) temp1.get(b.colKey),
										updated.get(j), temp1);
							}
						}
					}
				}

				return;
			}
			String curP;
			Vector curV;
			Object clust = getObjectType(t.colNameType.get(t.clusteringKey), strClusteringKey);
			LinkedList<Hashtable> updated = new LinkedList<>();
			for (int i = 0; i < t.Pages.size(); i++) {
				curP = t.Pages.get(i);
				curV = loadPage(curP);
				if (compare(t.colNameType.get(t.clusteringKey),
						((Hashtable<String, Object>) (curV.firstElement())).get(t.clusteringKey), clust) >= 1) {

					savePage(curP, curV);
					return;
				}
				int idx = lookForIndex(curV, clust, t.clusteringKey, t.colNameType.get(t.clusteringKey));
				if (idx == -1) {
					savePage(curP, curV);
					continue;
				}
				for (int j = idx; j < curV.size(); j++) {
					if (checkUpdate(strClusteringKey, t.colNameType.get(t.clusteringKey),
							((Hashtable<String, Object>) curV.get(j)).get(t.clusteringKey))) {
						updated.add((Hashtable) ((Hashtable<String, Object>) curV.get(j)).clone());
						for (String s : htblColNameValue.keySet()) {
							((Hashtable<String, Object>) curV.get(j)).put(s, htblColNameValue.get(s));
						}
					} else {
						break;
					}
				}
				for (int k = idx - 1; k >= 0; k--) {
					if (checkUpdate(strClusteringKey, t.colNameType.get(t.clusteringKey),
							((Hashtable<String, Object>) curV.get(k)).get(t.clusteringKey))) {
						updated.add((Hashtable) ((Hashtable<String, Object>) curV.get(k)).clone());
						for (String s : htblColNameValue.keySet()) {
							((Hashtable<String, Object>) curV.get(k)).put(s, htblColNameValue.get(s));
						}
					} else {
						break;
					}
				}
				savePage(curP, curV);

			}
			if (!ind.isEmpty() && !updated.isEmpty()) {
				for (int i = 0; i < ind.size(); i++) {
					if (t.colNameType.get(t.clusteringKey).equals("java.awt.polygon")) {
						RTree b = (RTree) readBPTree(t.name + "_" + ind.get(i));
						Hashtable temp1 = htblColNameValue;
						for (int j = 0; j < updated.size(); j++) {
							Enumeration<String> enumeration1 = updated.get(j).keys();
							Enumeration<String> enumeration2 = temp1.keys();
							LinkedList<String> updatedKeys = new LinkedList<>();
							while (enumeration2.hasMoreElements()) {
								updatedKeys.add(enumeration2.nextElement());
							}
							// iterate using enumeration object
							while (enumeration1.hasMoreElements()) {
								String keys = enumeration1.nextElement();
								if (!updatedKeys.contains(keys)) {
									temp1.put(keys, updated.get(j).get(keys));
								}
							}
							b.update((Comparable) updated.get(j).get(b.colKey), (Comparable) temp1.get(b.colKey),
									updated.get(j), temp1);
						}
					} else {
						BTree b = (BTree) readBPTree(t.name + "_" + ind.get(i));
						Hashtable temp1 = htblColNameValue;
						for (int j = 0; j < updated.size(); j++) {
							Enumeration<String> enumeration1 = updated.get(j).keys();
							Enumeration<String> enumeration2 = temp1.keys();
							LinkedList<String> updatedKeys = new LinkedList<>();
							while (enumeration2.hasMoreElements()) {
								updatedKeys.add(enumeration2.nextElement());
							}
							// iterate using enumeration object
							while (enumeration1.hasMoreElements()) {
								String keys = enumeration1.nextElement();
								if (!updatedKeys.contains(keys)) {
									temp1.put(keys, updated.get(j).get(keys));
								}
							}
							b.update((Comparable) updated.get(j).get(b.colKey), (Comparable) temp1.get(b.colKey),
									updated.get(j), temp1);
						}
					}

				}
			}
		}
		catch (Exception e){
			throw new DBAppException("error while updating");
		}
	}

	/**
	 *
	 * takes a number and calculates the page containing the rows to be updated and returns all rows which were updated
	 *
	 * @param strTableName
	 * @param strClusteringKey
	 * @param htblColNameValue
	 * @param index
	 * @return linked list
	 * @throws DBAppException
	 */

	public LinkedList<Hashtable> naderUpdate(String strTableName, String strClusteringKey,
			Hashtable<String, Object> htblColNameValue, int index) throws DBAppException {
		try{
			Table t = readTable(strTableName);
			LinkedList<Hashtable> updated = new LinkedList<>();
			if (!checkUpdateInput(t.colNameType, htblColNameValue)) {
				throw new DBAppException("WRONG UPDATE INPUT");
			}

			String curP;
			Vector curV;
			Object clust = getObjectType(t.colNameType.get(t.clusteringKey), strClusteringKey);
			int m = 0;
			for (m = 0; m < t.Pages.size(); m++) {
				curP = t.Pages.get(m);
				curV = loadPage(curP);
				index -= curV.size();
				if (index <= 0) {
					savePage(curP, curV);
					break;
				}
			}

			for (int i = m; i < t.Pages.size(); i++) {
				curP = t.Pages.get(i);
				curV = loadPage(curP);
				if (compare(t.colNameType.get(t.clusteringKey),
						((Hashtable<String, Object>) (curV.firstElement())).get(t.clusteringKey), clust) >= 1) {

					savePage(curP, curV);
					return updated;
				}

				int idx = lookForIndex(curV, clust, t.clusteringKey, t.colNameType.get(t.clusteringKey));
				if (idx == -1) {
					savePage(curP, curV);
					continue;
				}
				for (int j = idx; j < curV.size(); j++) {
					if (checkUpdate(strClusteringKey, t.colNameType.get(t.clusteringKey),
							((Hashtable<String, Object>) curV.get(j)).get(t.clusteringKey))) {
						updated.add((Hashtable) ((Hashtable<String, Object>) curV.get(j)).clone());
						for (String s : htblColNameValue.keySet()) {
							((Hashtable<String, Object>) curV.get(j)).put(s, htblColNameValue.get(s));
						}
					} else {
						break;
					}
				}
				for (int k = idx - 1; k >= 0; k--) {
					if (checkUpdate(strClusteringKey, t.colNameType.get(t.clusteringKey),
							((Hashtable<String, Object>) curV.get(k)).get(t.clusteringKey))) {
						updated.add((Hashtable) ((Hashtable<String, Object>) curV.get(k)).clone());
						for (String s : htblColNameValue.keySet()) {
							((Hashtable<String, Object>) curV.get(k)).put(s, htblColNameValue.get(s));
						}
					} else {
						break;
					}
				}
				savePage(curP, curV);

			}
			return updated;
		}
		catch (Exception e){
			throw new DBAppException("error while updating");
		}
	}

	/**
	 * looks for an index using binary search
	 *
	 * @param v
	 * @param o
	 * @param clust
	 * @param clustType
	 * @return
	 * @throws DBAppException
	 */
	public static int lookForIndex(Vector v, Object o, String clust, String clustType) throws DBAppException {
		int h = v.size() - 1;
		int l = 0;

		int m = (h + l) / 2;
		while (h >= l) {
			m = (h + l) / 2;
			Object x = ((Hashtable<String, Object>) v.get(m)).get(clust);
			// Object x = v.get(m);
			if (DBApp.compare(clustType, o, x) == 0) {
				return m;
			}
			if (DBApp.compare(clustType, o, x) >= 1) {
				l = m + 1;
			} else {
				h = m - 1;
			}
		}
		return -1;

	}

	/**
	 * 
	 * checks if the input to the update method is correct
	 * 
	 * @param tbl
	 * @param in
	 * @return
	 */
	public static boolean checkUpdateInput(Hashtable<String, String> tbl, Hashtable<String, Object> in) {
		for (String x : in.keySet()) {
			if (tbl.containsKey(x)) {
				if (tbl.get(x).toLowerCase().equals(in.get(x).getClass().getCanonicalName().toLowerCase())) {

				} else {
					return false;
				}
			} else {
				return false;
			}
		}

		return true;

	}

	/**
	 * 
	 * checks if the Object that is retrieved from the row is equal to the input
	 * clustering key value
	 * 
	 * @param strClusteringKey
	 * @param type
	 * @param object
	 * @return
	 * @throws DBAppException
	 */
	public static boolean checkUpdate(String strClusteringKey, String type, Object object) throws DBAppException {
		Object obj = getObjectType(type, strClusteringKey);
		if (compare(type, object, obj) != 0) {
			return false;
		}

		return true;
	}

	/**
	 * Takes as input a type and an object encoded as a string and returns the
	 * object as an actual object
	 * 
	 * @param type
	 * @param object
	 * @return
	 * @throws DBAppException
	 */
	public static Object getObjectType(String type, String object) throws DBAppException {
		if (type.toLowerCase().equals("java.lang.integer")) {
			int x = Integer.parseInt(object);
			return new Integer(x);
		}
		if (type.toLowerCase().equals("java.lang.string")) {
			return object;
		}

		if (type.toLowerCase().equals("java.lang.double")) {
			double x = Double.parseDouble(object);
			return new Double(x);
		}

		if (type.toLowerCase().equals("java.util.date")) {
			int y = Integer.parseInt(object.substring(0, 4));
			int m = Integer.parseInt(object.substring(5, 6));
			int d = Integer.parseInt(object.substring(8));
			Date date = new Date(y, m, d);
			return date;
		}
		if (type.toLowerCase().equals("java.awt.polygon")) {
			String[] y = object.split(",");
			int[] xp = new int[y.length / 2];
			int[] yp = new int[y.length / 2];

			int idx = 0;
			for (int i = 0; i < y.length - 1; i += 2) {

				int xpt = Integer.parseInt(y[i].substring(1));
				int ypt = Integer.parseInt(y[i + 1].substring(0, y[i + 1].length() - 1));
				xp[idx] = xpt;
				yp[idx++] = ypt;

			}
			return new myPolygon(xp, yp, xp.length);
		}

		throw new DBAppException("Error Parsing the input strClusteringKey");

	}

	/**
	 * Deletes all rows from a given table if it contains all the rows in the input
	 * row
	 * 
	 * @param strTableName
	 * @param htblColNameValue
	 * @throws DBAppException
	 */
	public void deleteFromTable(String strTableName, Hashtable<String, Object> htblColNameValue) throws DBAppException {
		try{
			Table t = readTable(strTableName);
			if (!checkUpdateInput(t.colNameType, htblColNameValue)) {
				throw new DBAppException("WRONG DELETE INPUT");
			}
			for (String s : htblColNameValue.keySet()) {
				if (t.colNameType.get(s).toLowerCase().equals("java.awt.polygon")) {
					Polygon p = (Polygon) htblColNameValue.get(s);
					myPolygon mp = new myPolygon(p.xpoints, p.ypoints, p.npoints);
					htblColNameValue.put(s, mp);
				}
			}
			LinkedList indices = getIndices(t.name);
			if (indices.contains(t.clusteringKey) && htblColNameValue.get(t.clusteringKey) != null) {
				t.writeTable();
				if (t.colNameType.get(t.clusteringKey).equals("java.awt.polygon")) {
					RTree b = (RTree) readBPTree(t.name + "_" + t.clusteringKey);
					LinkedList<Hashtable> deleted = null;
					int idx = b.delete((Comparable) htblColNameValue.get(t.clusteringKey),htblColNameValue);
					if (idx != -1)
						deleted = indexDelete(strTableName, htblColNameValue, idx);
					while (b.delete((Comparable) htblColNameValue.get(t.clusteringKey),htblColNameValue) != -1) {
					}
					indices.remove(t.clusteringKey);
					if (!indices.isEmpty() && deleted != null) {
						for (int i = 0; i < indices.size(); i++) {
							if (t.colNameType.get(indices.get(i)).equals("java.awt.polygon")) {
								RTree r = (RTree) readBPTree(t.name + "_" + indices.get(i));
								for (int j = 0; j < deleted.size(); j++) {
									r.delete((Comparable) deleted.get(j).get(r.colKey), deleted.get(j));
								}
							} else {
								BTree r = (BTree) readBPTree(t.name + "_" + indices.get(i));
								for (int j = 0; j < deleted.size(); j++) {
									r.delete((Comparable) deleted.get(j).get(r.colKey), deleted.get(j));
								}
							}
						}
					}
				} else {
					BTree b = (BTree) readBPTree(t.name + "_" + t.clusteringKey);
					LinkedList<Hashtable> deleted = null;
					int idx = b.delete((Comparable) htblColNameValue.get(t.clusteringKey),htblColNameValue);
					if (idx != -1)
						deleted = indexDelete(strTableName, htblColNameValue, idx);
					while (b.delete((Comparable) htblColNameValue.get(t.clusteringKey),htblColNameValue) != -1) {
					}
					indices.remove(t.clusteringKey);
					if (!indices.isEmpty() && deleted != null) {
						for (int i = 0; i < indices.size(); i++) {
							if (t.colNameType.get(indices.get(i)).equals("java.awt.polygon")) {
								RTree r = (RTree) readBPTree(t.name + "_" + indices.get(i));
								for (int j = 0; j < deleted.size(); j++) {
									r.delete((Comparable) deleted.get(j).get(r.colKey), deleted.get(j));
								}
							} else {
								BTree r = (BTree) readBPTree(t.name + "_" + indices.get(i));
								for (int j = 0; j < deleted.size(); j++) {
									r.delete((Comparable) deleted.get(j).get(r.colKey), deleted.get(j));
								}
							}
						}
					}
				}

			} else {
				linearDelete(strTableName, htblColNameValue);
			}
		}
		catch (Exception e){
			throw new DBAppException("error while deleting");
		}
	}

	/**
	 *
	 * takes an number and calculates the page to delete from directly and returns all values deleted
	 *
	 * @param strTableName
	 * @param htblColNameValue
	 * @param idx
	 * @return linked list
	 * @throws DBAppException
	 */

	public static LinkedList<Hashtable> indexDelete(String strTableName, Hashtable<String, Object> htblColNameValue,
			int idx) throws DBAppException {
		try {
			Table t = readTable(strTableName);
			String curP;
			Vector curV;
			LinkedList<String> del = new LinkedList<>();
			LinkedList<Hashtable> deleted = new LinkedList<>();
			Vector d = new Vector();
			int i = 0;
			for (i = 0; i < t.Pages.size(); i++) {
				curP = t.Pages.get(i);
				curV = loadPage(curP);
				if (idx <= 0) {
					savePage(curP, curV);
					break;
				} else {
					idx -= curV.size();
					if (idx <= 0) {
						savePage(curP, curV);
						break;
					}
					savePage(curP, curV);
				}
			}
			for (; i < t.Pages.size(); i++) {
				curP = t.Pages.get(i);
				curV = loadPage(curP);
				d = new Vector();
				for (int j = 0; j < curV.size(); j++) {

					Hashtable<String, Object> curE = (Hashtable<String, Object>) curV.get(j);
					if (compare(t.colNameType.get(t.clusteringKey), curE.get(t.clusteringKey),
							htblColNameValue.get(t.clusteringKey)) > 0) {
						for (int l = 0; l < d.size(); l++) {
							curV.remove(d.get(l));
						}
						if (curV.size() == 0) {
							del.add(curP);
						}
						savePage(curP, curV);
						for (int m = 0; m < del.size(); m++) {
							curP = del.get(m);
							t.Pages.remove(curP);
							deletePage(curP);
						}

						t.writeTable();
						return deleted;
					}
					if (checkDelete(curE, htblColNameValue, t.colNameType)) {
						d.add(curE);
						deleted.add(curE);
					}

				}
				for (int l = 0; l < d.size(); l++) {
					curV.remove(d.get(l));
				}
				if (curV.size() == 0) {
					del.add(curP);
				}
				savePage(curP, curV);

			}
			for (int m = 0; m < del.size(); m++) {
				curP = del.get(m);
				t.Pages.remove(curP);
				deletePage(curP);
			}
			t.writeTable();
			return deleted;
		}
		catch (Exception e){
			throw new DBAppException("error while deleting");
		}
	}

	/**
	 *
	 * goes over every page linearly to check for the value to be deleted
	 *
	 * @param strTableName
	 * @param htblColNameValue
	 * @throws DBAppException
	 */

	public void linearDelete(String strTableName, Hashtable<String, Object> htblColNameValue) throws DBAppException {
		try{
			Table t = readTable(strTableName);
			if (!checkUpdateInput(t.colNameType, htblColNameValue)) {
				throw new DBAppException("WRONG DELETE INPUT");
			}
			LinkedList<String> tempP = new LinkedList<>();
			LinkedList<Hashtable> deleted = new LinkedList<>();
			while (!t.Pages.isEmpty()) {
				String curP = t.Pages.pollFirst();
				Vector curV = loadPage(curP);
				Vector tempV = new Vector();
				while (!curV.isEmpty()) {
					Hashtable<String, Object> curE = (Hashtable<String, Object>) curV.remove(0);
					if (!checkDelete(curE, htblColNameValue, t.colNameType)) {
						tempV.add(curE);
					} else {
						deleted.add(curE);
					}
				}

				if (tempV.size() == 0) {
					deletePage(curP);

				} else {
					savePage(curP, tempV);
					tempP.addLast(curP);
				}
			}
			t.Pages = tempP;
			t.writeTable();
			t = readTable(t.name);
			LinkedList<String> indices = getIndices(t.name);
			if (!indices.isEmpty()) {
				for (int i = 0; i < indices.size(); i++) {
					if (t.colNameType.get(indices.get(i)).equals("java.awt.polygon")) {
						RTree b = (RTree) readBPTree(t.name + "_" + indices.get(i));
						for (int j = 0; j < deleted.size(); j++) {
							b.delete((Comparable) deleted.get(j).get(b.colKey), deleted.get(j));
						}
					} else {
						BTree b = (BTree) readBPTree(t.name + "_" + indices.get(i));
						for (int j = 0; j < deleted.size(); j++) {
							b.delete((Comparable) deleted.get(j).get(b.colKey), deleted.get(j));
						}
					}
				}
			}
		}
		catch (Exception e){
			throw new DBAppException("error while deleting");
		}
	}

	/**
	 * Reads the Clustering key from the metadata for an input table name
	 * 
	 * @param strTableName The Table Name
	 * @return The Clustering Key for the input Table Name
	 * @throws DBAppException
	 */
	public static String readKey(String strTableName) throws DBAppException {

		try {
			File f = new File("data\\metadata.csv");
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			while (br.ready()) {
				String s = br.readLine();
				String[] x = s.split(",");
				if (x[0].equals(strTableName) && x[3].equals("True")) {
					br.close();
					fr.close();
					return x[1];
				}

			}

			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		throw new DBAppException("PROBLEM RETRIEVING CLUSTERING KEY FROM METADATA");

	}

	/**
	 * 
	 * Reads the column names and types from the metadata for an input table name
	 * 
	 * @param strTableName Table name
	 * @return Hashtable of Column names types
	 * @throws DBAppException
	 */
	public static Hashtable<String, String> readColNameType(String strTableName) throws DBAppException {
		try {
			File f = new File("data\\metadata.csv");
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			Hashtable<String, String> htbl = new Hashtable();
			while (br.ready()) {
				String s = br.readLine();
				String[] x = s.split(",");
				if (x[0].equals(strTableName)) {
					if (!x[1].toLowerCase().equals("touchdate")) {
						htbl.put(x[1], x[2].toLowerCase());
					}
				}

			}
			br.close();
			fr.close();
			return htbl;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		throw new DBAppException("PROBLEM RETRIEVING COLUMN NAME AND TYPE FROM METADATA");

	}

	/**
	 * Deletes a page file from the hard disk
	 * 
	 * @param curP
	 */
	public static void deletePage(String curP) {
		File file = new File("data\\" + curP);

		if (file.delete()) {
			System.out.println(curP + " deleted successfully");
		} else {
			System.out.println(curP + " Failed to delete the file");
		}

	}

	/**
	 * checks delete condition on row in table
	 * 
	 * @param curE
	 * @param inE
	 * @param Types
	 * @return
	 */
	public static boolean checkDelete(Hashtable<String, Object> curE, Hashtable<String, Object> inE,
			Hashtable<String, String> Types) {

		for (String x : inE.keySet()) {
			try {
				if (Types.get(x).toLowerCase().equals("java.awt.polygon")) {
					if (!((myPolygon) (curE.get(x))).equalsto((myPolygon) inE.get(x))) {
						return false;
					}
				}
				if (compare(Types.get(x).toLowerCase(), inE.get(x), curE.get(x)) != 0) {
					return false;
				}
			} catch (DBAppException e) {
				e.printStackTrace();
			}
		}
		return true;
	}


	/**
	 *
	 * returns all columns in a table which have indices from the metadata
	 *
	 * @param tblName
	 * @return
	 */

	public static LinkedList<String> getIndices(String tblName) {
		LinkedList<String> list = new LinkedList<>();

		try {
			File f = new File("data\\metadata.csv");
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			Hashtable<String, String> htbl = new Hashtable();
			while (br.ready()) {
				String s = br.readLine();
				String[] x = s.split(",");
				if (x[0].equals(tblName)) {
					if (x[4].equals("True")) {
						list.add(x[1]);
					}

				}
			}
			br.close();
			fr.close();
			return list;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;

	}

	/**
	 *
	 * updates the metadata when an index is created
	 *
	 * @param tblName
	 * @param colName
	 */

	public static void setIndexTrue(String tblName, String colName) {

		try {
			File f = new File("data\\metadata.csv");
			File tmp = new File("data\\metadata1.csv");
			FileWriter fw = new FileWriter(tmp);

			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);

			while (br.ready()) {
				String s = br.readLine();
				String[] x = s.split(",");
				if (x[0].equals(tblName)) {
					if (x[1].equals(colName)) {
						x[4] = "True";
					}

				}
				for (int i = 0; i < x.length - 1; i++) {
					fw.append(x[i] + ",");
				}
				fw.append(x[4] + "\n");
			}
			br.close();
			fw.flush();
			fw.close();
			fr.close();

			System.out.println(f.delete());
			System.out.println(tmp.renameTo(f));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * this method searches the vector indices' clustering key (using binary search)
	 * for values bigger than or equal to the clustering key of the hashtable given
	 * as an input returns an object array that contains the value of the clustering
	 * key of the first index of the vector and the value of the index that contains
	 * the clustering key that is bigger than or equal to the entered hashtable's
	 * key and returns the value of the index as -1 if not found
	 *
	 * @param v
	 * @param in
	 * @param t
	 * @return
	 * @throws DBAppException
	 */
}
