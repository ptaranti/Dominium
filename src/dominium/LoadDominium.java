package dominium;

import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import dominium.platform.Vocabulary;

public class LoadDominium {

	ContainerController CC;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public LoadDominium(ContainerController CC) {
		this.CC = CC;
		LoadDominiumAgents();
	}

	private void LoadDominiumAgents() {

		AgentController compModelQueryAgentAC;
		try {
			compModelQueryAgentAC = CC.createNewAgent(
					Vocabulary.COMPMODELQUERY_AGENT,
					"dominium.platform.CompModelQueryAgent", new Object[0]);
			compModelQueryAgentAC.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AgentController consequenceAgentAC;
		try {
			consequenceAgentAC = CC.createNewAgent(
					Vocabulary.CONCEQUENCE_AGENT,
					"dominium.platform.ConsequenceAgent", new Object[0]);
			consequenceAgentAC.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AgentController domainOntologyAgentAC;
		try {
			domainOntologyAgentAC = CC.createNewAgent(
					Vocabulary.DOMAINONTOLOGY_AGENT,
					"dominium.platform.DomainOntologyAgent", new Object[0]);
			domainOntologyAgentAC.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AgentController environmentControllerAgentAC;
		try {
			environmentControllerAgentAC = CC.createNewAgent(
					Vocabulary.ENVIRONMENTCONTROLLER_AGENT,
					"dominium.platform.EnvironmentControllerAgent",
					new Object[0]);
			environmentControllerAgentAC.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AgentController gisDBAgentAC;
		try {
			gisDBAgentAC = CC.createNewAgent(Vocabulary.GISDB_AGENT,
					"dominium.platform.GisDBAgent", new Object[0]);
			gisDBAgentAC.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AgentController providerAgentAC;
		try {
			providerAgentAC = CC.createNewAgent(Vocabulary.PROVIDER_AGENT,
					"dominium.platform.ProviderAgent", new Object[0]);
			providerAgentAC.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AgentController verifierAgentAC;
		try {
			verifierAgentAC = CC.createNewAgent(Vocabulary.VERIFIER_AGENT,
					"dominium.platform.VerifierAgent", new Object[0]);
			verifierAgentAC.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
