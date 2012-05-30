package graphs;

import java.io.Serializable;

/**
 * Represents the pair object containing partitionID and vertexIdentifier
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 * 
 */
public class VertexID implements Serializable {
	private static final long serialVersionUID = -8636812964597333133L;
	private int partitionID;
	private long vertexIdentifier;

	/**
	 * A Pair Object containing partitionID and vertex identifier
	 * 
	 * @param partitionID
	 *            Represents the partition to which this vertex belongs to
	 * @param vertexIdentifier
	 *            Represents the unique vertex Identifier
	 */
	public VertexID(int partitionID, long vertexIdentifier) {
		super();
		this.partitionID = partitionID;
		this.vertexIdentifier = vertexIdentifier;
	}

	/**
	 * Represents the hashCode of this java object
	 */
	public int hashCode() {
		int vertexHash = new Long(vertexIdentifier).hashCode();
		return (partitionID + vertexHash) * vertexHash + partitionID;
	}

	/**
	 * Overrides the java object's equal method
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof VertexID) {
			VertexID otherPair = (VertexID) other;
			return ((this.partitionID == otherPair.partitionID) && (this.vertexIdentifier == otherPair.vertexIdentifier));
		}
		return false;
	}

	/**
	 * Overrides the java Object's toString() method
	 */
	@Override
	public String toString() {
		return "(" + partitionID + ", " + vertexIdentifier + ")";
	}

	/**
	 * Gets the partition ID of this vertex
	 * 
	 * @return Returns the partition ID of this vertex
	 */
	public int getPartitionID() {
		return partitionID;
	}

	/**
	 * Gets the unique vertexIdentifier
	 * 
	 * @return Returns the unique vertexIdentifier
	 */
	public long getVertexIdentifier() {
		return vertexIdentifier;
	}
}