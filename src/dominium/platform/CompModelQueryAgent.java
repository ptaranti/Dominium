package dominium.platform;

import jade.content.ContentElement;
import jade.content.ContentManager;
import jade.content.abs.AbsConcept;
import jade.content.abs.AbsIRE;
import jade.content.abs.AbsPredicate;
import jade.content.abs.AbsTerm;
import jade.content.abs.AbsVariable;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.lang.sl.SLVocabulary;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.leap.ArrayList;
import jade.util.leap.List;

import java.util.Vector;

import dominium.platform.suport.AgentAndSpatialContext;
import dominium.platform.suport.HasSituationData;
import dominium.platform.suport.HasSpatialContextSet;
import dominium.platform.suport.PlatDominiumOntology;
import dominium.platform.suport.SituationData;

public class CompModelQueryAgent extends Agent {

	private ContentManager manager = (ContentManager) getContentManager();
	// This agent "speaks" the SL language
	private Codec codec = new SLCodec();
	// This agent "knows" the PlatDominiumOntology()
	private Ontology ontology = PlatDominiumOntology.getInstance();

	private Vector<AID> buyMessages = new Vector<AID>(); // list of agents
	// that query
	private Vector<AID> activeNormativeAgents = new Vector<AID>();
	private int activeQueriesAgentsCounter = 0;

	protected void setup() {
		manager.registerLanguage(codec);
		manager.registerOntology(ontology);

		// Update the list of normative agents every minute
		addBehaviour(new VerifyListOfactiveNormativeAgentsBehaviour(this, 6000));
		addBehaviour(new HandlingProviderAgentQueryBehaviour(this));

		addBehaviour(new QueryGisDBAgentBehaviour(this));
		addBehaviour(new HandleInformBehaviour(this));

	}

	// recebe o nome do proximo agente normativo a ser verificado e encaminha
	// query para GisDBAgent

