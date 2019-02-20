import java.util.Arrays;
/***********************
 *
 * WAVLTree
 *
 * An implementation of a WAVL Tree.
 * (Haupler, Sen & Tarajan ‘15)
 *
 **************************/

/* Name:  Avital Friedland
 * ID: 315877126
 * Username: avitalfried
 * 
 * Name: Anat Lukach
 * ID: 301808911
 * Username: anatlukach
 */

public class WAVLTree {
	WAVLNode root;
	
	
	/******* CONSTANTS *******/
	public final  WAVLNode EXT_NODE =  new WAVLNode();
	
	private static final int NUM_OF_OPERATIONS_SINGLE_ROTATION = 2; 
	//rotation + demote = 2
	
	private static final int NUM_OF_OPERATIONS_SINGLE_ROTATION_DELETE = NUM_OF_OPERATIONS_SINGLE_ROTATION+1; 
	// rotation + demote + promote = 3
	
	private static final int NUM_OF_OPERATIONS_DOUBLE_ROTATION = NUM_OF_OPERATIONS_SINGLE_ROTATION*2+1;
	//2 rotations + 2 demotions + promote = 5
	
	private static final int NUM_OF_OPERATIONS_DOUBLE_ROTATION_DELETE = 
												NUM_OF_OPERATIONS_SINGLE_ROTATION_DELETE*2+1 ;
	//2*(rotation+demote+promote)+demote = 7 
	
	
	//Node Types:
	private static final int[] ZeroTwoNode = {0,2};
	private static final int[] TwoZeroNode = {2,0};
	private static final int[] OneTwoNode = {1,2};
	private static final int[] TwoOneNode = {2,1};
	private static final int[] ZeroOneNode = {0,1};
	private static final int[] OneZeroNode = {1,0};
	private static final int [] OneOneNode = {1,1};
	private static final int [] TreeTwoNode = {3,2};
	private static final int [] TwoTreeNode = {2,3};
	private static final int [] ThreeOneNode = {3,1};
	private static final int [] OneThreeNode = {1,3};
	private static final int [] TwoTwoNode = {2,2};

	
	public WAVLTree () {
		this.root = EXT_NODE;
	}
	
	private void setRoot(WAVLNode node){
		this.root = node;
		node.parent = null;
	}
	
	/**
	 * public boolean empty()
	 *
	 * returns true if and only if the tree is empty
	 *
	 */
	public boolean empty() {
		return root == EXT_NODE ||root==null;
	}

	/**
	 * public String search(int k)
	 *
	 * returns the info of an item with key k if it exists in the tree
	 * otherwise, returns null
	 */
	public String search(int k)
	{
		WAVLNode x = root;
		while (x != EXT_NODE) {
			if(k ==x.getKey()) {
				return x.getValue();
			}
			else if(k<x.getKey()) {
				x = x.getLeft();
			}
			else {
				x = x.getRight();
			}
		}
		return null;
	}
	
	/**
	 * private WAVLNode position (int k)
	 * 
	 * Look for k in the tree. returns the last WAVLNode encountered 
	 */

	private WAVLNode position(int k) {  
		WAVLNode x = root;
		WAVLNode y = null;
		while (x != EXT_NODE && x!=null) {
			y= x;
			if(k ==x.key) {
				return x;
			}
			else if(k<x.key) {
				x = x.left;
			}
			else {
				x = x.right;
			}
		}
		return y; 
	}
	
	/**
	 * private WAVLNode[] routeToPosition(int k)
	 * returns an array of all the WAVLNodes encountered while looking for k in the tree.
	 * result[0].key = number of elements in result.
	 * result[1] = tree.root
	 * result[result[0].key] = position(k)
	 * 
	 */
	
