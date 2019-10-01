import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.*;
import static java.lang.Math.*;

import static java.lang.Math.min;
import static java.lang.Math.max;

class Node <T> {
	int l;
	int r;
	TreeMap <Integer, T> values;
		
	public Node(int l, int r, T value) {
		this.l = l;
		this.r = r;
		values = new TreeMap <> ();
		values.put(0, value);
	}
	
	public T getLastValue() {
		return values.lastEntry().getValue();
	}
	
	public T getValue(int version) {
		return values.containsKey(version) ? values.get(version) : values.lowerEntry(version).getValue();
	}
	
	public void addValue(Integer version, T value) {
		values.put(version, value);
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

class SegmentTree <T> {
	int countUpdates = 0;
	Node <T>[] tree;
	Function1 <T> f1;
	Function2 <T> f2;
	
	public SegmentTree(T[] leaves, Function1 <T> f1, Function2 <T> f2) {
		this.tree = new Node[leaves.length * 4];
		this.f1 = f1;
		this.f2 = f2;
		this.build(0, leaves.length - 1, 0, leaves);
	}

	private static int right(int index) {
		return (index << 1) + 2;
	}

	private static int left(int index) {
		return (index << 1) + 1;
	}
	
	private void build(int l, int r, int index, T[] leaves) {
		if(l == r) {
			tree[index] = new Node <T> (l, r, leaves[l]);
			return;
		}
		int mean = (l + r) >> 1;
		this.build(l, mean, left(index), leaves);
		this.build(mean + 1, r, right(index), leaves);
		tree[index] = new Node <T> (l, r, f1.compute(tree[left(index)].getValue(0), 
													tree[right(index)].getValue(0)));
	}
		
	private void update(int index, int i, T value) {
		if(tree[index] == null)
			return;
		if(tree[index].l == tree[index].r && tree[index].l == i) {
			tree[index].addValue(countUpdates, f2.modify(tree[index].getLastValue(), value));
			return;
		}
		update(i <= ((tree[index].l + tree[index].r) >> 1) ? left(index) : right(index), i, value);
		tree[index].addValue(countUpdates, f1.compute(tree[left(index)].getLastValue(), tree[right(index)].getLastValue()));
	}
	
	public int update(int i, T value) {
		update(0, i, value);
		return countUpdates ++;
	}
	
	private T query(int index, int l, int r, int version) {
		if(l > r)
			return null;
		if(tree[index] == null)
			return null;
		if(tree[index].l == l && tree[index].r == r)
			return tree[index].getValue(version);
		T ans = null;
		int mean = (tree[index].l + tree[index].r) >> 1;
		if(l <= mean) 
			ans = f1.compute(ans, query(left(index), l, min(r, mean), version));
		if(r >= mean + 1)
			ans = f1.compute(ans, query(right(index), max(l, mean + 1), r, version));
		return ans;
	}
	
	public T query(int l, int r, int version) {
		return query(0, l, r, version);
	}
	
	public T query(int l, int r) {
		return query(0, l, r, Integer.MAX_VALUE);
	}
}

class Main {
	
	public static void fillSum(int curr, long s, ArrayList <Integer>[] tree, long[] w, long[] sum) {
		s += w[curr];
		sum[curr] = s;
		for(int next : tree[curr])
			fillSum(next, s, tree, w, sum);
	}
	
	public static void fillSegTree(int curr, int d, ArrayList <Integer>[] tree, long[] w, int[] depth, int[] version, SegmentTree <Long> st) {
		depth[curr] = d;
		version[curr] = st.update(d, w[curr]);
		for(int next : tree[curr])
			fillSegTree(next, d + 1, tree, w, depth, version, st);
	}
	
	public static void main(String[] args) throws IOException {
		// TESTER
		// TREE GENERATION
		final int N = (int)1E5;
		final int ROOT = 0;
		final int MAX_CHILDREN = 2;
		final int MAX_W = (int)1E3;
		ArrayList <Integer>[] tree = new ArrayList[N];
		for(int i = 0; i < N; i ++)
			tree[i] = new ArrayList <> ();
		ArrayList <Integer> connected = new ArrayList <> ();
		connected.add(ROOT);
		ArrayList <Integer> toConnect = new ArrayList <> ();
		for(int i = 0; i < N; i ++)
			if(i != ROOT)
				toConnect.add(i);
		int next = 0;
		for(int i = 0; i < connected.size(); i ++) {
			int children = new Random().nextInt(MAX_CHILDREN) + 1;
			while(next < toConnect.size() && children -- > 0) {
				tree[connected.get(i)].add(toConnect.get(next));
				connected.add(toConnect.get(next ++));
			}
		}
		long[] w = new long[N];
		for(int i = 0; i < N; i ++)
			w[i] = (long) new Random().nextInt(MAX_W);
		//
		
		long[] sum = new long[N];
		fillSum(ROOT, 0, tree, w, sum);
		
		Function1 <Long> f1 = (Long n1, Long n2) -> {
			if(n1 == null)
				return n2;
			if(n2 == null)
				return n1;
			return n1 + n2;
		};
		Function2 <Long> f2 = (Long n1, Long n2) -> {
			return n2;
		};
		int[] depth = new int[N];
		int[] version = new int[N];
		SegmentTree <Long> st = new SegmentTree <> (new Long[N], f1, f2);

		long time = System.currentTimeMillis();
		fillSegTree(ROOT, 0, tree, w, depth, version, st);
		System.out.println("TIME: " + (System.currentTimeMillis() - time) + " ms");
		
		for(int i = 0; i < N; i ++) {
			if(sum[i] != st.query(0, depth[i], version[i]))
				System.out.println("ERROR");
		}
	}
 
	public static FastReader in = new FastReader();
	public static BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));
}

class FastReader {
    BufferedReader br;
    StringTokenizer st;
 
    public FastReader() {
        br = new BufferedReader(new InputStreamReader(System.in));
    }
    
    public String next() {
        while (st == null || !st.hasMoreElements()) {
            try {
                st = new StringTokenizer(br.readLine());
            } catch (IOException  e) {
                e.printStackTrace();
            }
        }
        return st.nextToken();
    }
 
    public char nextChar() {
    	return next().charAt(0);
    }
    
    public int nextInt() {
        return Integer.parseInt(next());
    }
 
    public long nextLong() {
        return Long.parseLong(next());
    }
 
    public double nextDouble() {
        return Double.parseDouble(next());
    }
 
    public String nextLine() {
        String str = "";
        try {
            str = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }
    
    public int[] nextIntArray(int n) {
    	int[] a = new int[n];
    	for(int i = 0; i < n; i ++)
    		a[i] = nextInt();
    	return a;
    }
    
    public Integer[] nextIntegerArray(int n) {
    	Integer[] a = new Integer[n];
    	for(int i = 0; i < n; i ++)
    		a[i] = nextInt();
    	return a;
    }
    
    public long[] nextLongArray(int n) {
    	long[] a = new long[n];
    	for(int i = 0; i < n; i ++)
    		a[i] = nextLong();
    	return a;
    }
    
    public double[] nextDoubleArray(int n) {
    	double[] a = new double[n];
    	for(int i = 0; i < n; i ++)
    		a[i] = nextDouble();
    	return a;
    }
    
    public String[] nextStringArray(int n) {
    	String[] a = new String[n];
    	for(int i = 0; i < n; i ++)
    		a[i] = next();
    	return a;
    }
}