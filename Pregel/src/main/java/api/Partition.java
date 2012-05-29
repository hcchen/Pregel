package api;

import java.util.*;

/**
 * Represents the partition of a graph consisting of set of vertices and their
 * outgoing edges
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 * 
 */

public class Partition implements Iterable<Vertex> {
	private List<Vertex> vertexList;

	/**
	 * @param vertexList
	 *            Represents the list of vertices in a partition
	 */
	public Partition(List<Vertex> vertexList) {
		this.vertexList = vertexList;
	}

	/**
	 * Iterator to iterate through the vertices
	 */
	@Override
	public Iterator<Vertex> iterator() {
		Iterator<Vertex> iter = vertexList.iterator();
		return iter;
	}
}