	private WAVLNode[] routeToPosition(int k){ 
		int n = root.getSubtreeSize();

		WAVLNode[] result = new WAVLNode[n+1];
		int numOfElements=0;
		WAVLNode x = root;
		while(x!=EXT_NODE){
			result[numOfElements+1]=x;

			numOfElements+=1;
			if(k==x.getKey()){
				break;
			}
			if(k<x.getKey()){
				x=x.getLeft();
			}
			else{
				x=x.getRight();
			}
		}
		WAVLNode dummy = new WAVLNode(numOfElements,"dummy");
		result[0]=dummy;
		return result;
	}
	
	/**
	 * private void increaseSizesInRoute(WAVLNode[] route) 
	 * Increases the size of each WAVLNode in route by 1 
	 */
	private void increaseSizesInRoute(WAVLNode[] route){ 
		int n = route[0].getKey();
		for(int i=0; i<n;i++){
			route[i+1].size+=1;
		}
	}
	
	/**
	 * private void decreaseSizesInRoute(WAVLNode[] route)
	 * Decreases the size of each WAVLNode in route by 1 
	 */
	
	private void decreaseSizesInRoute(WAVLNode[] route){ 
		int n = route[0].getKey();
		for(int i=0; i<n;i++){
			route[i+1].size-=1;
		}
	}
	
	/**
	 * private void updateSizeOfNode(WAVLNode x)
	 * Sets x's size to: 1+size of x's right subtree+ size of x's left subtree.
	 * in case x is an EXT_NODE, sets its size to 0.
	 */
	private void updateSizeOfNode(WAVLNode x){ 
		if(x==EXT_NODE) {
			x.size = 0;
			return;
		}
		x.size = 1+ x.getRight().getSubtreeSize() + x.getLeft().getSubtreeSize();
	}
	
	/**
	 * updateSizeParentAndChildren(WAVLNode parent)
	 * calls for updateSizeOfNode on parent, parent.right, parent.left
	 */
	
	private void updateSizeParentAndChildren(WAVLNode parent){
		updateSizeOfNode(parent.getLeft());
		updateSizeOfNode(parent.getRight());
		updateSizeOfNode(parent);
	}

	/**
	   * public int insert(int k, String i)
	   *
	   * inserts an item with key k and info i to the WAVL tree.
	   * the tree must remain valid (keep its invariants).
	   * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
	   * returns -1 if an item with key k already exists in the tree.
	   */
	
	public int insert(int k, String i){

		if(this.empty()) {                                     //if empty tree just update root.
			root = new WAVLNode(k,i);
			return 1;
		}
		WAVLNode [] route = routeToPosition(k);
		WAVLNode parent = route[route[0].getKey()];
		if (parent.key == k) {     					//already in tree 
			return -1;	
		}
		WAVLNode x = new WAVLNode(k, parent, i);
		int cnt =0;
		if(!parent.isLeaf()){          //if the parent is a unary node - no need to promote.
			insertAfter(parent,x);
			increaseSizesInRoute(route);
		}
		         
		else{
			insertAfter(parent,x);
			increaseSizesInRoute(route); //first, increase all sizes of nodes visited by 1. in case of rotation, the change isn't final
			promote(parent);
			cnt = 1+ rebalanceAfterInsert(parent,cnt); 							
		}
		return cnt;	
	}
	
	/**
	 * private void insertAfter(WAVLNode parent, WAVLNode child)
	 * Inserts child after parent - to the right if child.key>parent.key and to the left otherwise.
	 * Only called when parent!=null and child!=null.
	 */
	
	private void insertAfter(WAVLNode parent, WAVLNode child){
		int k = child.key;
		if (k>parent.key) {
			parent.right = child;
			child.parent =parent;
		}
		if (k<parent.key) {
			parent.left = child;
			child.parent =parent;
		}
	}
	
	/**
	 * private int rebalanceAfterInsert(WAVLNode x, int cnt)
	 * Differentiate between the rebalancing cases based on nodeType(x).
	 * Returns the number of rebalancing operations performed .
	 */
	
