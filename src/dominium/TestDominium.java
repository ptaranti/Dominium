package dominium;

import java.util.HashSet;
import java.util.Hashtable;
import com.hp.hpl.jena.ontology.impl.OntModelImpl;

import jade.Boot;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.tools.introspector.gui.MainWindow;
import jade.tools.rma.rma;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import jade.core.AID;
import jade.core.AgentContainer;
import jade.core.ContainerID;
import jade.core.Location;
import jade.core.MainContainer;
import jade.core.MainContainerImpl;
import jade.core.Profile;

import com.hp.hpl.jena.rdf.model.Resource;
import dominium.ontology.*;
import dominium.platform.Vocabulary;

public class TestDominium {

	public static TestDominium singleton = null;

	Runtime rt;
	static rma remoteAgentManager;

	public static TestDominium getInstance() {
		if (singleton == null) {
			singleton = new TestDominium();
		}
		return singleton;
	}

	public TestDominium() {

		rt = Runtime.instance();
		try {
			createTeather(rt);
		} catch (ControllerException e) {
			e.printStackTrace();
		}
	}

	public static void createTeather(Runtime rt) throws ControllerException {

		// World Locations
		Location local = null;

		// read ambients
		String tmp;

		System.out.println("iniciando: ");

		// instanciando MainConteiner
		Profile m = new ProfileImpl();
		ContainerController myMain = rt.createMainContainer(m);
		AgentController myRMA = myMain.createNewAgent("RMA",
				"jade.tools.rma.rma", new Object[0]);

		AgentController normativeAgentCA = myMain.createNewAgent(
				"NormativeAgent01", "dominium.template.NormativeAgent",
				new Object[0]);

		myRMA.start();
		normativeAgentCA.start();

		new LoadDominium(myMain);

	}

	public Runtime getRt() {
		return rt;
	}

	public static void main(String[] args) {
		// start jade with gui
		TestDominium.getInstance();

	}

	protected static void startJadeGui() {

	}

}
