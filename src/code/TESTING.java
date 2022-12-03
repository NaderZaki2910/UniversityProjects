package code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;

public class TESTING {
	public static void main(String[] args) {
		Node n1=new Node("", null, null, 0, 10, 0);
		Node n2=new Node("", null, null, 0, 20, 0);
		PriorityQueue<Node> pq=new PriorityQueue<Node>();
		pq.add(n2);
		pq.add(n1);
		System.out.println(pq);
		System.out.println(pq.poll());
		System.out.println(pq.poll());
	}
}