	private int rebalanceAfterInsert(WAVLNode x, int cnt){
		WAVLNode z = x.getParent();
		if(z==null){
			return cnt;
		}
		if(equals(nodeType(z),ZeroOneNode)||equals(nodeType(z),OneZeroNode)){ //case 1&2
			promote(z);
			cnt+=1;
			cnt = rebalanceAfterInsert(z,cnt);	
		}
		if(equals(nodeType(z),ZeroTwoNode)&&equals(nodeType(x),OneTwoNode)){ //case 3
			rotateRight(x);
			return cnt+NUM_OF_OPERATIONS_SINGLE_ROTATION;
		}
		if(equals(nodeType(z),ZeroTwoNode)&&equals(nodeType(x),TwoOneNode)){ // case 4 
			WAVLNode r = x.getRight();
			rotateLeft(r);
			rotateRight(r);
			promote(r);              
			return cnt+NUM_OF_OPERATIONS_DOUBLE_ROTATION;
		}
		if(equals(nodeType(z),TwoZeroNode)&&equals(nodeType(x),OneTwoNode)){// case 5
			WAVLNode l = x.getLeft();
			rotateRight(l);
			rotateLeft(l);
			promote(l);         
			return cnt+NUM_OF_OPERATIONS_DOUBLE_ROTATION;
		}
		if(equals(nodeType(z),TwoZeroNode)&&equals(nodeType(x),TwoOneNode)){// case 6
			rotateLeft(x);
			return cnt+NUM_OF_OPERATIONS_SINGLE_ROTATION;
		}
		else{
			return cnt;
		}
	}
	
	/**
	 * private void rotateRight(WAVLNode x)
	 * Rotation to the right ##after insertion## (rotation+demote(x.parent)+size update)
	 */
	
	private void rotateRight(WAVLNode x){
		WAVLNode z = x.getParent();
		WAVLNode y = z.getParent();
		WAVLNode r = x.getRight();
		setParentAfterRotation(y,z,x);
		z.parent =x;
		x.right =z; 
		z.left = r;
		if(r!= EXT_NODE) {r.parent = z;}
		demote(z);
		updateSizeParentAndChildren(x);
	}
	
	/**
	 * private void rotateLeft(WAVLNode x)
	 * Rotation to the left ##after insertion## (rotation+demote(x.parent)+size update)
	 */
	private void rotateLeft(WAVLNode x){
		WAVLNode z = x.getParent();
		WAVLNode y = z.getParent();
		WAVLNode L = x.getLeft();
		setParentAfterRotation(y,z,x);          
		z.parent =x;
		x.left =z;
		z.right = L;
		if(L!= EXT_NODE) {L.parent = z;}
		demote(z);
		updateSizeParentAndChildren(x);
	}
	
	/**
	 * private void rotateRightDelete(WAVLNode x)
	 * Rotation to the right ##after delete## - rotateRight(x) and promote(x)
	 */
	
	private void rotateRightDelete(WAVLNode x){
		promote(x);
		rotateRight(x);
	}
	
	/**
	 * private void rotateLeftDelete(WAVLNode x)
	 * Rotation to the right ##after delete## - rotateLeft(x) and promote(x)
	 */
	
	private void rotateLeftDelete(WAVLNode x){
		promote(x);
		rotateLeft(x);
	}
	/**
	 * private void setParentAfterRotation(WAVLNode grandp, WAVLNode parent,WAVLNode child)
	 * Sets WAVLNode child to be son of WAVLNode grandp. if grandp is null, sets child to tree.root
	 */
	private void setParentAfterRotation(WAVLNode grandp, WAVLNode parent,WAVLNode child){ 
		if(grandp==null){
			setRoot(child);
			return;
		}
		else{
			if(parent.isRightChild()){
				grandp.right = child;
				child.parent = grandp;
			}
			else{
				grandp.left = child;
				child.parent = grandp;
			}
		}
	}
	
	/**
	 * private boolean equals(int[] arr1, int[] arr2)
	 * returns true if the arr1 and arr2 are the same.
	 */

	private boolean equals(int[] arr1, int[] arr2) {
		if (Arrays.equals(arr1, arr2))
		return true;
		else return false;
	}
	
