package system;

import graphs.VertexID;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import api.Partition;

/**
 * Represents the computation node
 * 
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 **/

public class Worker extends UnicastRemoteObject {

	private static final long serialVersionUID = -8137628519082382850L;
	private int numThreads;
	private BlockingQueue<Partition> partitionQueue;
	/* Hostname of the node */
	private String workerID;
	private Worker2Master masterProxy;
	private Map<Integer, String> mapPartitionIdToWorkerId;
	private Map<String, Worker2WorkerProxy> mapWorkerIdToW2WProxy = new HashMap<>();
	private Map<Worker, Map<VertexID, List<Message>>> outgoingMessages;
	private Map<Integer, Map<VertexID, List<Message>>> previousIncomingMessages;
	private Map<Integer, Map<VertexID, List<Message>>> currentIncomingMessages;

	/** boolean variable indicating whether the partitions can be worked upon by the workers in each superstep. **/
	boolean startSuperStep = false;


	public Worker() throws RemoteException {
		InetAddress address = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYMMMdd.HHmmss.SSS");
		String timeStamp = simpleDateFormat.format(new Date());

		String hostName = new String();
		try {
			address = InetAddress.getLocalHost();
			hostName = address.getHostName();
		} catch (UnknownHostException e) {
			hostName = "UnKnownHost";
			e.printStackTrace();
		} 

		this.workerID = hostName + "_" + timeStamp;
		this.partitionQueue = new LinkedBlockingDeque<Partition>();
		this.numThreads = Runtime.getRuntime().availableProcessors();
		for (int i = 0; i < numThreads; i++) {
			WorkerThread workerThread = new WorkerThread();
			workerThread.start();
		}
	}

	/**
	 * Adds the partition to be assigned to the worker.
	 * 
	 * @param partition
	 *            the partition to be assigned
	 */
	public void addPartition(Partition partition) throws RemoteException {
		this.partitionQueue.add(partition);
	}

	public void addPartitionList(List<Partition> workerPartitions)
			throws RemoteException {
		this.partitionQueue.addAll(workerPartitions);
	}

	public int getNumThreads() {
		return numThreads;
	}

	public String getWorkerID() {
		return workerID;
	}

	private class WorkerThread extends Thread {
		@Override
		public void run() {
			while (startSuperStep) {
				try {
					Partition partition = partitionQueue.take();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void setWorkerPartitionInfo(Map<Integer, String> mapPartitionIdToWorkerId,
			Map<String, Worker> mapWorkerIdToWorker) {
		this.mapPartitionIdToWorkerId = mapPartitionIdToWorkerId;
		mapWorkerIdToW2WProxy = new HashMap<>();
		for(Entry<String, Worker> entry : mapWorkerIdToWorker.entrySet()) {
			String currWorkerID = entry.getKey();
			if ( ! this.workerID.equals(currWorkerID)) {
				Worker worker = entry.getValue();
				Worker2WorkerProxy w2wProxy = new Worker2WorkerProxy(worker);
				mapWorkerIdToW2WProxy.put(currWorkerID, w2wProxy);
			}
		}
		startSuperStep = true;

	}

	public static void main(String[] args) throws Exception {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			String masterMachineName = args[0];
			Registry registry = LocateRegistry.getRegistry(masterMachineName);
			Worker2Master worker2Master = (Worker2Master) registry
					.lookup(Worker2Master.SERVICE_NAME);
			Worker worker = new Worker();
			Worker2Master masterProxy = worker2Master.register(worker,
					worker.getWorkerID(), worker.getNumThreads());			
			worker.setMasterProxy(masterProxy);
			System.out.println("Worker is bound and ready for computations ");
		} catch (Exception e) {
			System.err.println("ComputeEngine exception:");
			e.printStackTrace();
		}
	}

	private void setMasterProxy(Worker2Master masterProxy) {
		this.masterProxy = masterProxy;
	}

	public void receiveMessage(Map<VertexID, List<Message>> incomingMessages) {
		Map<VertexID, List<Message>> partitionMessages = null;
		int partitionID = 0;
		VertexID vertexID = null;
		for(Entry<VertexID, List<Message>> entry : incomingMessages.entrySet()) {
			vertexID = entry.getKey();
			partitionID = vertexID.getPartitionID(); 
			partitionMessages = currentIncomingMessages.get(partitionID);
			if(partitionMessages.containsKey(vertexID))
			{
				partitionMessages.get(vertexID).addAll(entry.getValue());
			}
			else
			{
				partitionMessages.put(vertexID, entry.getValue());
			}
		}
	}
}
