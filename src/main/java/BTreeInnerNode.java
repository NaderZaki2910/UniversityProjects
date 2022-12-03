
import java.io.Serializable;

class BTreeInnerNode<TKey extends Comparable<TKey>> extends BTreeNode<TKey> implements Serializable {
	protected static int INNERORDER;
	protected String[] children;
	
	public BTreeInnerNode(String btreeName) throws DBAppException {
		super(btreeName);
		INNERORDER = this.getBPTree().nodeSize;
		this.keys = new Object[INNERORDER + 1];
		this.children = new String[INNERORDER + 2];
		for(int i = 0 ; i < children.length ; i++){
			children[i] = "";
		}
		getBPTree().saveNodeData(this.nodeName, this);
	}
	
	@SuppressWarnings("unchecked")
	public BTreeNode<TKey> getChild(int index) throws DBAppException {
		BTree tree = this.getBPTree();
		if(tree.nodes.contains(this.children[index]))
		return tree.loadNodeData(this.children[index]);
		else{
			if(index == 0){
				return tree.loadNodeData(this.children[index+1]);
			}
			else{
				return tree.loadNodeData(this.children[index-1]);
			}
		}
	}
	public BTreeNode<TKey> getChild() throws DBAppException {
		int count = 0;
		BTree tree = this.getBPTree();
		while(this.children[count].equals("")){
			count++;
		}
		return (BTreeNode<TKey>)tree.loadNodeData(this.children[count]);
	}

	public BTreeNode<TKey> getChild(String side) throws DBAppException {
		BTree tree = this.getBPTree();
		if(side.toLowerCase().equals("left")){
			int count = 0;
			BTreeNode<TKey> node = this;
			while(this.children[count].equals("")){
				count++;
			}
			return (BTreeNode<TKey>)tree.loadNodeData(this.children[count]);
		}
		else{
			if(side.toLowerCase().equals("right")){
				int count = this.keyCount;
				while(this.children[count].equals("")){
					count--;
				}
				return (BTreeNode<TKey>)tree.loadNodeData(this.children[count]);
			}
			else{
				System.out.println("You entered an undefined side. Check the syntax.");
				return null;
			}
		}
	}

	public void setChild(int index, BTreeNode<TKey> child) throws DBAppException {
		if(child != null)
			this.children[index] = child.nodeName;
		if (child != null){
			child.setParent(this);
			this.getBPTree().saveNodeData(child.nodeName,child);
			this.getBPTree().saveNodeData(this.nodeName,this);
		}
		this.getBPTree().saveNodeData(this.nodeName,this);
		this.getBPTree().writeBPTree();
	}
	
	@Override
	public TreeNodeType getNodeType() {
		return TreeNodeType.InnerNode;
	}
	
	@Override
	public int search(TKey key) {
		int index;
		for (index = 0; index < this.getKeyCount(); ++index) {
			int cmp = ((TKey)this.keys[index]).compareTo(key);
			if (cmp == 0) {
				return index + 1;
			}
			else if (cmp > 0) {
				return index;
			}
		}
		
		return index;
	}
	
	
	/* The codes below are used to support insertion operation */
	
	private void insertAt(int index, TKey key, BTreeNode<TKey> leftChild, BTreeNode<TKey> rightChild) throws DBAppException {
		// move space for the new key
		for (int i = this.getKeyCount() + 1; i > index; --i) {
			this.setChild(i, this.getChild(i - 1));
		}
		for (int i = this.getKeyCount(); i > index; --i) {
			this.setKey(i, this.getKey(i - 1));
		}
		
		// insert the new key
		this.setKey(index, key);
		this.setChild(index, leftChild);
		this.setChild(index + 1, rightChild);
		this.keyCount += 1;
	}
	
	/**
	 * When splits a internal node, the middle key is kicked out and be pushed to parent node.
	 */
	@Override
	protected BTreeNode<TKey> split() throws DBAppException {
		int midIndex = this.getKeyCount() / 2;
		
		BTreeInnerNode<TKey> newRNode = new BTreeInnerNode<TKey>(this.bTree);
		for (int i = midIndex + 1; i < this.getKeyCount(); ++i) {
			newRNode.setKey(i - midIndex - 1, this.getKey(i));
			this.setKey(i, null);
		}
		for (int i = midIndex + 1; i <= this.getKeyCount(); ++i) {
			newRNode.setChild(i - midIndex - 1, this.getChild(i));
			newRNode.getChild(i - midIndex - 1).setParent(newRNode);
			this.setChild(i, null);
		}
		this.setKey(midIndex, null);
		newRNode.keyCount = this.getKeyCount() - midIndex - 1;
		this.keyCount = midIndex;
		this.getBPTree().saveNodeData(this.nodeName,this);
		this.getBPTree().saveNodeData(newRNode.nodeName,newRNode);
		this.getBPTree().writeBPTree();
		return newRNode;
	}
	