	/**
	 * public int delete(int k)
	 *
	 * deletes an item with key k from the binary tree, if it is there;
	 * the tree must remain valid (keep its invariants).
	 * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
	 * returns -1 if an item with key k was not found in the tree.
	 */
	
	public int delete(int k){
		//Step 1: By the end of step 1, nodeToDelete is either a leaf or a unary node********************//
		WAVLNode[] route;
		WAVLNode nodeToDelete = position(k);
		if (nodeToDelete ==null || nodeToDelete.key != k ) { 
			return -1;
		}
		if (!nodeToDelete.isLeaf()&&!nodeToDelete.isUnaryNode()) {           //need to replace with successor => either a leaf or a unary node
			WAVLNode nodeNext = successor(nodeToDelete);
			route = routeToPosition(nodeNext.getKey());
			swap(nodeToDelete, nodeNext);
		}
		else{
			route = routeToPosition(nodeToDelete.key);
		}
		
		//Step 2: By the end of step 2 - nodeToDelete no longer in tree**********************************//
		WAVLNode parentNodeToDelete = nodeToDelete.getParent();
		boolean leaf = nodeToDelete.isLeaf();
		boolean rightChild = nodeToDelete.isRightChild();
		int cnt =0;
		int deleteCaseNum = caseClassificationForInitialDeletion(parentNodeToDelete,leaf,rightChild);
		naiveDeletion(nodeToDelete,leaf,route);
		
		//Step 3: Call for rebalance based on case classification***************************************//
		if (deleteCaseNum==1){
			return cnt;
		}
		if (deleteCaseNum==2){
			demote(parentNodeToDelete);
			cnt+=1;
			if(isRoot(parentNodeToDelete)){
				return cnt;
			}
			else{
				return cnt+rebalanceAfterDelete(parentNodeToDelete.getParent(),cnt);
			}
		}
		else{
			return cnt+rebalanceAfterDelete(parentNodeToDelete,cnt);
		}
	}
	
	/**
	 * private int rebalanceAfterDelete(WAVLNode node, int cnt)
	 * Differentiate between the rebalancing steps required based on rank differences of node.
	 * Returns the number of rebalance operations performed. 
	 */
	
	private int rebalanceAfterDelete(WAVLNode node, int cnt){
		int [] nodeType = nodeType(node);
		int [] rightNodeType = nodeType(node.getRight());
		int [] leftNodeType = nodeType(node.getLeft());
		if(equals(nodeType, TreeTwoNode) ||equals(nodeType, TwoTreeNode)){
			demote(node);
			cnt+=1;
			if(isRoot(node)){
				return cnt;
			}
			else{
				cnt = rebalanceAfterDelete(node.getParent(),cnt);
			}
		}
		if(equals(nodeType, ThreeOneNode) && equals(rightNodeType,TwoTwoNode)){
			demote(node);
			demote(node.getRight());
			cnt+=2;
			if(isRoot(node)){
				return cnt;
			}
			else{
				rebalanceAfterDelete(node.getParent(),cnt);
			}
		}
		if(equals(nodeType, OneThreeNode) && equals(leftNodeType,TwoTwoNode)){
			demote(node);
			demote(node.getLeft());
			cnt+=2;
			if(isRoot(node)){
				return cnt;
			}
			else{
				rebalanceAfterDelete(node.getParent(),cnt);
			}
		}
		if(equals(nodeType, ThreeOneNode) && equals(rightNodeType,OneOneNode)){
			rotateLeftDelete(node.getRight());
			return cnt + NUM_OF_OPERATIONS_SINGLE_ROTATION_DELETE;
		}
		if(equals(nodeType, OneThreeNode) && equals(leftNodeType,OneOneNode)){
			rotateRightDelete(node.getLeft());
			return cnt + NUM_OF_OPERATIONS_SINGLE_ROTATION_DELETE;
		}
		if(equals(nodeType, ThreeOneNode) && equals(rightNodeType,TwoOneNode)){
			rotateLeftDelete(node.getRight());
			if(isTwoTwoLeaf(node)){
				demote(node);
				cnt+=1;
			}
			return cnt + NUM_OF_OPERATIONS_SINGLE_ROTATION_DELETE;
		}
		if(equals(nodeType, OneThreeNode) && equals(leftNodeType,OneTwoNode)){
			rotateRightDelete(node.getLeft());
			if(isTwoTwoLeaf(node)){
				demote(node);
				cnt+=1;
			}
			return cnt + NUM_OF_OPERATIONS_SINGLE_ROTATION_DELETE;
		}
		if(equals(nodeType, ThreeOneNode) && equals(rightNodeType,OneTwoNode)){
			WAVLNode a = node.getRight().getLeft();
			rotateRightDelete(a);
			rotateLeftDelete(a);
			demote(node);
			return cnt + NUM_OF_OPERATIONS_DOUBLE_ROTATION_DELETE;
		}
		if(equals(nodeType, OneThreeNode) && equals(leftNodeType,TwoOneNode)){
			WAVLNode a = node.getLeft().getRight();
			rotateLeftDelete(a);
			rotateRightDelete(a);
			demote(node);
			return cnt + NUM_OF_OPERATIONS_DOUBLE_ROTATION_DELETE;
		}
		else{
			return cnt;
		}
	}
	
