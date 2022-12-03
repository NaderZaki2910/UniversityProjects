
import java.io.Serializable;
import java.util.Hashtable;

class RTreeLeafNode<TKey extends Comparable<TKey>, TValue> extends RTreeNode<TKey> implements Serializable {
    protected static int LEAFORDER;
    private Object[] values;

    public RTreeLeafNode(RTree btreeName) throws DBAppException {
        super(btreeName);
        LEAFORDER = btreeName.nodeSize;
        this.keys = new Object[LEAFORDER + 1];
        this.values = new Object[LEAFORDER + 1];
        btreeName.saveNodeData(this.nodeName, this);
    }
    public RTreeLeafNode(String btreeName) throws DBAppException {
        super(btreeName);
        LEAFORDER = this.getBPTree().nodeSize;
        this.keys = new Object[LEAFORDER + 1];
        this.values = new Object[LEAFORDER + 1];
        getBPTree().saveNodeData(this.nodeName, this);
    }

    @SuppressWarnings("unchecked")
    public TValue getValue(int index) {
        return (TValue)this.values[index];
    }
    public void getValue() {
        System.out.println("-----------------------------------");
        System.out.println(this.nodeName+" Size = "+this.keyCount);
        System.out.println("-----------------------------------");
        for(int i = 0 ; i < this.values.length ; i++)
        {
            if(this.values[i] != null){
                System.out.println(this.values[i]);
            }
        }
        System.out.println("-----------------------------------");
    }

    public void setValue(int index, TValue value) throws DBAppException {
        this.values[index] = value;
        this.getBPTree().saveNodeData(this.nodeName,this);
    }

    @Override
    public TreeNodeType getNodeType() {
        return TreeNodeType.LeafNode;
    }

    @Override
    public int search(TKey key) {
        for (int i = 0; i < this.getKeyCount(); ++i) {
            int cmp = this.getKey(i).compareTo(key);
            if (cmp == 0) {
                return i;
            }
            else if (cmp > 0) {
                return -1;
            }
        }

        return -1;
    }

    public int search(TValue value) {
        for (int i = 0; i < this.getKeyCount(); ++i) {
            int cmp = ((myPolygon)this.getKey(i)).compareTo((myPolygon)value);
            if (cmp == 0) {
                if(((myPolygon)((Hashtable)this.values[i]).get(this.getBPTree().colKey)).equalsto((myPolygon) ((Hashtable)value).get(this.getBPTree().colKey))){
                    return i;
                }
            }
            else if (cmp > 0) {
                return -1;
            }
        }

        return -1;
    }

    /* The codes below are used to support insertion operation */

    public int insertKey(TKey key, TValue value) throws DBAppException {
        int index = 0;
        while (index < this.getKeyCount() && this.getKey(index).compareTo(key) < 0)
            ++index;
        this.insertAt(index, key, value);
        this.getBPTree().saveNodeData(this.nodeName,this);
        return index;
    }

    private void insertAt(int index, TKey key, TValue value) throws DBAppException {
        // move space for the new key
        for (int i = this.getKeyCount() - 1; i >= index; --i) {
            this.setKey(i + 1, this.getKey(i));
            this.setValue(i + 1, this.getValue(i));
        }

        // insert new key and value
        this.setKey(index, key);
        this.setValue(index, value);
        ++this.keyCount;
        this.getBPTree().saveNodeData(this.nodeName,this);
    }


    /**
     * When splits a leaf node, the middle key is kept on new node and be pushed to parent node.
     */
    @Override
    protected RTreeNode<TKey> split() throws DBAppException {
        int midIndex = this.getKeyCount() / 2;

        RTreeLeafNode<TKey, TValue> newRNode = new RTreeLeafNode<TKey, TValue>(this.bTree);
        for (int i = midIndex; i < this.getKeyCount(); ++i) {
            newRNode.setKey(i - midIndex, this.getKey(i));
            newRNode.setValue(i - midIndex, this.getValue(i));
            this.setKey(i, null);
            this.setValue(i, null);
        }
        newRNode.keyCount = this.getKeyCount() - midIndex;
        this.keyCount = midIndex;
        this.getBPTree().saveNodeData(this.nodeName,this);
        this.getBPTree().saveNodeData(newRNode.nodeName,newRNode);
        return newRNode;
    }

    @Override
    protected RTreeNode<TKey> pushUpKey(TKey key, RTreeNode<TKey> leftChild, RTreeNode<TKey> rightNode) {
        throw new UnsupportedOperationException();
    }




    /* The codes below are used to support deletion operation */

    public boolean delete(TKey key) throws DBAppException {
        int index = this.search(key);
        if (index == -1)
            return false;

        this.deleteAt(index);
        this.getBPTree().saveNodeData(this.nodeName,this);
        return true;
    }

    public void deleteAt(int index) throws DBAppException {
        int i = index;
        for (i = index; i < this.getKeyCount() - 1; ++i) {
            this.setKey(i, this.getKey(i + 1));
            this.setValue(i, this.getValue(i + 1));
        }
        this.setKey(i, null);
        this.setValue(i, null);
        --this.keyCount;
        this.getBPTree().saveNodeData(this.nodeName,this);
    }

    @Override
    protected void processChildrenTransfer(RTreeNode<TKey> borrower, RTreeNode<TKey> lender, int borrowIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected RTreeNode<TKey> processChildrenFusion(RTreeNode<TKey> leftChild, RTreeNode<TKey> rightChild) {
        throw new UnsupportedOperationException();
    }

    /**
     * Notice that the key sunk from parent is be abandoned.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void fusionWithSibling(TKey sinkKey, RTreeNode<TKey> rightSibling) throws DBAppException {
        RTreeLeafNode<TKey, TValue> siblingLeaf = (RTreeLeafNode<TKey, TValue>)rightSibling;

        int j = this.getKeyCount();
        for (int i = 0; i < siblingLeaf.getKeyCount(); ++i) {
            this.setKey(j + i, siblingLeaf.getKey(i));
            this.setValue(j + i, siblingLeaf.getValue(i));
        }
        this.keyCount += siblingLeaf.getKeyCount();
        this.rightSibling = siblingLeaf.rightSibling;
        RTreeNode<TKey> temp = this.getBPTree().loadNodeData(siblingLeaf.rightSibling);
        if (temp != null){
            temp.leftSibling = this.nodeName;
            this.getBPTree().saveNodeData(siblingLeaf.rightSibling,temp);
        }
        RTree b = this.getBPTree();
        this.getBPTree().saveNodeData(this.nodeName,this);
        this.getBPTree().deletePage(rightSibling.nodeName);
        b.nodes.remove(rightSibling.nodeName);
        b.writeBPTree();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected TKey transferFromSibling(TKey sinkKey, RTreeNode<TKey> sibling, int borrowIndex) throws DBAppException {
        RTreeLeafNode<TKey, TValue> siblingNode = (RTreeLeafNode<TKey, TValue>)sibling;

        this.insertKey(siblingNode.getKey(borrowIndex), siblingNode.getValue(borrowIndex));
        siblingNode.deleteAt(borrowIndex);
        this.getBPTree().saveNodeData(this.nodeName,this);
        this.getBPTree().saveNodeData(siblingNode.nodeName,siblingNode);
        return borrowIndex == 0 ? sibling.getKey(0) : this.getKey(0);
    }
}
