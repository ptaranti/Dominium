package dominium.platform;

import jade.content.ContentElement;
import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.lang.reflect.Constructor;

import dominium.platform.suport.Cause;
import dominium.platform.suport.PlatDominiumOntology;

public class ConsequenceAgent extends Agent {

	private ContentManager manager = (ContentManager) getContentManager();
	// This agent "speaks" the SL language
	private Codec codec = new SLCodec();
	// This agent "knows" the PlatDominiumOntology()
	private Ontology ontology = PlatDominiumOntology.getInstance();

	private final static Class[] parameterTypes = { ConsequenceAgent.class,
			AID.class };

	protected void setup() {
		manager.registerLanguage(codec);
		manager.registerOntology(ontology);
		addBehaviour(new HandleInformBehaviour(this));
	}

	// ConsequenceAgent handles informations received from the VerifierAgent
	private class HandleInformBehaviour extends CyclicBehaviour {

		Agent a;

		public HandleInformBehaviour(Agent a) {
			super(a);
			this.a = a;
		}

		public void action() {
			ACLMessage msg = receive(MessageTemplate
					.MatchPerformative(ACLMessage.INFORM));
			if ((msg != null)
					&& (msg.getSender().getLocalName()
							.equals(Vocabulary.VERIFIER_AGENT))) {
				System.out
						.println("\nConsequenceAgent: Information received from VERIFIER_AGENT. Message is");

				try {
					ContentElement ce = manager.extractContent(msg);
					if (ce instanceof Cause) {
						Cause cause = (Cause) ce;
						AID regulatedAgent = cause.getRegulatedAgent();
						System.out
								.println("Agente Violador: " + regulatedAgent);
						String consequenceName = cause.getConsequenceName();
						System.out.println("Consequencia causada: "
								+ consequenceName);

						String behaviourName = ("dominium.consequence." + consequenceName);
						System.out.println("\n" + behaviourName + "\n");
						try {
							Class clsConsequenceBehaviour = Class
									.forName(behaviourName);
							Constructor ctBeh = clsConsequenceBehaviour
									.getConstructor(parameterTypes);
							Object[] parameters = { a, regulatedAgent };
							Object objectConsequenceBehaviour = ctBeh
									.newInstance(parameters);
							a
									.addBehaviour((Behaviour) objectConsequenceBehaviour);

							// sistema considera que todos enforce receberao
							// ponteiro para seu
							// agente e dados contextuais.

						} catch (Throwable e) {
							System.out.println(e);
						}

					}

					else {
						System.out.println("CA: Unknown predicate "
								+ ce.getClass().getName());
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				block();
			}
		}

	}

}