	/**
	 * private boolean isTwoTwoLeaf(WAVLNode node)
	 * Returns true if a node is a leaf with rank=1
	 */
	
	private boolean isTwoTwoLeaf(WAVLNode node){
		return (equals(nodeType(node),TwoTwoNode)&&node.isLeaf());
	}
	
	/**
	 * naiveDeletion(WAVLNode nodeToDelete, boolean leaf, WAVLNode[] route)
	 * Decreases the size of each WAVLNode in route by 1 and removes nodeToDelete from the tree.
	 * leaf == true iff nodeToDelete is a leaf.
	 */
	
	private void naiveDeletion(WAVLNode nodeToDelete, boolean leaf, WAVLNode[] route){
		decreaseSizesInRoute(route);
		if(leaf==true){
			deleteLeaf(nodeToDelete);
		}
		else{
			deleteUnary(nodeToDelete);
		}
	}
	
	/**
	 * private int caseClassificationForInitialDeletion(WAVLNode nodeParent, boolean leaf, boolean rightChild)
	 * Returns 1,2 or 3 as an indication of how to continue with deletion:
	 * 1 - No further rebalancing operations are needed.
	 * 2 - Demote nodeParent and move up the problem.
	 * 3 - Conduct rebalancing steps (no rolling up the problem). 
	 */
	private int caseClassificationForInitialDeletion(WAVLNode nodeParent, boolean leaf, boolean rightChild){
		int[] cases ={1,2,3};
		int[] parentType = nodeType(nodeParent);
		if(equals(parentType, OneOneNode) 
				|| (leaf==false&&rightChild==false&&equals(parentType, OneTwoNode)) 
				|| (leaf==false&&rightChild==true&&equals(parentType, TwoOneNode))){
			return cases[0];
		}
		if(leaf==true && ((rightChild==false&&equals(parentType, OneTwoNode)) 
				|| (rightChild==true&&equals(parentType, TwoOneNode)))){
			return cases[1];
		}
		return cases[2];
	}
	
	/**
	 * private boolean isRoot(WAVLNode node)
	 * returns true if this.root==node
	 */
	
	private boolean isRoot(WAVLNode node){
		return this.root==node;
	}
	

	
	/**
	 * private void swapParentAndChild(WAVLNode parent, WAVLNode child)
	 * Swaps between parent parent.right
	 */
		