	private class VerifyListOfactiveNormativeAgentsBehaviour extends
			TickerBehaviour {

		public VerifyListOfactiveNormativeAgentsBehaviour(Agent a, long miliseg) {
			super(a, miliseg);
		}

		protected void onTick() {

			// verifica se todos elementos da lista foram verificados e gera
			// nova lista neste caso.
			if (activeNormativeAgents.isEmpty()) {
				// Update the list of active NormativeAgents agents
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("NormativeAgent");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent,
							template);
					activeNormativeAgents.clear();
					for (int i = 0; i < result.length; ++i) {
						activeNormativeAgents.add(result[i].getName()); // insere
						// os
						// AID na
						// lista
					}
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}
			}
		}
	}

	private class HandlingProviderAgentQueryBehaviour extends CyclicBehaviour {

		long msgID;

		public HandlingProviderAgentQueryBehaviour(Agent a) {
			super(a);
		}

		public void action() {
			ACLMessage msg = receive(MessageTemplate
					.MatchPerformative(ACLMessage.QUERY_REF));
			if (msg != null) {
				try {

					msgID = Long.parseLong(msg.getConversationId());
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

							if (!(buyMessages.contains(regAgentAID)))
								buyMessages.add(regAgentAID);

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

	private class QueryGisDBAgentBehaviour extends CyclicBehaviour {
		AbsIRE iota;

		AID normativeAgentAID;

		public QueryGisDBAgentBehaviour(Agent a) {
			super(a);

		}

		public void action() {

			if ((activeQueriesAgentsCounter < 10)
					&& (!(activeNormativeAgents.isEmpty()))) {

				try {
					normativeAgentAID = activeNormativeAgents.remove(0);

					// Prepare the message
					ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);
					AID receiver = new AID(Vocabulary.GISDB_AGENT,
							AID.ISLOCALNAME);

					msg.setSender(getAID());
					msg.addReceiver(receiver);
					msg.setLanguage(codec.getName());
					msg.setOntology(ontology.getName());

					// Fill the content
					Ontology onto = PlatDominiumOntology.getInstance();
					AbsVariable x = new AbsVariable(
							"x",
							PlatDominiumOntology.HAS_SPATIAL_CONTEXT_SET_CONTEXT_SET);

					AbsPredicate hasSpatialContextSet = new AbsPredicate(
							PlatDominiumOntology.HAS_SPATIAL_CONTEXT_SET);
					hasSpatialContextSet.set(
							PlatDominiumOntology.HAS_SPATIAL_CONTEXT_SET_AGENT,
							(AbsTerm) onto.fromObject(normativeAgentAID));
					hasSpatialContextSet
							.set(
									PlatDominiumOntology.HAS_SPATIAL_CONTEXT_SET_CONTEXT_SET,
									x);

					AbsIRE iota = new AbsIRE(SLVocabulary.IOTA);
					iota.setVariable(x);
					iota.setProposition(hasSpatialContextSet);

					manager.fillContent(msg, iota);
					send(msg);

					activeQueriesAgentsCounter = +1;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	private class QueryDomainOntologyAgentBehaviour extends OneShotBehaviour {
		AbsIRE iota;

		AgentAndSpatialContext agentAndSpatialContext = new AgentAndSpatialContext();

		public QueryDomainOntologyAgentBehaviour(Agent a,
				HasSpatialContextSet hasSpatialContextSet) {
			super(a);

			agentAndSpatialContext.setRegulatedAgent(hasSpatialContextSet
					.getRegulatedAgent());
			agentAndSpatialContext.setSpatialContextSet(hasSpatialContextSet
					.getSpatialContextSet());

		}

		public void action() {

			try {
				/*
				 * System.out .println("\nCompModelQuery: Query set NORMS " +
				 * agentAndSpatialContext.getRegulatedAgent().getLocalName());
				 */

				// Prepare the message
				ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);
				AID receiver = new AID(Vocabulary.DOMAINONTOLOGY_AGENT,
						AID.ISLOCALNAME);

				msg.setSender(getAID());
				msg.addReceiver(receiver);
				msg.setLanguage(codec.getName());
				msg.setOntology(ontology.getName());
				// msg.setConversationId(String.valueOf(time));

				// Fill the content
				Ontology onto = PlatDominiumOntology.getInstance();
				AbsVariable x = new AbsVariable(
						"x",
						PlatDominiumOntology.HAS_SPATIAL_CONTEXT_SET_CONTEXT_SET);

				AbsPredicate hasSpatialContextSet = new AbsPredicate(
						PlatDominiumOntology.HAS_SITUATION_DATA_2);
				hasSpatialContextSet
						.set(
								PlatDominiumOntology.HAS_SITUATION_DATA_AGENT_AND_SPATIAL_CONTEXT_SET,
								(AbsTerm) onto
										.fromObject(agentAndSpatialContext));
				hasSpatialContextSet
						.set(
								PlatDominiumOntology.HAS_SITUATION_DATA_SITUATION_DATA_2,
								x);

				AbsIRE iota = new AbsIRE(SLVocabulary.IOTA);
				iota.setVariable(x);
				iota.setProposition(hasSpatialContextSet);

				manager.fillContent(msg, iota);
				send(msg);

				activeQueriesAgentsCounter = activeQueriesAgentsCounter - 1;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private class InformSituationDataBehaviour extends OneShotBehaviour {
		Agent a;
		private AID regulatedAgent;
		private SituationData situationData;

		public InformSituationDataBehaviour(Agent a, SituationData situationData) {
			super(a);
			this.a = a;
			this.regulatedAgent = situationData.getRegulatedAgent();
			this.situationData = situationData;
		}

		public void action() {

			try {

				// Prepare the message
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				AID receiver = new AID(Vocabulary.VERIFIER_AGENT,
						AID.ISLOCALNAME);

				msg.setSender(getAID());
				msg.addReceiver(receiver);
				msg.setLanguage(codec.getName());
				msg.setOntology(ontology.getName());
				if ((buyMessages.contains(regulatedAgent))) {
					receiver = new AID(Vocabulary.PROVIDER_AGENT,
							AID.ISLOCALNAME);
					msg.addReceiver(receiver);
					buyMessages.remove(regulatedAgent);
				}

				// Fill the content
				HasSituationData hasSituationData = new HasSituationData();
				hasSituationData.setRegulatedAgent(regulatedAgent);
				hasSituationData.setSituationData(situationData);

				manager.fillContent(msg, hasSituationData);
				send(msg);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private class HandleInformBehaviour extends CyclicBehaviour {
		Agent a;

		public HandleInformBehaviour(Agent a) {
			super(a);
			this.a = a;
		}

		public void action() {
			ACLMessage msg = receive(MessageTemplate
					.MatchPerformative(ACLMessage.INFORM));
			if ((msg != null)) {

				try {
					ContentElement ce = manager.extractContent(msg);
					if ((ce instanceof HasSituationData)
							&& (msg.getSender().getLocalName()
									.equals(Vocabulary.DOMAINONTOLOGY_AGENT))) {
						HasSituationData hasSituationData = (HasSituationData) ce;
						AID regulatedAgent = (AID) hasSituationData
								.getRegulatedAgent();

						SituationData situationData = (SituationData) hasSituationData
								.getSituationData();

						addBehaviour(new InformSituationDataBehaviour(this.a,
								situationData));
					}

					else if ((ce instanceof HasSpatialContextSet)
							&& (msg.getSender().getLocalName()
									.equals(Vocabulary.GISDB_AGENT))) {
						HasSpatialContextSet hasSpatialContextSet = (HasSpatialContextSet) ce;

						AID regulatedAgent = hasSpatialContextSet
								.getRegulatedAgent();

						List spatialContextSet = new ArrayList();
						spatialContextSet = hasSpatialContextSet
								.getSpatialContextSet();

						addBehaviour(new QueryDomainOntologyAgentBehaviour(a,
								hasSpatialContextSet));

					}

					else {
						System.out.println("CMQ:  Unknown predicate "
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
