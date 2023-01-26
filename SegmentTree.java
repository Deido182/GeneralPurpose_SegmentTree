import static java.lang.Math.min;
import static java.lang.Math.max;
	    
class Node <T> {
	int l;
	int r;
	T value;
		
	public Node(int l, int r, T value) {
		this.l = l;
		this.r = r;
		this.value = value;
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

class SegmentTree <T> {
	Node <T>[] tree;
	T[] leaves;
	T[] pushDown;
	ComputeFunction <T> compute;
	ModifyFunction <T> modify;
	RangeFunction <T> range;
	
	public SegmentTree(T[] leaves, ComputeFunction <T> f1, ModifyFunction <T> f2, RangeFunction <T> f3) {
		this.leaves = leaves;
		this.tree = new Node[this.leaves.length * 4];
		this.pushDown = (T[]) new Object[this.leaves.length * 4];
		this.compute = f1;
		this.modify = f2;
		this.range = f3;
		this.build(0, this.leaves.length - 1, 0);
	}

	private void build(int l, int r, int index) {
		if(l == r) {
			tree[index] = new Node <T> (l, r, leaves[l]);
			return;
		}
		this.build(l, (l + r) >> 1, (index << 1) + 1);
		this.build(((l + r) >> 1) + 1, r, (index << 1) + 2);
		tree[index] = new Node <T> (l, r, compute.compute(tree[(index << 1) + 1].value, tree[(index << 1) + 2].value));
	}
		
	private void update(int index, int l, int r, T value) {
		if(l > r)
			return;
		if(tree[index] == null)
			return;
		if(tree[index].l == tree[index].r) {
			tree[index].value = leaves[tree[index].l] = modify.modify(leaves[tree[index].l], value);
			return;
		}
		if(tree[index].l == l && tree[index].r == r) {
			pushDown[index] = modify.modify(pushDown[index], value);
			tree[index].value = modify.modify(tree[index].value, range.range(value, tree[index].l, tree[index].r));
			return;
		}
		if(pushDown[index] != null) 
			pushDown(index);
		if(l <= (tree[index].l + tree[index].r) >> 1) 
			update((index << 1) + 1, l, min(r, (tree[index].l + tree[index].r) >> 1), value);
		if(r >= ((tree[index].l + tree[index].r) >> 1) + 1) 
			update((index << 1) + 2, max(l, ((tree[index].l + tree[index].r) >> 1) + 1), r, value);
		tree[index].value = compute.compute(tree[(index << 1) + 1].value, tree[(index << 1) + 2].value);
	}
	
	private void pushDown(int index) {
		update((index << 1) + 1, tree[index].l, (tree[index].l + tree[index].r) >> 1, pushDown[index]);
		update((index << 1) + 2, ((tree[index].l + tree[index].r) >> 1) + 1, tree[index].r, pushDown[index]);
		pushDown[index] = null;
	}
	
	public void update(int l, int r, T value) {
		update(0, l, r, value);
	}
	
	private T query(int index, int l, int r) {
		if(l > r)
			return null;
		if(tree[index] == null)
			return null;
		if(pushDown[index] != null) 
			pushDown(index);
		if(tree[index].l == l && tree[index].r == r)
			return tree[index].value;
		T ans = null;
		if(l <= (tree[index].l + tree[index].r) >> 1) 
			ans = compute.compute(ans, query((index << 1) + 1, l, min(r, (tree[index].l + tree[index].r) >> 1)));
		if(r >= ((tree[index].l + tree[index].r) >> 1) + 1)
			ans = compute.compute(ans, query((index << 1) + 2, max(l, ((tree[index].l + tree[index].r) >> 1) + 1), r));
		return ans;
	}
	
	public T query(int l, int r) {
		return query(0, l, r);
	}
}