	private void swapParentAndChild(WAVLNode parent, WAVLNode child) {  //always swapping RIGHT child and parent
		WAVLNode grandp= parent.getParent();
		setParentAfterRotation(grandp, parent, child);
		WAVLNode parentLeft =parent.getLeft();
 		WAVLNode childLeft = child.getLeft();  
		WAVLNode childRight = child.getRight();  
		
		
		if(!childLeft.isExtLeaf()){ //update children about their new parents only if they aren't EXT_LEAF
			childLeft.parent = parent;
		}
		if(!childRight.isExtLeaf()){
			childRight.parent = parent;
		}
		if(!parentLeft.isExtLeaf()){
			parentLeft.parent = child;
		}
		child.left= parentLeft;
		child.right= parent;
		parent.parent = child; 
		parent.right=childRight;
		parent.left = childLeft;
		if(!parent.left.isExtLeaf()){
			parent.left= childLeft;
		}
		parent.right = childRight;

	}
	
	/**
	 * private void switchSizesAndRanks(WAVLNode z, WAVLNode y)
	 * Sets z's rank and size to be y's rank and size, and y's rank and size to be z's.
	 */
	
	private void switchSizesAndRanks(WAVLNode z, WAVLNode y){
		int tempZRank = z.getRank();
		z.rank = y.getRank();
		y.rank = tempZRank;
		int tempSizeZ = z.getSubtreeSize();
		z.size = y.getSubtreeSize();
		y.size = tempSizeZ;
		
	}
	

	/**
	 * public void swap(WAVLNode z, WAVLNode y)
	 * Swaps z with y in the tree. 
	 */
	
	private void swap(WAVLNode z, WAVLNode y){ 
		/*swap ranks and sizes*/
		switchSizesAndRanks(z,y);
		
		/*create temp pointers*/
		WAVLNode zParent = z.getParent();  
		WAVLNode zLeft = z.getLeft(); 
		WAVLNode zRight = z.getRight();
		WAVLNode yParent = y.getParent(); 
		WAVLNode yLeft = y.getLeft();
		WAVLNode yRight = y.getRight();
		boolean zIsRightChild = z.isRightChild();
		boolean yIsRightChild = y.isRightChild();
		
		/*extreme case#1: y&z are parent and child*/
		if(z.right == y) {
			swapParentAndChild(z,y);
			return;
		}
		/*update children about their new parents - only if they aren't EXT_NODES*/
		if(!yRight.isExtLeaf()){
			yRight.parent = z;
		}
		if(!yLeft.isExtLeaf()){
			yLeft.parent =z;
		}
		if(!zRight.isExtLeaf()){
			zRight.parent =y;
		}
		if(!zLeft.isExtLeaf()){
			zLeft.parent =y;
		}
				
		/*update z and y about their new children*/
		z.right = yRight;
		z.left = yLeft;
		y.right = zRight;
		y.left = zLeft;
		
		/*update z and y about their new parents, and the parents about their new kids*/
		z.parent = yParent;
		y.parent = zParent;
		if(yIsRightChild){
			yParent.right = z;
		}
		else{
			yParent.left = z;
		}
		if(zParent==null){
			setRoot(y);
		}
		else{
			if(zIsRightChild){
				zParent.right = y;
			}
			else{
				zParent.left = y;
			}
		}
	}
	
	/**
	 * private void deleteLeaf(WAVLNode node)
	 * Removes node from tree. Called only when node is a leaf
	 */
	private void deleteLeaf(WAVLNode node) {
		if(this.root == node){ //this is the only node in the tree
			this.root = EXT_NODE;
		}
		else{
			WAVLNode parent = node.parent;
			if(node.isRightChild()){
				parent.right = EXT_NODE;
			}
			else{
				parent.left = EXT_NODE;
			}
		}
	}
	
	/**
	 * private void deleteUnary(WAVLNode node)
	 * Removes node from tree. Called only when node is Unary.
	 */
	
