package dominium.template;

import jade.content.ContentManager;
import jade.content.abs.AbsConcept;
import jade.content.abs.AbsIRE;
import jade.content.abs.AbsPredicate;
import jade.content.abs.AbsVariable;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.lang.sl.SLVocabulary;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import dominium.platform.Vocabulary;
import dominium.platform.suport.AgentState;
import dominium.platform.suport.HasState;
import dominium.platform.suport.PlatDominiumOntology;

public abstract class NormativeAgent extends Agent {

	// variaveis que compoe State - dependem do dominio da aplicacao
	// para Neptuno - somente normati

	private AgentState agentState;
	private ContentManager manager = (ContentManager) getContentManager();
	// This agent "speaks" the SL language
	private Codec codec = new SLCodec();
	// This agent "knows" the PlatDominiumOntology()
	private Ontology ontology = PlatDominiumOntology.getInstance();

	protected void setup() {

		System.out.println(this.getLocalName() + " is Active\n");

		manager.registerLanguage(codec);
		manager.registerOntology(ontology);

		// cadastrar DF
		// Register the book-selling service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("NormativeAgent");
		sd.setName(getLocalName() + "-NormativeAgent");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		addBehaviour(new HandlingStateQueryBehaviour(this));

		normativeAgentSetup();

	}

	protected void takeDown() {

		// Deregister NormativeAgent from the yellow pages
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	// este metodo serve para incluir os novos behaviors
	abstract protected void normativeAgentSetup();

	private class HandlingStateQueryBehaviour extends CyclicBehaviour {

		Agent a;

		public HandlingStateQueryBehaviour(Agent a) {
			super(a);
			this.a = a;
		}

		public void action() {
			ACLMessage msg = receive(MessageTemplate
					.MatchPerformative(ACLMessage.QUERY_REF));
			if (msg != null) {
				try {
					System.out.println("\n " + a.getLocalName()
							+ " Received query from Verifier. Message is");
					System.out.println(msg);
					// The content of a QUERY_REF is certainly an abstract
					// descriptor
					// representing an IRE
					AbsIRE ire = (AbsIRE) manager.extractAbsContent(msg);
					if (ire.getTypeName().equals(SLVocabulary.IOTA)) {
						AbsPredicate p = (AbsPredicate) ire.getProposition();
						if (p.getTypeName().equals(
								PlatDominiumOntology.HAS_STATE)
								&& p
								.getAbsTerm(PlatDominiumOntology.HAS_STATE_DATA) instanceof AbsVariable) {

							AbsConcept absRegAg = (AbsConcept) p
							.getAbsTerm(PlatDominiumOntology.HAS_STATE_AGENT);
							AID regulatedAgent = (AID) ontology
							.toObject(absRegAg);

							if (a.getLocalName().equals(
									regulatedAgent.getLocalName())) {
								addBehaviour(new InformStateBehaviour(myAgent));
							} else {
								System.out
								.println("Can't answer to query!! - the responder is other agent");
							}

						} else {
							System.out.println("Can't answer to query!!");
						}
					} else {
						System.out.println("Can't manage IRE of type "
								+ ire.getTypeName());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				block();
			}
		}

	}

	private class InformStateBehaviour extends OneShotBehaviour {

		Agent a;

		public InformStateBehaviour(Agent a) {
			super(a);
			this.a = a;

		}

		public void action() {

			try {
				System.out.println("\n" + a.getLocalName()
						+ "Inform the State to VerifierAgent");

				// Prepare the message
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				AID receiver = new AID(Vocabulary.VERIFIER_AGENT,
						AID.ISLOCALNAME);

				msg.setSender(getAID());
				msg.addReceiver(receiver);
				msg.setLanguage(codec.getName());
				msg.setOntology(ontology.getName());

				// Fill the content
				HasState hasState = new HasState();
				hasState.setRegulatedAgent(getAID());
				hasState.setAgentState(agentState);

				manager.fillContent(msg, hasState);
				send(msg);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}
}
