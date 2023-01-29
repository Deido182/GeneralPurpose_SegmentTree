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
			
		public Node(int fi, int li, T value) {
			this.firstInPos = fi;
			this.lastInPos = li;
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
	
	private Node build(int l, int r, T[] leaves) {
		if(l == r) 
			return new Node(l, r, leaves[l]);
		Node curr = new Node(l, r, null);
		curr.leftNode = this.build(l, (l + r) >> 1, leaves);
		curr.rightNode = this.build(((l + r) >> 1) + 1, r, leaves);
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
		
	private Node update(Node oldNode, int l, int r, T value) {
		if(l > r)
			return null;
		if(oldNode == null)
			oldNode = new Node(l, r, null);
    			Node newNode = oldNode.clone();
		if(newNode.firstInPos == newNode.lastInPos) {
			newNode.value = modify.modify(newNode.value, value);
			return newNode;
		}
		if(newNode.firstInPos == l && newNode.lastInPos == r) {
			newNode.value = modify.modify(newNode.value, range.range(value, newNode.firstInPos, newNode.lastInPos));
			newNode.pushDown = modify.modify(newNode.pushDown, value);
			return newNode;
		}
		pushDown(newNode);
		if(l <= (newNode.firstInPos + newNode.lastInPos) >> 1) 
			newNode.leftNode = update(newNode.leftNode, l, min(r, (newNode.firstInPos + newNode.lastInPos) >> 1), value);
		if(r >= ((newNode.firstInPos + newNode.lastInPos) >> 1) + 1) 
			newNode.rightNode = update(newNode.rightNode, max(l, ((newNode.firstInPos + newNode.lastInPos) >> 1) + 1), r, value);
		newNode.value = compute.compute(newNode.leftNode != null ? newNode.leftNode.value : null, newNode.rightNode != null ? newNode.rightNode.value : null);
		return newNode;
	}
	
	public int update(int version, int l, int r, T value) {
		roots.add(update(roots.get(version), l, r, value));
		return lastVersion();
	}
	
	public int update(int l, int r, T value) {
		return update(lastVersion(), l, r, value);
	}
	
	private T query(Node curr, int l, int r) {
		if(l > r)
			return null;
		if(curr == null)
			return null;
		pushDown(curr);
		if(curr.firstInPos == l && curr.lastInPos == r)
			return curr.value;
		T ans = null;
		if(l <= (curr.firstInPos + curr.lastInPos) >> 1) 
			ans = compute.compute(ans, query(curr.leftNode, l, min(r, (curr.firstInPos + curr.lastInPos) >> 1)));
		if(r >= ((curr.firstInPos + curr.lastInPos) >> 1) + 1)
			ans = compute.compute(ans, query(curr.rightNode, max(l, ((curr.firstInPos + curr.lastInPos) >> 1) + 1), r));
		return ans;
	}
	
	public T query(int version, int l, int r) {
		return query(roots.get(version), l, r);
	}
	
	public T query(int l, int r) {
		return query(lastVersion(), l, r);
	}
	
	public int lastVersion() {
		return roots.size() - 1;
	}
}