	private void deleteUnary(WAVLNode node){
		if(isRoot(node)){
			if(node.right.isExtLeaf()){
				setRoot(node.left);
			}else{
				setRoot(node.right);
			}
		}
		WAVLNode parent = node.parent;
		if(node.isRightChild()&&node.left.isExtLeaf()){
			parent.right= node.right;
			node.right.parent = parent;
		}
		if(node.isRightChild()&&node.right.isExtLeaf()){
			parent.right = node.left;
			node.left.parent = parent;
		}
		if(node.isLeftChild()&&node.left.isExtLeaf()){
			parent.left = node.right;
			node.right.parent = parent;
		}
		if(node.isLeftChild()&&node.right.isExtLeaf()){
			parent.left = node.left;
			node.left.parent = parent;
		}
	}
	
	/**
	 * private WAVLNode successor(WAVLNode x) 
	 * Returns the successor of x
	 */
	private WAVLNode successor(WAVLNode x) { 
		if(x.right != EXT_NODE) {
			return minNodeOfSubtree(x.right); //exactly the same as min- only returns a node and not info.
		}
		WAVLNode y = x.getParent();
		while(y !=EXT_NODE && x == y.getRight()) {
			x = y;
			y = x.parent;
		}
		return y;
	}
	
	/**
	 * private WAVLNode minNodeOfSubtree(WAVLNode x)
	 * returns the minimal key in the subtree of x.
	 * Not called on an empty tree
	 */

