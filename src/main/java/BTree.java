
import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * A B+ tree
 * Since the structures and behaviors between internal node and external node are different,
 * so there are two different classes for each kind of node.
 *
 * @param <TKey>   the data type of the key
 * @param <TValue> the data type of the value
 */
public class BTree<TKey extends Comparable<TKey>, TValue> implements Serializable {
    BTreeNode<TKey> root;
    ArrayList<String> nodes;
    int count;
    String bTreeName;
    String colKey;
    String tableName;
    int nodeSize;

    public BTree(Table table, String colKey, int nodeSize) throws DBAppException {
        this.colKey = colKey;
        count = 0;
        this.nodeSize = nodeSize;
        this.bTreeName = table.name + "_" + this.colKey;
        this.nodes = new ArrayList<>();
        this.root = new BTreeLeafNode<TKey,TValue>(this);
        tableName = table.name;
        this.writeBPTree();
    }

    public void writeBPTree() throws DBAppException {
        try {
            if (new File("Data" + "\\" + this.bTreeName + ".class").createNewFile()) {
                System.out.println("File " + this.bTreeName + " created successfully");
            } else {
                System.out.println("File " + this.bTreeName + " not created");
            }

            try {

                FileOutputStream file = new FileOutputStream("data" + "\\" + this.bTreeName + ".class");
                ObjectOutputStream out = new ObjectOutputStream(file);

                // Method for serialization of object
                out.writeObject(this);

                out.close();
                file.close();

                System.out.println(this.bTreeName + " Object has been serialized");
                setIndexTrue(tableName,colKey);
            } catch (Exception e) {
                e.printStackTrace();

                throw new DBAppException("Problem Serializing and writing to file");
            }

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    public BTreeNode<TKey> loadNodeData(String nodeName) throws DBAppException {
        FileInputStream fileIn;
        ObjectInputStream in;
        try {
            if(!nodeName.equals("")){
                fileIn = new FileInputStream("data" + "\\" + nodeName);
                in = new ObjectInputStream(fileIn);

                BTreeNode<TKey> v = (BTreeNode<TKey>) in.readObject();

                System.out.println("Node data " +
                        "" + nodeName + " Deserialized");
                in.close();
                fileIn.close();
                return v;
            }
            else{
                return null;
            }
        } catch (FileNotFoundException e) {
            System.out.println();
            System.out.println("nodeName");
            System.out.println(nodeName);
            System.out.println();
            throw new DBAppException("problem finding/reading file");
        } catch (IOException e) {
            System.out.println(nodeName);
            throw new DBAppException("IO Problem");
        } catch (ClassNotFoundException e) {
            throw new DBAppException("Class Not Found Exception");
        }
    }

    public void saveNodeData(String nodeName, BTreeNode<TKey> v) throws DBAppException {
        try {
            if (new File("data" + "\\" + nodeName).createNewFile()) {
                System.out.println("File " + nodeName + " created successfully");
            } else {
                System.out.println("File " + nodeName + " not created");
            }

            try {

                FileOutputStream file = new FileOutputStream("data" + "\\" + nodeName);
                ObjectOutputStream out = new ObjectOutputStream(file);

                // Method for serialization of object
                out.writeObject(v);

                out.close();
                file.close();

                System.out.println(nodeName + "Object has been serialized");
            } catch (Exception e) {
                e.printStackTrace();
                throw new DBAppException("Problem Serializing and writing to file");
            }

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * Insert a new key and its associated value into the B+ tree.
     */
    public int insert(TKey key, TValue value) throws DBAppException {
        BTreeLeafNode<TKey, TValue> leaf = readBPTree(this.bTreeName).findLeafNodeShouldContainKey(key);
        int index = leaf.insertKey(key, value);
        System.out.println("++++++++++++++++++++++++++++++++");
        System.out.println("Current node: "+leaf.nodeName);
        System.out.println("Current index: "+index);
        System.out.println("Next node: "+leaf.leftSibling);
        System.out.println("++++++++++++++++++++++++++++++++");
        BTreeLeafNode<TKey, TValue> leafNode = (BTreeLeafNode<TKey, TValue>) loadNodeData(leaf.leftSibling);
        while(leafNode != null){
            System.out.println("++++++++++++++++++++++++++++++++");
            System.out.println("Current node: "+leafNode.nodeName);
            System.out.println("Current index: "+index);
            index=leafNode.keyCount+index;
            System.out.println("Updated index: "+index);
            System.out.println("Next node: "+leafNode.leftSibling);
            System.out.println("++++++++++++++++++++++++++++++++");
            leafNode = (BTreeLeafNode<TKey, TValue>) loadNodeData(leafNode.leftSibling);
        }
        if (leaf.isOverflow()) {
            BTreeNode<TKey> n = leaf.dealOverflow();
            if (n != null) {
                BTreeNode<TKey> node = loadNodeData((String) readBPTree(this.bTreeName).nodes.get(0));
                while(!node.parentNode.equals(""))
                {
                    node = loadNodeData(node.parentNode);
                }
                saveNodeData(node.nodeName,n);
            }
        }
        this.printTree();

        return index;
    }

    /**
     * Search a key value on the tree and return its associated value.
     */
    public Object[] search(TKey key) throws DBAppException {
        BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);
        Object[] ret = new Object[2];
        int index = leaf.search(key);
        if (index == -1) {
            return null;
        } else {
            ret[0] = leaf;
            ret[1] = index;
            return ret;
        }
    }

    public LinkedList search(TKey key, String operation) throws DBAppException {
        BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);
        LinkedList<Hashtable> ret = new LinkedList<>();
        int index = leaf.search(key);
        if (index == -1 && operation.equals("=")) {
            return new LinkedList<>();
        } else {
            if (index != -1) {
                if (operation.equals("=")) {
                    for (int i = index; i <= leaf.keyCount; i++) {
                        if (leaf.keys[i] != null && ((TKey) leaf.keys[i]).equals(key)) {
                            ret.add((Hashtable) leaf.getValue(i));
                        } else {
                            if (leaf.keys[i] == null && i != leaf.keyCount) {
                                leaf = null;
                                if (leaf.rightSibling != null)
                                    leaf = (BTreeLeafNode<TKey, TValue>) loadNodeData(leaf.rightSibling);
                                break;
                            }
                        }
                    }
                    if (leaf != null && leaf.rightSibling != null)
                        leaf = (BTreeLeafNode<TKey, TValue>) loadNodeData(leaf.rightSibling);
                    while (leaf != null) {
                        for (int i = 0; i <= leaf.keyCount; i++) {
                            if (leaf.keys[i] != null && ((TKey) leaf.keys[i]).equals(key)) {
                                ret.add((Hashtable) leaf.getValue(i));
                            } else {
                                if (leaf.keys[i] == null && (i != leaf.keyCount || i != 0)) {
                                    leaf = null;
                                    if (leaf.rightSibling != null)
                                        leaf = (BTreeLeafNode<TKey, TValue>) loadNodeData(leaf.rightSibling);
                                    break;
                                } else {
                                    if (!((TKey) leaf.keys[i]).equals(key)) {
                                        leaf = null;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (operation.equals("!=")) {
                        BTreeNode<TKey> node = loadNodeData(this.nodes.get(0));
                        while(!node.parentNode.equals(""))
                        {
                            this.saveNodeData(node.nodeName,node);
                            node = loadNodeData(node.parentNode);
                        }
                        while (node.getNodeType() == TreeNodeType.InnerNode) {
                            this.saveNodeData(node.nodeName,node);
                            node = ((BTreeInnerNode<TKey>) node).getChild();
                        }
                        while(node != null){
                            for(int i = 0 ; i < node.keyCount ; i++)
                            {
                                if(((TKey)node.keys[i]).compareTo(key) != 0){
                                    ret.add((Hashtable)((BTreeLeafNode<TKey, TValue>) node).getValue(i));
                                }
                            }
                            if(node != null){
                                if(!node.rightSibling.equals(""))
                                    node = loadNodeData(node.rightSibling);
                                else{
                                    node = null;
                                }
                            }
                        }
                    } else {
                        if (operation.equals("<=")) {
                            BTreeNode<TKey> node = loadNodeData(this.nodes.get(0));
                            while(!node.parentNode.equals(""))
                            {
                                this.saveNodeData(node.nodeName,node);
                                node = loadNodeData(node.parentNode);
                            }
                            while (node.getNodeType() == TreeNodeType.InnerNode) {
                                this.saveNodeData(node.nodeName,node);
                                node = ((BTreeInnerNode<TKey>) node).getChild();
                            }
                            while(node != null){
                                for(int i = 0 ; i < node.keyCount ; i++)
                                {
                                    if(((TKey)node.keys[i]).compareTo(key) <= 0){
                                        ret.add((Hashtable)((BTreeLeafNode<TKey, TValue>) node).getValue(i));
                                    }
                                    else{
                                        node = null;
                                        break;
                                    }
                                }
                                if(node != null){
                                    if(!node.rightSibling.equals(""))
                                        node = loadNodeData(node.rightSibling);
                                    else{
                                        node = null;
                                    }
                                }
                            }
                        } else {
                            if (operation.equals("<")) {
                                BTreeNode<TKey> node = loadNodeData(this.nodes.get(0));
                                while(!node.parentNode.equals(""))
                                {
                                    this.saveNodeData(node.nodeName,node);
                                    node = loadNodeData(node.parentNode);
                                }
                                while (node.getNodeType() == TreeNodeType.InnerNode) {
                                    this.saveNodeData(node.nodeName,node);
                                    node = ((BTreeInnerNode<TKey>) node).getChild();
                                }
                                while(node != null){
                                    for(int i = 0 ; i < node.keyCount ; i++)
                                    {
                                        if(((TKey)node.keys[i]).compareTo(key) < 0){
                                            ret.add((Hashtable)((BTreeLeafNode<TKey, TValue>) node).getValue(i));
                                        }
                                        else{
                                            node = null;
                                            break;
                                        }
                                    }
                                    if(node != null){
                                        if(!node.rightSibling.equals(""))
                                            node = loadNodeData(node.rightSibling);
                                        else{
                                            node = null;
                                        }
                                    }
                                }
                            } else {
                                if (operation.equals(">=")) {
                                    for (int i = index; i < leaf.keyCount; i++) {
                                        if (leaf.keys[i] != null && ((TKey) leaf.keys[i]).compareTo(key) >= 0) {
                                            ret.add((Hashtable) leaf.getValue(i));
                                        } else {
                                            if (leaf.keys[i] == null && i != leaf.keyCount) {
                                                leaf = null;
                                                if (leaf.rightSibling != null)
                                                    leaf = (BTreeLeafNode<TKey, TValue>) loadNodeData(leaf.rightSibling);
                                                break;
                                            }
                                        }
                                    }
                                    if (leaf != null && leaf.rightSibling != null)
                                        leaf = (BTreeLeafNode<TKey, TValue>) loadNodeData(leaf.rightSibling);
                                    while (leaf != null) {
                                        for (int i = 0; i < leaf.keyCount; i++) {
                                            if (leaf.keys[i] != null && ((TKey) leaf.keys[i]).compareTo(key) >= 0) {
                                                ret.add((Hashtable) leaf.getValue(i));
                                            } else {
                                                if (leaf.keys[i] == null && (i != leaf.keyCount || i != 0)) {
                                                    leaf = null;
                                                    if (leaf.rightSibling != null)
                                                        leaf = (BTreeLeafNode<TKey, TValue>) loadNodeData(leaf.rightSibling);
                                                    break;
                                                } else {
                                                    if (((TKey) leaf.keys[i]).compareTo(key) < 0) {
                                                        leaf = null;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    if (operation.equals(">")) {
                                        for (int i = index; i < leaf.keyCount; i++) {
                                            if (leaf.keys[i] != null && ((TKey) leaf.keys[i]).compareTo(key) > 0) {
                                                ret.add((Hashtable) leaf.getValue(i));
                                            } else {
                                                if (leaf.keys[i] == null && i != leaf.keyCount) {
                                                    leaf = null;
                                                    if (leaf.rightSibling != null)
                                                        leaf = (BTreeLeafNode<TKey, TValue>) loadNodeData(leaf.rightSibling);
                                                    break;
                                                }
                                            }
                                        }
                                        if (leaf != null && leaf.rightSibling != null)
                                            leaf = (BTreeLeafNode<TKey, TValue>) loadNodeData(leaf.rightSibling);
                                        while (leaf != null) {
                                            for (int i = 0; i < leaf.keyCount; i++) {
                                                if (leaf.keys[i] != null && ((TKey) leaf.keys[i]).compareTo(key) >= 0) {
                                                    ret.add((Hashtable) leaf.getValue(i));
                                                } else {
                                                    if (leaf.keys[i] == null && (i != leaf.keyCount || i != 0)) {
                                                        leaf = null;
                                                        if (leaf.rightSibling != null)
                                                            leaf = (BTreeLeafNode<TKey, TValue>) loadNodeData(leaf.rightSibling);
                                                        break;
                                                    } else {
                                                        if (!((TKey) leaf.keys[i]).equals(key)) {
                                                            leaf = null;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                if (operation.equals("!=")) {
                    BTreeNode<TKey> nodeLeft = loadNodeData(this.nodes.get(0));
                    while(!nodeLeft.parentNode.equals(""))
                    {
                        nodeLeft = loadNodeData(nodeLeft.parentNode);
                    }
                    while (nodeLeft.getNodeType() == TreeNodeType.InnerNode) {
                        nodeLeft = ((BTreeInnerNode<TKey>) nodeLeft).getChild("left");
                    }
                    while (nodeLeft != null) {
                        for (int i = 0; i < nodeLeft.keyCount; i++) {
                            if (nodeLeft.getKey(i) != null) {
                                ret.add((Hashtable) ((BTreeLeafNode) nodeLeft).getValue(i));
                            }
                        }
                        nodeLeft = loadNodeData(nodeLeft.rightSibling);
                    }
                } else {
                    if (operation.equals("<=") || operation.equals("<")) {
                        BTreeNode<TKey> nodeLeft = loadNodeData(this.nodes.get(0));
                        while(!nodeLeft.parentNode.equals(""))
                        {
                            nodeLeft = loadNodeData(nodeLeft.parentNode);
                        }
                        while (nodeLeft.getNodeType() == TreeNodeType.InnerNode) {
                            nodeLeft = ((BTreeInnerNode<TKey>) nodeLeft).getChild("left");
                        }
                        while (nodeLeft != null) {
                            for (int i = 0; i < nodeLeft.keyCount; i++) {
                                if (nodeLeft.getKey(i) != null && nodeLeft.getKey(i).compareTo(key) < 0) {
                                    ret.add((Hashtable) ((BTreeLeafNode) nodeLeft).getValue(i));
                                } else {
                                    if (nodeLeft.getKey(i).compareTo(key) > 0) {
                                        nodeLeft = null;
                                        break;
                                    }
                                }
                            }
                            if (nodeLeft != null) {
                                nodeLeft = loadNodeData(nodeLeft.rightSibling);
                            }
                        }
                    } else {
                        if (operation.equals(">=") || operation.equals(">")) {
                            BTreeNode<TKey> nodeRight = loadNodeData(this.nodes.get(0));
                            while(!nodeRight.parentNode.equals(""))
                            {
                                nodeRight = loadNodeData(nodeRight.parentNode);
                            }
                            while (nodeRight.getNodeType() == TreeNodeType.InnerNode) {
                                nodeRight = ((BTreeInnerNode<TKey>) nodeRight).getChild("right");
                            }
                            while (nodeRight != null) {
                                for (int i = nodeRight.keyCount-1; i >= 0; i--) {
                                    if (nodeRight.getKey(i) != null && nodeRight.getKey(i).compareTo(key) > 0) {
                                        ret.addFirst((Hashtable) ((BTreeLeafNode) nodeRight).getValue(i));
                                    } else {
                                        if (nodeRight.getKey(i).compareTo(key) < 0) {
                                            nodeRight = null;
                                            break;
                                        }
                                    }
                                }
                                if (nodeRight != null) {
                                    nodeRight = loadNodeData(nodeRight.rightSibling);
                                }
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    /**
     * Delete a key and its associated value from the tree.
     */
    public int delete(TKey key,TValue value) throws DBAppException {
        BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);
        Table t = readTable(tableName);
        int index = -1;
        for(int i = 0 ; leaf != null && i < leaf.keyCount ; i++){
            Hashtable val2 = ((Hashtable) leaf.getValue(i));
            Hashtable val1 = (Hashtable) value;
            boolean flag = true;
            Enumeration<String> enumeration = val1.keys();

            // iterate using enumeration object
            while(enumeration.hasMoreElements() && flag) {
                String keys = enumeration.nextElement();
                if(compare(t.colNameType.get(keys),val1.get(keys),val2.get(keys)) != 0 && !keys.equals("TouchDate")){
                    flag = false;
                }
            }
            if(compare(t.colNameType.get(colKey),leaf.keys[i],key) == 0 && flag){
                index = i;
                BTreeLeafNode<TKey, TValue> leafNode = (BTreeLeafNode<TKey, TValue>) loadNodeData(leaf.leftSibling);
                while(leafNode != null){
                    index=leafNode.keyCount+index;
                    leafNode = (BTreeLeafNode<TKey, TValue>) loadNodeData(leafNode.leftSibling);
                }
                leaf.deleteAt(i);
                break;
            }
            else{
                if(leaf.keys[i].equals(key) && i == leaf.keyCount-1){
                    saveNodeData(leaf.nodeName,leaf);
                    if(!leaf.rightSibling.equals("")){
                        leaf = (BTreeLeafNode<TKey, TValue>) loadNodeData(leaf.rightSibling);
                        i = 0;
                        if(!leaf.keys[0].equals(key))
                        {
                            saveNodeData(leaf.nodeName,leaf);
                            return -1;
                        }
                    }
                }
                else{
                    if(i == leaf.keyCount-1){
                        return -1;
                    }
                }
            }
        }
        if (leaf != null && leaf.isUnderflow()) {
            BTreeNode<TKey> n = leaf.dealUnderflow();
            if (n != null) {
                BTreeNode<TKey> node = loadNodeData(this.nodes.get(0));
                while(!node.parentNode.equals(""))
                {
                    node = loadNodeData(node.parentNode);
                }
                saveNodeData(node.nodeName,n);
            }
        }
        if(leaf == null)
            return -1;
        return index;
    }

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


    public static BTree readBPTree(String name) {
        try {
            FileInputStream fileIn = new FileInputStream("data" + "\\" + name + ".class");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            BTree b = (BTree) in.readObject();

            System.out.println(b.getbTreeName() + "       Object Deserialized");
            in.close();
            fileIn.close();

            return b;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public int delete(TKey key) throws DBAppException {
        BTreeLeafNode<TKey, TValue> leaf = this.findLeafNodeShouldContainKey(key);
        int index = leaf.search(key);
        if(index != -1){
            BTreeLeafNode<TKey, TValue> leafNode = (BTreeLeafNode<TKey, TValue>) loadNodeData(leaf.leftSibling);
            while(leafNode != null){
                System.out.println("++++++++++++++++++++++++++++++++");
                System.out.println("Current node: "+leafNode.nodeName);
                System.out.println("Current index: "+index);
                index=leafNode.keyCount+index;
                System.out.println("Updated index: "+index);
                System.out.println("Next node: "+leafNode.leftSibling);
                System.out.println("++++++++++++++++++++++++++++++++");
                leafNode = (BTreeLeafNode<TKey, TValue>) loadNodeData(leafNode.leftSibling);
            }
        }
        if (leaf.delete(key) && leaf.isUnderflow()) {
            BTreeNode<TKey> n = leaf.dealUnderflow();
            if (n != null) {
                BTreeNode<TKey> node = loadNodeData(this.nodes.get(0));
                while(!node.parentNode.equals(""))
                {
                    node = loadNodeData(node.parentNode);
                }
                saveNodeData(node.nodeName,n);
            }
        }
        return index;
    }

    public void update(TKey keyOld, TKey keyNew, TValue valueOld, TValue valueNew) throws DBAppException {
        if (delete(keyOld,valueOld)>=0) {
            insert(keyNew, valueNew);
        }
    }

    public int update(TKey keyOld, TValue valueNew) throws DBAppException {
        int index = delete(keyOld);
        if (index != -1) {
            insert(keyOld, valueNew);
        }
        return index;
    }

    /**
     * Search the leaf node which should contain the specified key
     */
    @SuppressWarnings("unchecked")
    public BTreeLeafNode<TKey, TValue> findLeafNodeShouldContainKey(TKey key) throws DBAppException {
        BTreeNode<TKey> node = loadNodeData(this.nodes.get(0));
        while(!node.parentNode.equals(""))
        {
            node = loadNodeData(node.parentNode);
        }

        while (node.getNodeType() == TreeNodeType.InnerNode) {
            node = ((BTreeInnerNode<TKey>) node).getChild(node.search(key));
        }
        this.writeBPTree();
        return (BTreeLeafNode<TKey, TValue>) node;
    }

    public BTreeNode<TKey> getRoot() {
        return root;
    }

    public String getbTreeName() {
        return bTreeName;
    }

    public String getColKey() {
        return colKey;
    }

    public void printTree() throws DBAppException {
        BTreeNode<TKey> node = loadNodeData(this.nodes.get(0));
        while(!node.parentNode.equals(""))
        {
            this.saveNodeData(node.nodeName,node);
            node = loadNodeData(node.parentNode);
        }
        while (node.getNodeType() == TreeNodeType.InnerNode) {
            this.saveNodeData(node.nodeName,node);
            node = ((BTreeInnerNode<TKey>) node).getChild();
            System.out.println(node.nodeName);
        }
        System.out.println();
        ((BTreeLeafNode) node).getValue();
        while (!node.rightSibling.equals("")) {
            this.saveNodeData(node.nodeName,node);
            System.out.println();
            node = loadNodeData(node.rightSibling);
            ((BTreeLeafNode) node).getValue();
        }
        this.saveNodeData(node.nodeName,node);
    }
    public void deletePage(String curP) {
        File file = new File("data\\" + curP);

        if (file.delete()) {
            System.out.println(curP + " deleted successfully");
        } else {
            System.out.println(curP + " Failed to delete the file");
        }

    }

    public static Table readTable(String n) {
        try {

            FileInputStream fileIn = new FileInputStream("data" + "\\" + n + ".class");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Table t = (Table) in.readObject();
            t.clusteringKey = readKey(n);
            t.colNameType = readColNameType(n);

            System.out.println(t.name + "       Object Deserialized");
            in.close();
            fileIn.close();

            return t;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String readKey(String strTableName) throws DBAppException {

        try {
            File f = new File("data\\metadata.csv");
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            while (br.ready()) {
                String s = br.readLine();
                String[] x = s.split(",");
                if (x[0].equals(strTableName) && x[3].equals("True")) {
                    return x[1];
                }

            }

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

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
                    if (!x[1].toLowerCase().equals("java.util.date")) {
                        htbl.put(x[1], x[2].toLowerCase());
                    }
                }

            }
            return htbl;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new DBAppException("PROBLEM RETRIEVING COLUMN NAME AND TYPE FROM METADATA");

    }

    public void setIndexTrue(String tblName, String colName) {

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
            fw.flush();
            fw.close();
            fr.close();

            System.out.println(f.delete());
            System.out.println(tmp.renameTo(f));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
