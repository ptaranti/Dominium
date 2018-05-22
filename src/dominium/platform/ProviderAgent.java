package dominium.platform;

import jade.content.ContentElement;
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
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashSet;
import java.util.Hashtable;

import dominium.platform.suport.HasSituationData;
import dominium.platform.suport.PlatDominiumOntology;

public class ProviderAgent extends Agent {

	private ContentManager manager = (ContentManager) getContentManager();
	// This agent "speaks" the SL language
	private Codec codec = new SLCodec();
	// This agent "knows" the PlatDominiumOntology()
	private Ontology ontology = PlatDominiumOntology.getInstance();

	protected void setup() {
		manager.registerLanguage(codec);
		manager.registerOntology(ontology);

		addBehaviour(new HandleQuerySituationDataBehaviour(this));
		addBehaviour(new HandleInformBehaviour(this));
	}

	/**
	 * @author root classe HandleQuerySituationDataBehaviour trata as queries
	 *         dos NormativeAgentes. atÃ© 25/08 responde queries sobre dados de
	 *         contexto. Falta implementar querie de visibilidade.
	 */
	private class HandleQuerySituationDataBehaviour extends CyclicBehaviour {

		public HandleQuerySituationDataBehaviour(Agent a) {
			super(a);
		}

		public void action() {
			ACLMessage msg = receive(MessageTemplate
					.MatchPerformative(ACLMessage.QUERY_REF));
			if (msg != null) {
				try {

					// The content of a QUERY_REF is certainly an abstract
					// descriptor
					// representing an IRE

					AbsIRE ire = (AbsIRE) manager.extractAbsContent(msg);
					if (ire.getTypeName().equals(SLVocabulary.IOTA)) {
						AbsPredicate p = (AbsPredicate) ire.getProposition();
						if (p.getTypeName().equals(
								PlatDominiumOntology.HAS_SITUATION_DATA)
								&& p
								.getAbsTerm(PlatDominiumOntology.HAS_SITUATION_DATA_SITUATION_DATA) instanceof AbsVariable) {

							AbsConcept absregAgentAID = (AbsConcept) p
							.getAbsTerm(PlatDominiumOntology.HAS_SITUATION_DATA_AGENT);
							AID regAgentAID = (AID) ontology
							.toObject(absregAgentAID);

							// inserir agente normativo na lista de espera de
							// informações
							// buyAgents.add(regAgentAID);

							addBehaviour(new QuerySituationDataBehaviour(
									myAgent, ire));
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

	/**
	 * @author root classe QuerySituationDataBehaviour envia para
	 *         CompModelQueryAgent as queries dos NormativeAgentes.
	 * 
	 */
	private class QuerySituationDataBehaviour extends OneShotBehaviour {

		AbsIRE iota;
		long time = System.currentTimeMillis();
		AID normativeAgentAID;

		public QuerySituationDataBehaviour(Agent a, AbsIRE iota) {
			super(a);
			this.iota = iota;
			this.time = System.currentTimeMillis();

		}

		public void action() {
			try {

				// Prepare the message
				ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);
				AID receiver = new AID(Vocabulary.COMPMODELQUERY_AGENT,
						AID.ISLOCALNAME);

				msg.setSender(getAID());
				msg.addReceiver(receiver);
				msg.setLanguage(codec.getName());
				msg.setOntology(ontology.getName());
				msg.setConversationId(String.valueOf(time));

				manager.fillContent(msg, iota);
				send(msg);

				// get the AID of normativeAgent
				AbsPredicate p = (AbsPredicate) iota.getProposition();
				AbsConcept absAID = (AbsConcept) p
				.getAbsTerm(PlatDominiumOntology.HAS_SITUATION_DATA_AGENT);
				normativeAgentAID = (AID) ontology.toObject(absAID);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * @author root
	 * 
	 */
	private class HandleInformBehaviour extends CyclicBehaviour {

		public HandleInformBehaviour(Agent a) {
			super(a);
		}

		public void action() {
			ACLMessage msg = receive(MessageTemplate
					.MatchPerformative(ACLMessage.INFORM));
			if (msg != null) {

				try {
					ContentElement ce = manager.extractContent(msg);

					if (ce instanceof HasSituationData) {
						// Prepare the message

						HasSituationData hasSituationData = (HasSituationData) ce;
						AID normativeAgentReceiver = hasSituationData
						.getRegulatedAgent();

						msg.setSender(getAID());
						msg.removeReceiver(getAID());
						msg.removeReceiver(new AID(Vocabulary.VERIFIER_AGENT,
								AID.ISLOCALNAME));
						msg.addReceiver(normativeAgentReceiver);

						send(msg);
					}

					else {
						System.out.println("PA: Unknown predicate "
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
