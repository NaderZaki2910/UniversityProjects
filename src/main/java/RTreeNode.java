
import java.io.*;
import java.util.Vector;

enum rTreeNodeType {
    InnerNode,
    LeafNode
}

abstract class RTreeNode<TKey extends Comparable<TKey>> implements Serializable {
    protected Object[] keys;
    protected int keyCount;
    protected String parentNode;
    protected String leftSibling;
    protected String rightSibling;
    protected String nodeName;
    protected String bTree;

    protected RTreeNode(RTree bTree) throws DBAppException {
        this.keyCount = 0;
        this.bTree = bTree.bTreeName;
        this.parentNode = "";
        this.nodeName = this.bTree+"_Node_"+ (bTree.count++) +".class";
        bTree.saveNodeData(this.nodeName, this);
        bTree.nodes.add(this.nodeName);
        this.leftSibling = "";
        this.rightSibling = "";
    }
    protected RTreeNode(String bTree) throws DBAppException {
        this.keyCount = 0;
        this.bTree = bTree;
        RTree b = readBPTree(bTree);
        this.parentNode = "";
        this.nodeName = this.bTree+"_Node_"+ (b.count++) +".class";
        b.saveNodeData(this.nodeName, this);
        b.nodes.add(this.nodeName);
        this.leftSibling = "";
        this.rightSibling = "";
        writeBPTree(b);
    }
    public RTree getBPTree() {
        return this.readBPTree(bTree);
    }

    public int getKeyCount() {
        return this.keyCount;
    }

    @SuppressWarnings("unchecked")
    public TKey getKey(int index) {
        return (TKey)this.keys[index];
    }

    public void setKey(int index, TKey key) throws DBAppException {
        System.out.println("\n\n\n"+index+"\n\n\n"+this.keys.length+"\n\n\n"+this.nodeName+"\n\n\n");
        this.keys[index] = key;
        this.getBPTree().saveNodeData(this.nodeName,this);
    }

    public RTreeNode<TKey> getParent() throws DBAppException {
        if (parentNode.equals(""))
            return null;
        return this.getBPTree().loadNodeData(parentNode);
    }

    public void setParent(RTreeNode<TKey> parent) throws DBAppException {
        this.parentNode = parent.nodeName;
        this.getBPTree().saveNodeData(this.parentNode,parent);
        this.getBPTree().saveNodeData(this.nodeName,this);
    }

    public abstract TreeNodeType getNodeType();


    /**
     * Search a key on current node, if found the key then return its position,
     * otherwise return -1 for a leaf node,
     * return the child node index which should contain the key for a internal node.
     */
    public abstract int search(TKey key);



    /* The codes below are used to support insertion operation */

    public boolean isOverflow() {
        return this.getKeyCount() == this.keys.length;
    }

    public RTreeNode<TKey> dealOverflow() throws DBAppException {
        int midIndex = this.getKeyCount() / 2;
        TKey upKey = this.getKey(midIndex);

        RTreeNode<TKey> newRNode = this.split();

        if (this.getParent() == null) {
            this.setParent(new RTreeInnerNode<TKey>(this.bTree));
        }
        newRNode.setParent(this.getParent());

        // maintain links of sibling nodes
        newRNode.setLeftSibling(this);
        if(!rightSibling.equals(""))
            newRNode.setRightSibling(this.getBPTree().loadNodeData(rightSibling));
        this.getBPTree().saveNodeData(newRNode.nodeName,newRNode);
        if (!this.rightSibling.equals("")){
            RTreeNode<TKey> node = this.getBPTree().loadNodeData(this.rightSibling);
            node.setLeftSibling(newRNode);
            this.getBPTree().saveNodeData(node.nodeName,node);
        }
        this.setRightSibling(newRNode);
        this.getBPTree().saveNodeData(this.nodeName,this);
        // push up a key to parent internal node
        return this.getParent().pushUpKey(upKey, this, newRNode);
    }

    protected abstract RTreeNode<TKey> split() throws DBAppException;

    protected abstract RTreeNode<TKey> pushUpKey(TKey key, RTreeNode<TKey> leftChild, RTreeNode<TKey> rightNode) throws DBAppException;






    /* The codes below are used to support deletion operation */

    public boolean isUnderflow() {
        return this.getKeyCount() < (this.keys.length / 2);
    }

    public boolean canLendAKey() {
        return this.getKeyCount() > (this.keys.length / 2);
    }

    public RTreeNode<TKey> getLeftSibling() throws DBAppException {
        if (!this.leftSibling.equals("") && this.getBPTree().loadNodeData(leftSibling).getParent() == this.getParent())
            return this.getBPTree().loadNodeData(leftSibling);
        return null;
    }

    public void setLeftSibling(RTreeNode<TKey> sibling) throws DBAppException {
        this.leftSibling = sibling.nodeName;
        this.getBPTree().saveNodeData(this.nodeName,this);
    }

