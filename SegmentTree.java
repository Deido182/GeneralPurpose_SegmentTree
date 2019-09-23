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
 
interface Function1 <T> {
	public T compute(T v1, T v2);
}
 
/*
 * Remember to check v1 == null and v2 == null
 */
 
interface Function2 <T> {
	public T modify(T v1, T v2);
}
 
interface Function3 <T> {
	public T range(T v, int firstIn, int lastIn);
}
 
class SegmentTree <T> {
 
	private static int log2(int n) {
		int log2n = 31 - Integer.numberOfLeadingZeros(n);
		return 1 << log2n == n ? log2n : log2n + 1;
	}
	
	
	Node <T>[] tree;
	T[] leaves;
	T[] pushDown;
	Function1 <T> f1;
	Function2 <T> f2;
	Function3 <T> f3;
	private int log2n;
	private int newSize;
	
	public SegmentTree(T[] leaves, Function1 <T> f1, Function2 <T> f2, Function3 <T> f3) {
		log2n = log2(leaves.length);
		int newSize = 1 << log2n;
		this.leaves = (T[]) new Object[newSize];
		for(int i = 0; i < leaves.length; i ++)
			this.leaves[i] = leaves[i];
		this.tree = new Node[newSize << 1];
		this.pushDown = (T[]) new Object[newSize << 1];
		this.f1 = f1;
		this.f2 = f2;
		this.f3 = f3;
		this.build(0, newSize - 1, 0);
	}
 
	private void build(int l, int r, int index) {
		if(l == r) {
			tree[index] = new Node <T> (l, r, leaves[l]);
			return;
		}
		this.build(l, (l + r) >> 1, (index << 1) + 1);
		this.build(((l + r) >> 1) + 1, r, (index << 1) + 2);
		tree[index] = new Node <T> (l, r, f1.compute(tree[(index << 1) + 1].value, 
													tree[(index << 1) + 2].value));
	}
		
	private void update(int index, int l, int r, T value) {
		if(l > r)
			return;
		if(tree[index] == null)
			return;
		if(tree[index].l == tree[index].r) {
			tree[index].value = leaves[tree[index].l] = f2.modify(leaves[tree[index].l], value);
			return;
		}
		if(tree[index].l == l && tree[index].r == r) {
			pushDown[index] = f2.modify(pushDown[index], value);
			tree[index].value = f2.modify(tree[index].value, f3.range(value, tree[index].l, tree[index].r));
			return;
		}
		if(pushDown[index] != null) 
			pushDown(index);
		if(l <= (tree[index].l + tree[index].r) >> 1) 
			update((index << 1) + 1, l, min(r, (tree[index].l + tree[index].r) >> 1), value);
		if(r >= ((tree[index].l + tree[index].r) >> 1) + 1) 
			update((index << 1) + 2, max(l, ((tree[index].l + tree[index].r) >> 1) + 1), r, value);
		tree[index].value = f1.compute(tree[(index << 1) + 1].value, tree[(index << 1) + 2].value);
	}
	
	private void pushDown(int index) {
		update((index << 1) + 1, tree[index].l, (tree[index].l + tree[index].r) >> 1, pushDown[index]);
		update((index << 1) + 2, ((tree[index].l + tree[index].r) >> 1) + 1, tree[index].r, pushDown[index]);
		pushDown[index] = null;
	}
	
	public void update(int l, int r, T value) {
		update(0, l, r, value);
	}
	
	public T query(int l, int r) {
		T ans = null;
		int log2h = log2(l & (~(l - 1)));
		int blockSize = 1 << log2h;
		while(l <= r) {
			if(l + blockSize - 1 <= r) {
				int index = ((1 << (log2n - log2h)) - 1) + l / blockSize;
				if(pushDown[index] != null) 
					pushDown(index);
				ans = f1.compute(ans, tree[index].value);
				l += blockSize;
				log2h = log2(l & (~(l - 1)));
				blockSize = 1 << log2h;
			} else {
				log2h --;
				blockSize >>= 1;
			}
		}
		return ans;
	}
}