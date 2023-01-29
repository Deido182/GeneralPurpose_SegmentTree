import static java.lang.Math.min;
import static java.lang.Math.max;

class SegmentTree <T> {
	class Node {
		int firstInPos;
		int lastInPos;
		T value;
		T pushDown;
		Node leftNode;
		Node rightNode;
			
		public Node(int firstIn, int lastIn, T value) {
			this.firstInPos = firstIn;
			this.lastInPos = lastIn;
			this.value = value;
		}
		
		public Node clone() {
			Node node = new Node(firstInPos, lastInPos, value);
			node.leftNode = leftNode;
			node.rightNode = rightNode;
			node.pushDown = pushDown;
			return node;
		}
		
		@Override
		public String toString() {
			return "( [" + firstInPos + ", " + lastInPos + "] | value: " + value + ", pushDown: " + pushDown + " )";
		}
	}

	/*
	 * Remember to check v1 == null and v2 == null
	 */

	interface ComputeFunction <T> {
		public T compute(T v1, T v2);
	}

	/*
	 * Remember to check v1 == null and v2 == null
	 */

	interface ModifyFunction <T> {
		public T modify(T v1, T v2);
	}

	interface RangeFunction <T> {
		public T range(T v, int firstIn, int lastIn);
	}
	
	public final int FIRST_VERSION = 0;
	
	List<Node> roots;
	ComputeFunction <T> compute;
	ModifyFunction <T> modify;
	RangeFunction <T> range;
	
	public SegmentTree(T[] leaves, ComputeFunction <T> compute, ModifyFunction <T> modify, RangeFunction <T> range) {
		final int N = leaves.length;
		this.compute = compute;
		this.modify = modify;
		this.range = range;
		roots = new ArrayList <> ();
		roots.add(build(0, N - 1, leaves));
	}
	
	private Node build(int firstIn, int lastIn, T[] leaves) {
		if(firstIn == lastIn) 
			return new Node(firstIn, lastIn, leaves[firstIn]);
		Node curr = new Node(firstIn, lastIn, null);
		curr.leftNode = this.build(firstIn, (firstIn + lastIn) >> 1, leaves);
		curr.rightNode = this.build(((firstIn + lastIn) >> 1) + 1, lastIn, leaves);
		curr.value = compute.compute(curr.leftNode.value, curr.rightNode.value);
		return curr;
	}
	
	private void pushDown(Node node) {
		if(node.pushDown == null)
			return;
		node.leftNode = update(node.leftNode, node.firstInPos, (node.firstInPos + node.lastInPos) >> 1, node.pushDown);
		node.rightNode = update(node.rightNode, ((node.firstInPos + node.lastInPos) >> 1) + 1, node.lastInPos, node.pushDown);
		node.pushDown = null;
	}
		
	private Node update(Node oldNode, int firstIn, int lastIn, T value) {
		if(firstIn > lastIn)
			return null;
		if(oldNode == null)
			oldNode = new Node(firstIn, lastIn, null);
    			Node newNode = oldNode.clone();
		if(newNode.firstInPos == newNode.lastInPos) {
			newNode.value = modify.modify(newNode.value, value);
			return newNode;
		}
		if(newNode.firstInPos == firstIn && newNode.lastInPos == lastIn) {
			newNode.value = modify.modify(newNode.value, range.range(value, newNode.firstInPos, newNode.lastInPos));
			newNode.pushDown = modify.modify(newNode.pushDown, value);
			return newNode;
		}
		pushDown(newNode);
		if(firstIn <= (newNode.firstInPos + newNode.lastInPos) >> 1) 
			newNode.leftNode = update(newNode.leftNode, firstIn, min(lastIn, (newNode.firstInPos + newNode.lastInPos) >> 1), value);
		if(lastIn >= ((newNode.firstInPos + newNode.lastInPos) >> 1) + 1) 
			newNode.rightNode = update(newNode.rightNode, max(firstIn, ((newNode.firstInPos + newNode.lastInPos) >> 1) + 1), lastIn, value);
		newNode.value = compute.compute(newNode.leftNode != null ? newNode.leftNode.value : null, newNode.rightNode != null ? newNode.rightNode.value : null);
		return newNode;
	}
	
	public int update(int version, int firstIn, int lastIn, T value) {
		roots.add(update(roots.get(version), firstIn, lastIn, value));
		return lastVersion();
	}
	
	public int update(int firstIn, int lastIn, T value) {
		return update(lastVersion(), firstIn, lastIn, value);
	}
	
	private T query(Node curr, int firstIn, int lastIn) {
		if(firstIn > lastIn)
			return null;
		if(curr == null)
			return null;
		pushDown(curr);
		if(curr.firstInPos == firstIn && curr.lastInPos == lastIn)
			return curr.value;
		T ans = null;
		if(firstIn <= (curr.firstInPos + curr.lastInPos) >> 1) 
			ans = compute.compute(ans, query(curr.leftNode, firstIn, min(lastIn, (curr.firstInPos + curr.lastInPos) >> 1)));
		if(lastIn >= ((curr.firstInPos + curr.lastInPos) >> 1) + 1)
			ans = compute.compute(ans, query(curr.rightNode, max(firstIn, ((curr.firstInPos + curr.lastInPos) >> 1) + 1), lastIn));
		return ans;
	}
	
	public T query(int version, int firstIn, int lastIn) {
		return query(roots.get(version), firstIn, lastIn);
	}
	
	public T query(int firstIn, int lastIn) {
		return query(lastVersion(), firstIn, lastIn);
	}
	
	public int lastVersion() {
		return roots.size() - 1;
	}
}