    public RTreeNode<TKey> getRightSibling() throws DBAppException {
        if (!this.rightSibling.equals("") && this.getBPTree().loadNodeData(rightSibling).getParent() == this.getParent())
            return this.getBPTree().loadNodeData(rightSibling);
        return null;
    }

    public void setRightSibling(RTreeNode<TKey> sibling) throws DBAppException {
        this.rightSibling = sibling.nodeName;
        this.getBPTree().saveNodeData(this.nodeName,this);
    }

    public RTreeNode<TKey> dealUnderflow() throws DBAppException {
        RTree b = readBPTree(this.bTree);
        if (this.getParent() == null)
            return null;

        // try to borrow a key from sibling
        RTreeNode<TKey> leftSibling = null;
        if (!this.leftSibling.equals("")) {
            leftSibling = this.getBPTree().loadNodeData(this.leftSibling);
            if(leftSibling.canLendAKey()){
                this.getParent().processChildrenTransfer(this, leftSibling, leftSibling.getKeyCount() - 1);
                this.getBPTree().saveNodeData(this.nodeName,this);
                this.getBPTree().saveNodeData(leftSibling.nodeName,leftSibling);
                return null;
            }

        }
        b = readBPTree(b.bTreeName);
        RTreeNode<TKey> rightSibling = null;
        if (!this.rightSibling.equals("")) {
            rightSibling = this.getBPTree().loadNodeData(this.rightSibling);
            if(rightSibling.canLendAKey()){
                this.getParent().processChildrenTransfer(this, rightSibling, 0);
                b.saveNodeData(this.nodeName,this);
                b.saveNodeData(rightSibling.nodeName,rightSibling);
                return null;
            }
        }
        b = readBPTree(b.bTreeName);
        // Can not borrow a key from any sibling, then do fusion with sibling
        if (leftSibling != null) {
            RTreeNode<TKey> temp = this.getParent().processChildrenFusion(leftSibling, this);
            b.saveNodeData(leftSibling.nodeName,temp);
            b.deletePage(this.nodeName);
            b.nodes.remove(this.nodeName);
            b.writeBPTree();
            return temp;
        }
        else {
            RTreeNode<TKey> temp = this.getParent().processChildrenFusion(this, rightSibling);
            b.saveNodeData(this.nodeName,temp);
            b.deletePage(rightSibling.nodeName);
            b.nodes.remove(rightSibling.nodeName);
            b.writeBPTree();
            return temp;
        }
    }

    protected abstract void processChildrenTransfer(RTreeNode<TKey> borrower, RTreeNode<TKey> lender, int borrowIndex) throws DBAppException;

    protected abstract RTreeNode<TKey> processChildrenFusion(RTreeNode<TKey> leftChild, RTreeNode<TKey> rightChild) throws DBAppException;

    protected abstract void fusionWithSibling(TKey sinkKey, RTreeNode<TKey> rightSibling) throws DBAppException;

    protected abstract TKey transferFromSibling(TKey sinkKey, RTreeNode<TKey> sibling, int borrowIndex) throws DBAppException;

    public static Vector loadNode(String nodeName) throws DBAppException {
        FileInputStream fileIn;
        ObjectInputStream in;
        try {
            fileIn = new FileInputStream("data" + "\\" + nodeName);
            in = new ObjectInputStream(fileIn);

            Vector v = (Vector) in.readObject();

            System.out.println("Node" + nodeName + " Deserialized");
            in.close();
            fileIn.close();
            return v;
        } catch (FileNotFoundException e) {
            throw new DBAppException("problem finding/reading file");
        } catch (IOException e) {
            throw new DBAppException("IO Problem");
        } catch (ClassNotFoundException e) {
            throw new DBAppException("Class Not Found Exception");
        }
    }

    public static void saveNode(String nodeName, Vector v) throws DBAppException {
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

    public static RTree readBPTree(String name) {
        try {
            FileInputStream fileIn = new FileInputStream("data" + "\\" + name + ".class");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            RTree b = (RTree) in.readObject();

            System.out.println(b.getbTreeName() + "       Object Deserialized");
            in.close();
            fileIn.close();
            return b;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    public void writeBPTree(RTree b) throws DBAppException {
        try {
            if (new File("Data" + "\\" + this.bTree + ".class").createNewFile()) {
                System.out.println("File " + this.bTree + " created successfully");
            } else {
                System.out.println("File " + this.bTree + " not created");
            }

            try {

                FileOutputStream file = new FileOutputStream("data" + "\\" + this.bTree + ".class");
                ObjectOutputStream out = new ObjectOutputStream(file);

                // Method for serialization of object
                out.writeObject(b);

                out.close();
                file.close();

                System.out.println(this.bTree+ " Object has been serialized");
            } catch (Exception e) {
                e.printStackTrace();
                throw new DBAppException("Problem Serializing and writing to file");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}