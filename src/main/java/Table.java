
import java.io.*;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

public class Table implements Serializable {

	String name;
	transient String clusteringKey;
	transient Hashtable<String, String> colNameType;
	LinkedList<String> Pages;
	int maxPage;
	int maxRowCount;

	public void printAllNodes() throws DBAppException {
		if (!DBApp.getIndices(this.name).isEmpty()) {
			for (int i = 0; i < DBApp.getIndices(this.name).size(); i++) {
				BTree b = readBPTree(this.name + "_" + DBApp.getIndices(this.name).get(i));
				b.printTree();
			}
		}
	}

	/**
	 * Serializes the table and stores it in a binary file
	 * 
	 * @throws DBAppException
	 */
	public void writeTable() throws DBAppException {
		try {
			if (new File("data" + "\\" + name + ".class").createNewFile()) {
				System.out.println("File " + name + " created successfully");
			} else {
				System.out.println("File " + name + " not created");
			}

			try {

				FileOutputStream file = new FileOutputStream("data" + "\\" + this.name + ".class");
				ObjectOutputStream out = new ObjectOutputStream(file);

				// Method for serialization of object
				out.writeObject(this);

				out.close();
				file.close();

				// System.out.println(this.name + " Object has been serialized");
			} catch (Exception e) {
				e.printStackTrace();
				throw new DBAppException("Problem Serializing and writing to file: " + this.name);
			}

		} catch (IOException e) {
			throw new DBAppException("Problem Serializing and writing to file: " + this.name);
		}

	}

	/**
	 * Initializes the table and serializes it and save it to hard disk and also
	 * adds table info to the metadata file
	 * 
	 * @param n
	 * @param cluster
	 * @param h
	 * @param max
	 * @throws DBAppException
	 */
	public Table(String n, String cluster, Hashtable<String, String> h, int max) throws DBAppException {

		if (checkExistingTable(n)) {

			name = n;
			// clusteringKey = cluster;
			// colNameType = h;
			Pages = new LinkedList<>();
			maxPage = 0;
			maxRowCount = max;
			writeMetaData(h, cluster);
			writeTable();
		}

	}

	/**
	 * Adds table info to the metadata file
	 * 
	 * @param h
	 * @param c
	 * @throws DBAppException
	 */
	public void writeMetaData(Hashtable<String, String> h, String c) throws DBAppException {
		File f = new File("data\\metadata.csv");
		FileWriter fw;
		try {
			fw = new FileWriter(f, true);

			for (String x : h.keySet()) {
				fw.append(this.name);
				fw.append(",");

				fw.append(x);
				fw.append(",");

				fw.append(h.get(x));
				fw.append(",");
				if (x.equals(c)) {
					fw.append("True");
				} else {
					fw.append("False");
				}

				fw.append(",");

				fw.append("False");
				fw.append("\n");

			}

			fw.append(this.name);
			fw.append(",");

			fw.append("TouchDate");
			fw.append(",");

			fw.append("java.util.Date");
			fw.append(",");

			fw.append("False");
			fw.append(",");

			fw.append("False");

			fw.append("\n");

			fw.flush();
			fw.close();

		} catch (IOException e) {
			throw new DBAppException("PROBLEM READING/WRITING TO CSV FILE");
		}

	}

	/**
	 * Prints all the pages of a table along with their rows
	 * 
	 * @throws DBAppException
	 */
	public void viewTable() throws DBAppException {

		for (String pageName : Pages) {
			try {
				viewPage(pageName);
			} catch (DBAppException e) {
				throw new DBAppException("Problem Serializing and writing to file: " + this.name);
			}
		}

	}

	/**
	 * Prints the rows stored in a page
	 * 
	 * @param pageName
	 * @throws DBAppException
	 */
	public void viewPage(String pageName) throws DBAppException {

		FileInputStream fileIn;
		ObjectInputStream in;
		try {
			fileIn = new FileInputStream("data" + "\\" + pageName);
			in = new ObjectInputStream(fileIn);

			Vector v = (Vector) in.readObject();
			System.out.println("------------------------------");
			System.out.println(pageName);
			for (Object x : v) {
				System.out.println((Hashtable<String, Object>) x);
			}
			System.out.println("------------------------------");
			// System.out.println("Object Deserialized");
			in.close();
			fileIn.close();
		} catch (FileNotFoundException e) {
			throw new DBAppException("problem finding/reading file");
		} catch (IOException e) {
			throw new DBAppException("IO Problem");
		} catch (ClassNotFoundException e) {
			throw new DBAppException("Class Not Found Exception");
		}

	}

	public String toString() {
		return "Table: " + name + "\n" + "Clustering Key: " + clusteringKey + "\n" + "Columns-Types: " + colNameType
				+ "\n" + "max pages: " + maxPage + "\n" + "Max Row Count: " + maxRowCount;
	}

	public static boolean checkExistingTable(String n) throws DBAppException {

		try {
			File f = new File("data\\metadata.csv");
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			Hashtable<String, String> htbl = new Hashtable();
			while (br.ready()) {
				String s = br.readLine();
				String[] x = s.split(",");
				if (x[0].equals(n)) {
					System.out.println("-------------TABLE ALREADY EXISTS---------: " + n);
					br.close();
					fr.close();
					return false;
					// throw new DBAppException("PROBLEM RETRIEVING COLUMN NAME AND TYPE FROM
					// METADATA");
				}
			}
			br.close();
			fr.close();

			return true;

		} catch (FileNotFoundException e) {
			throw new DBAppException("Problem  checking existing table in metadata: " + n);
		} catch (IOException e) {
			throw new DBAppException("Problem  checking existing table in metadata: " + n);
		}

	}

	public static BTree readBPTree(String name) throws DBAppException {
		try {
			FileInputStream fileIn = new FileInputStream("data" + "\\" + name + ".class");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			BTree b = (BTree) in.readObject();

			// System.out.println(b.getbTreeName() + " Object Deserialized");
			in.close();
			fileIn.close();
			return b;

		} catch (Exception e) {
			throw new DBAppException("Problem  reading BPTree: " + name);
		}

	}
}