	private WAVLNode minNodeOfSubtree(WAVLNode x) {
		while (x.left != EXT_NODE) {
			x = x.left;
		}
		return x; 
	}
	/**
	 * public String min()
	 *
	 * Returns the info of the item with the smallest key in the tree,
	 * or null if the tree is empty
	 */
	public String min()
	{
		if (this.empty()) {
			return null;
		}
		WAVLNode x = root;
		while (x.left != EXT_NODE) {
			x = x.left;
		}
		return x.info; 
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree,
	 * or null if the tree is empty
	 */
	public String max()  {
		if (this.empty()) {
			return null;
		}
		WAVLNode x = root;
		while (x.right != EXT_NODE) {
			x = x.right;
		}
		return x.info; 
	}
	
	public WAVLNode minNode()   //exactly the same as min, just returns a node (works on a given WAVLTree)
	{
		if (this.empty()) {
			return null;
		}
		WAVLNode x = root;
		while (x.left != EXT_NODE) {
			x = x.left;
		}
		return x; 
	}

	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree,
	 * or an empty array if the tree is empty.
	 */
	
	
	// the algorithm: start with min node in tree, call n times to Successor and write to array.
	// there's possibly a better implementation (like in-order)
	public int[] keysToArray()
	{
		int[] emptyArr = {};
		if (this.empty()) {
			return emptyArr;
		}
		int[] retArr = new int[root.size];
		WAVLNode prev = minNode();
		retArr[0] = prev.key;
		
		for (int i = 1; i < retArr.length; i++) {
			WAVLNode next =successor(prev); 
			retArr[i] = next.key;
			 prev = next;
		}
		return retArr;
	}


		/**
		 * public String[] infoToArray()
		 *
		 * Returns an array which contains all info in the tree,
		 * sorted by their respective keys,
		 * or an empty array if the tree is empty.
		 */
	
	//same as keysToArray
		public String[] infoToArray()
		{
			String[] emptyArr = {};
			if (this.empty()) {
				return emptyArr;
			}
				String[] retArr = new String[root.size];
				WAVLNode prev = minNode();
				retArr[0] = prev.info;
				
				for (int i = 1; i < retArr.length; i++) {
					WAVLNode next =successor(prev); 
					retArr[i] = next.info;
					 prev = next;
				}
				return retArr;
			}

		/**
		 * public int size()
		 *
		 * Returns the number of nodes in the tree.
		 *
		 */
		public int size()
		{
			return root.size; 
		}

		/**
		 * public WAVLNode getRoot()
		 *
		 * Returns the root WAVL node, or null if the tree is empty
		 *
		 */
		public WAVLNode getRoot()
		{
			return root;
		}
		
		/**
		 * public int select(int i)
		 *
		 * Returns the info of the i'th smallest key (return -1 if tree is empty)
		 * Example 1: select(1) returns the info of the node with minimal key 
		 * Example 2: select(size()) returns the info of the node with maximal key 
		 * Example 3: select(2) returns the info 2nd smallest minimal node, i.e the info of the node minimal node's successor  
		 *
		 */   
		public String select(int i)
		{
			if (this.empty() || i > root.size) {
				return null;
			}
			WAVLNode x = root;
			return selectNode(x,i); 
		}

		public String selectNode(WAVLNode x, int i) {
			int r = x.getLeft().getSubtreeSize();
			if (i==r+1) {
				return x.info;
			}
			else if(i<r+1) {
				return selectNode(x.left, i);
			}
			else {
				return selectNode(x.right, i-r-1);
			}
		}
		
		public void promote(WAVLNode x) {
			x.rank ++;
		}
		
		public void demote(WAVLNode x) {
			x.rank --;
		}
		
		public int[] nodeType(WAVLNode x) { //returns what type of Node x is, a 2:2 type node will return {2,2}
			int[] ans = new int[2];
			/*
			 * Extreme case#1: Deleting a root and root is a leaf/unary
			 * Extreme case#2: Calling nodeType on EXT_NODE - could happen during deletion:
			 * ask for node type of child and child is EXT
			 */
			if(x==null || x==EXT_NODE){ //could happen when we want to the delete the root and root is a leaf/unary
				ans[0]=1;
				ans[1]=1;
			}
			else{
				ans[0] = x.getRank() - x.getLeft().getRank();
				ans[1] = x.getRank() - x.getRight().getRank();
			}
			return ans;
		}
		
		
		/****************************
		 		public class WAVLNode
		 *****************************/

		public class WAVLNode{
			private int key;
			private int size; 
			private WAVLNode right;
			private WAVLNode left;
			private WAVLNode parent; 
			private String info;
			private int rank;
			
			public WAVLNode() {

				this(-1,null,null,0,null,null);
				rank = -1;	

			}
			
			public WAVLNode (int key, String info) { 
				this(key, null, info,1, EXT_NODE, EXT_NODE);
			}

			public WAVLNode (int key, WAVLNode parent, String info) {
				this(key, parent, info, 1, EXT_NODE, EXT_NODE);
			}
			
			public WAVLNode (int key, WAVLNode parent,int size, String info) {
				this(key, parent, info, size, EXT_NODE, EXT_NODE);
			}
			
			public WAVLNode (int key, WAVLNode parent, String info, int size, WAVLNode left, WAVLNode right) {
				this.key = key;
				this.parent = parent;
				this.info = info;
				this.size = size;
				this.left = left;
				this.right = right;
			}
			
			public int getKey()
			{
				return key; 
			}
			
			public WAVLNode getParent()
			{
				return parent; 
			}
			
			public String getValue()
			{
				return info; 
			}
			
			public WAVLNode getLeft() 
			{

				return left; 
			}
			
			public WAVLNode getRight() 
			{ 

				return right; 
			}
			
			public boolean isInnerNode()
			{
				if (this==EXT_NODE) {
					return false;
				}
				return true;
			}

			public boolean isUnaryNode() {
				if(left.rank == -1 ^ right.rank == -1) {
					return true; 
				}
				return false;
			}

			public boolean isLeaf() {
				if(left==EXT_NODE  && right== EXT_NODE) {
					return true; 
				}
				return false;
			}
			
			public boolean isExtLeaf(){
				return (this==EXT_NODE);
			}

			public int getSubtreeSize() 
			{
				if(this==EXT_NODE){
					this.size = 0;
					return 0;
				}
				return size; 
			}
			
			public boolean isRightChild() {
				if(this.getParent()==null){
					return false;
				}
				return this.getParent().right == this;
			}
			
			public boolean isLeftChild() {
				if(this.getParent()==null){
					return false;
				}
				return this.getParent().left == this;
			}
			
			public int getRank()
			{
				return rank; 
			}

		}

		}

		
		