	@Override
	protected BTreeNode<TKey> pushUpKey(TKey key, BTreeNode<TKey> leftChild, BTreeNode<TKey> rightNode) throws DBAppException {
		// find the target position of the new key
		int index = this.search(key);
		
		// insert the new key
		this.insertAt(index, key, leftChild, rightNode);

		// check whether current node need to be split
		if (this.isOverflow()) {
			return this.dealOverflow();
		}
		else {
			this.getBPTree().saveNodeData(this.nodeName,this);
			return this.getParent() == null ? this : null;
		}
	}
	
	
	
	
	/* The codes below are used to support delete operation */
	
	private void deleteAt(int index) throws DBAppException {
		int i = 0;
		for (i = index; i < this.getKeyCount() - 1; ++i) {
			this.setKey(i, this.getKey(i + 1));
			this.setChild(i + 1, this.getChild(i + 2));
		}
		this.setKey(i, null);
		this.setChild(i + 1, null);
		--this.keyCount;
	}
	
	@Override
	protected void processChildrenTransfer(BTreeNode<TKey> borrower, BTreeNode<TKey> lender, int borrowIndex) throws DBAppException {
		int borrowerChildIndex = 0;
		while (borrowerChildIndex < this.getKeyCount() + 1 && this.getChild(borrowerChildIndex) != borrower)
			++borrowerChildIndex;
		
		if (borrowIndex == 0) {
			// borrow a key from right sibling
			TKey upKey = borrower.transferFromSibling(this.getKey(borrowerChildIndex), lender, borrowIndex);
			this.setKey(borrowerChildIndex, upKey);
		}
		else {
			// borrow a key from left sibling
			TKey upKey = borrower.transferFromSibling(this.getKey(borrowerChildIndex - 1), lender, borrowIndex);
			this.setKey(borrowerChildIndex - 1, upKey);
		}
	}
	
	@Override
	protected BTreeNode<TKey> processChildrenFusion(BTreeNode<TKey> leftChild, BTreeNode<TKey> rightChild) throws DBAppException {
		int index = 0;
		while (index < this.getKeyCount() && this.getChild(index) != leftChild)
			++index;
		TKey sinkKey = this.getKey(index);
		
		// merge two children and the sink key into the left child node
		leftChild.fusionWithSibling(sinkKey, rightChild);
		
		// remove the sink key, keep the left child and abandon the right child
		this.deleteAt(index);
		
		// check whether need to propagate borrow or fusion to parent
		if (this.isUnderflow()) {
			if (this.getParent() == null) {
				// current node is root, only remove keys or delete the whole root node
				if (this.getKeyCount() == 0) {
					leftChild.parentNode = "";
					return leftChild;
				}
				else {
					return null;
				}
			}
			
			return this.dealUnderflow();
		}
		
		return null;
	}
	
	
	@Override
	protected void fusionWithSibling(TKey sinkKey, BTreeNode<TKey> rightSibling) throws DBAppException {
		BTreeInnerNode<TKey> rightSiblingNode = (BTreeInnerNode<TKey>)rightSibling;
		
		int j = this.getKeyCount();
		this.setKey(j++, sinkKey);
		
		for (int i = 0; i < rightSiblingNode.getKeyCount(); ++i) {
			this.setKey(j + i, rightSiblingNode.getKey(i));
		}
		for (int i = 0; i < rightSiblingNode.getKeyCount() + 1; ++i) {
			this.setChild(j + i, rightSiblingNode.getChild(i));
		}
		this.keyCount += 1 + rightSiblingNode.getKeyCount();
		
		this.rightSibling = rightSiblingNode.rightSibling;
		BTreeNode<TKey> temp = this.getBPTree().loadNodeData(rightSiblingNode.rightSibling);
		if (temp != null){
			temp.leftSibling = this.nodeName;
			this.getBPTree().saveNodeData(temp.nodeName,temp);
		}
		this.getBPTree().saveNodeData(this.nodeName,this);
		BTree b = this.getBPTree();
		b.nodes.remove(rightSibling.nodeName);
		b.deletePage(rightSibling.nodeName);
		writeBPTree(b);
	}
	
	@Override
	protected TKey transferFromSibling(TKey sinkKey, BTreeNode<TKey> sibling, int borrowIndex) throws DBAppException {
		BTreeInnerNode<TKey> siblingNode = (BTreeInnerNode<TKey>)sibling;
		
		TKey upKey = null;
		if (borrowIndex == 0) {
			// borrow the first key from right sibling, append it to tail
			int index = this.getKeyCount();
			this.setKey(index, sinkKey);
			this.setChild(index + 1, siblingNode.getChild(borrowIndex));			
			this.keyCount += 1;
			
			upKey = siblingNode.getKey(0);
			siblingNode.deleteAt(borrowIndex);
		}
		else {
			// borrow the last key from left sibling, insert it to head
			this.insertAt(0, sinkKey, siblingNode.getChild(borrowIndex + 1), this.getChild(0));
			upKey = siblingNode.getKey(borrowIndex);
			siblingNode.deleteAt(borrowIndex);
		}
		
		return upKey;
	}
}