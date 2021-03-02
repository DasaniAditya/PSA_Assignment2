

/**
 * Original code:
 * Copyright © 2000–2017, Robert Sedgewick and Kevin Wayne.
 * <p>
 * Modifications:
 * Copyright (c) 2017. Phasmid Software
 */
package edu.neu.coe.info6205.union_find;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * Height-weighted Quick Union with Path Compression
 */
public class WeightedUnionAlternative implements UF {
	/**
	 * Ensure that site p is connected to site q,
	 *
	 * @param p the integer representing one site
	 * @param q the integer representing the other site
	 */
	public void connect(int p, int q) {
		if (!isConnected(p, q)) union(p, q);
	}

	/**
	 * Initializes an empty union–find data structure with {@code n} sites
	 * {@code 0} through {@code n-1}. Each site is initially in its own
	 * component.
	 *
	 * @param n               the number of sites
	 * @param pathCompression whether to use path compression
	 * @throws IllegalArgumentException if {@code n < 0}
	 */
	public WeightedUnionAlternative(int n, boolean pathCompression) {
		count = n;
		parent = new int[n];
		height = new int[n];
		for (int i = 0; i < n; i++) {
			parent[i] = i;
			height[i] = 1;
		}
		this.pathCompression = pathCompression;
	}

	/**
	 * Initializes an empty union–find data structure with {@code n} sites
	 * {@code 0} through {@code n-1}. Each site is initially in its own
	 * component.
	 * This data structure uses path compression
	 *
	 * @param n the number of sites
	 * @throws IllegalArgumentException if {@code n < 0}
	 */
	public WeightedUnionAlternative(int n) {
		this(n, true);
	}

	public void show() {
		for (int i = 0; i < parent.length; i++) {
			System.out.printf("%d: %d, %d\n", i, parent[i], height[i]);
		}
	}

	/**
	 * Returns the number of components.
	 *
	 * @return the number of components (between {@code 1} and {@code n})
	 */
	public int components() {
		return count;
	}

	/**
	 * Returns the component identifier for the component containing site {@code p}.
	 *
	 * @param p the integer representing one site
	 * @return the component identifier for the component containing site {@code p}
	 * @throws IllegalArgumentException unless {@code 0 <= p < n}
	 */
	public int find(int p) {
		validate(p);
		int root = p;
		while(root != getParent(root)) {
			if(pathCompression) {
				doPathCompression(root);
			}
			root=getParent(root);
		}
		return root;
	}

	/**
	 * Returns true if the the two sites are in the same component.
	 *
	 * @param p the integer representing one site
	 * @param q the integer representing the other site
	 * @return {@code true} if the two sites {@code p} and {@code q} are in the same component;
	 * {@code false} otherwise
	 * @throws IllegalArgumentException unless
	 *                                  both {@code 0 <= p < n} and {@code 0 <= q < n}
	 */
	public boolean connected(int p, int q) {
		return find(p) == find(q);
	}

	/**
	 * Merges the component containing site {@code p} with the
	 * the component containing site {@code q}.
	 *
	 * @param p the integer representing one site
	 * @param q the integer representing the other site
	 * @throws IllegalArgumentException unless
	 *                                  both {@code 0 <= p < n} and {@code 0 <= q < n}
	 */
	public void union(int p, int q) {
		// CONSIDER can we avoid doing find again?
		mergeComponents(find(p), find(q));
		count--;
	}

	@Override
	public int size() {
		return parent.length;
	}

	/**
	 * Used only by testing code
	 *
	 * @param pathCompression true if you want path compression
	 */
	public void setPathCompression(boolean pathCompression) {
		this.pathCompression = pathCompression;
	}

	@Override
	public String toString() {
		return "UF_HWQUPC:" + "\n  count: " + count +
				"\n  path compression? " + pathCompression +
				"\n  parents: " + Arrays.toString(parent) +
				"\n  heights: " + Arrays.toString(height);
	}

	// validate that p is a valid index
	private void validate(int p) {
		int n = parent.length;
		if (p < 0 || p >= n) {
			throw new IllegalArgumentException("index " + p + " is not between 0 and " + (n - 1));
		}
	}

	private void updateParent(int p, int x) {
		parent[p] = x;
	}

	private void updateHeight(int p, int x) {
		height[p] += height[x];
	}

	/**
	 * Used only by testing code
	 *
	 * @param i the component
	 * @return the parent of the component
	 */
	private int getParent(int i) {
		return parent[i];
	}

	private final int[] parent;   // parent[i] = parent of i
	private final int[] height;   // height[i] = height of subtree rooted at i
	private int count;  // number of components
	private boolean pathCompression;

	private void mergeComponents(int i, int j) {
		int ri=find(i);
		int rj=find(j);
		
		if(height[ri]==height[rj]) {
			parent[rj]=ri;
			height[ri]++;
		}
		else if(height[ri]<height[rj]) {
			parent[ri]=rj;
			
		}
		else{
			parent[rj]=ri;
			
		}
	}

	/**
	 * This implements the single-pass path-halving mechanism of path compression
	 */
	private void doPathCompression(int i) {
		// TO BE IMPLEMENTED update parent to value of grandparent
		while(i!=getParent(i)){
		int p=getParent(i);
		int g=getParent(p);
		updateParent(i, g);
		i=p;
		}

	}

	public static void main(String[] args) {
		//Scanner sc = new Scanner(System.in);
		//		int n = sc.nextInt();
		Random r = new Random();

		// Total numbers of pairs to be generated to get to 1 tree.
		// For this the relation can be counted as number of pairs will be n-1 (n=number of objects)
		for(int n=500;n<=2048000;n=n*2) {

			UF_HWQUPC u = new UF_HWQUPC(n, false);

			int pair=0;
			for(int i=0;i<=n-1;i++) {
				int j,k;

				while(u.components()!=1) {
					j=r.nextInt(n);
					k=r.nextInt(n);
					if(!u.connected(k, j)) {
						u.union(j, k);
						pair=pair+1;
					}
				}
			}
			int count=u.components();
			System.out.println("Numbers => " +n + "= Pairs => " +pair + " Count => "+count);
		}
		System.out.println("");
		// Number of pairs generated till n-1 objects
		// For this the relation can be counted as n-m (n=number of objects; m=number of pairs)
		for(int n=500;n<=2048000;n=n*2) {

			UF_HWQUPC u = new UF_HWQUPC(n, false);

			int pair=0;
			for(int i=0;i<=n-1;i++) {
				int j,k;


				j=r.nextInt(n);
				k=r.nextInt(n);
				if(!u.connected(k, j)) {
					u.union(j, k);
					pair=pair+1;
				}
			}
			int count=u.components();
			System.out.println("Numbers => " +n + "= Pairs => " +pair + " Count => "+count);
		}
		for(int n=500;n<=2048000;n=n*2) {

			UF_HWQUPC u = new UF_HWQUPC(n, false);

			int pair=0;
			for(int i=0;i<=n-1;i++) {
				int j,k;

				while(u.components()!=1) {
					j=r.nextInt(n);
					k=r.nextInt(n);
					pair=pair+1;
					if(!u.connected(k, j)) {
						u.union(j, k);
						
					}
				}
				
			}
			int c=u.components();
			System.out.println("Numbers => " +n + "= Pairs => " +pair + " Trees => "+c);
			
		}
	}
} 
