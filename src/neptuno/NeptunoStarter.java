package neptuno;

import jade.core.Location;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.tools.rma.rma;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;

import java.util.HashSet;

import com.hp.hpl.jena.ontology.impl.OntModelImpl;
import com.hp.hpl.jena.rdf.model.Resource;

import dominium.LoadDominium;
import dominium.ontology.DominiumOntoModels;
import dominium.ontology.OntoAddress;

public class NeptunoStarter {

	public static NeptunoStarter singleton = null;

	Runtime rt;

	static rma remoteAgentManager;

	public static NeptunoStarter getInstance() {
		if (singleton == null) {
			singleton = new NeptunoStarter();
		}
		return singleton;
	}

	public NeptunoStarter() {

		rt = Runtime.instance();
		try {
			createSeaTeather(rt);
		} catch (ControllerException e) {
			e.printStackTrace();
		}
	}

	public static void createSeaTeather(Runtime rt) throws ControllerException {

		// World Locations
		Location local = null;

		// read ambients
		String tmp;

		OntModelImpl ontModel = DominiumOntoModels.getDominiumOntoModel();

		//instanciando MainConteiner (JADE)
		Profile m = new ProfileImpl(true);
		m.setParameter("detect-main", "false"); // to avoid a bug in jade 3.5 
		ContainerController myMain = rt.createMainContainer(m);
		AgentController myRMA = myMain.createNewAgent("RMA",
				"jade.tools.rma.rma", new Object[0]);
		myRMA.start();

		//iniciandoi Dominium
		new LoadDominium(myMain);

		//instanciando SeaAgents
		AgentController seaAgent;

		HashSet<Resource> agt = (HashSet<Resource>) ontModel.listIndividuals(
				ontModel.getOntClass(OntoAddress.ontologyNS + "Agent")).toSet();

		System.out.println(OntoAddress.ontologyNS + "Agent");

		System.out.println(agt.toString());

		for (Resource a : agt) {

			System.out.println("Instanciando agente: " + a.getLocalName());
			seaAgent = myMain.createNewAgent(a.getLocalName(),
					"neptuno.SeaAgent", new Object[0]);
			seaAgent.start();
		}

	}

	public Runtime getRt() {
		return rt;
	}

	public static void main(String[] args) {
		NeptunoStarter.getInstance();
	}

}
