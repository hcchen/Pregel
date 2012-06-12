package applications;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import utility.Props;
import api.Client2Master;
import api.Data;
import exceptions.PropertyNotFoundException;
import graphs.InputGenerator;

/**
 * Client class.
 *
 * @author Prakash Chandrasekaran
 * @author Gautham Narayanasamy
 * @author Vijayaraghavan Subbaiah
 */

public class Client {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws RemoteException the remote exception
	 * @throws NotBoundException the not bound exception
	 * @throws MalformedURLException the malformed url exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws PropertyNotFoundException the property not found exception
	 */
	public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException, ClassNotFoundException, PropertyNotFoundException {
		if(args.length < 2){
			System.err.println("Insufficient number of arguments");
			System.exit(0);
		}
		String masterMachineName = args[0];
		int applicationID = Integer.parseInt(args[1]);
		
		String masterURL = "//" + masterMachineName + "/" + Client2Master.SERVICE_NAME;
		Client2Master client2Master = (Client2Master) Naming.lookup(masterURL);		
		runApplication(client2Master, applicationID);
		
	}
	
	/**
	 * Run application.
	 *
	 * @param client2Master the client2 master
	 * @param applicationID the application id
	 * @throws PropertyNotFoundException the property not found exception
	 * @throws RemoteException the remote exception
	 */
	private static void runApplication(Client2Master client2Master, int applicationID) throws PropertyNotFoundException, RemoteException{
		Props properties = Props.getInstance();
		int numVertices = properties.getIntProperty("TOTAL_NUM_VERTICES");
		double minEdgeWeight = properties.getDoubleProperty("MIN_EDGE_WEIGHT");
		double maxEdgeWeight = properties.getDoubleProperty("MAX_EDGE_WEIGHT");
		String graphFile = properties.getStringProperty("INPUT_GRAPH");
		InputGenerator inputGenerator = new InputGenerator(numVertices,
				minEdgeWeight, maxEdgeWeight, graphFile);
		inputGenerator.generateInput();
		String vertexClassName = null;
		Data<Double> data = null;
		switch(applicationID){
		case 1:
			vertexClassName = "applications.ShortestPathVertex";
			data = new ShortestPathData(new Double(0));
			break;
		case 2:
			vertexClassName = "applications.PageRankVertex";
			data = new PageRankData(new Double(0));
			break;
		}
		System.out.println("Vertex class: " + vertexClassName);
		client2Master.putTask(graphFile, vertexClassName, 0, data);
		String outputFile = client2Master.takeResult();
		System.out.println("Output written to " + outputFile);
	}
}
