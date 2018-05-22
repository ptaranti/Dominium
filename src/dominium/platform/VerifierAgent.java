package dominium.platform;

import jade.content.ContentElement;
import jade.content.ContentManager;
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
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.leap.ArrayList;
import jade.util.leap.List;

import java.lang.reflect.Constructor;
import java.util.Hashtable;

import dominium.platform.suport.AgentState;
import dominium.platform.suport.Cause;
import dominium.platform.suport.HasSituationData;
import dominium.platform.suport.HasState;
import dominium.platform.suport.Norm;
import dominium.platform.suport.PlatDominiumOntology;
import dominium.platform.suport.SituationData;
import dominium.verify.Verify;

public class VerifierAgent extends Agent {

	private ContentManager manager = (ContentManager) getContentManager();
	// This agent "speaks" the SL language
	private Codec codec = new SLCodec();
	// This agent "knows" the PlatDominiumOntology
	private Ontology ontology = PlatDominiumOntology.getInstance();

	private Hashtable<AID, SituationData> TableVerify = new Hashtable<AID, SituationData>();

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
			if ((msg != null)) {

				try {

					boolean flag = true;
					ContentElement ce = manager.extractContent(msg);
					if ((ce instanceof HasSituationData)
							&& (msg.getSender().getLocalName()
									.equals(Vocabulary.COMPMODELQUERY_AGENT))) {

						HasSituationData hasSituationData = (HasSituationData) ce;
						AID regulatedAgent = hasSituationData
						.getRegulatedAgent();

						SituationData situationData = hasSituationData
						.getSituationData();

						//se já está na TableVerify, então está aguardando resposta do estado do agente,
						// senão , manda executar a query
						if (!(TableVerify.containsKey(regulatedAgent))) {
							addBehaviour(new QueryStateBehaviour(this.a,
									regulatedAgent));
						}

						// insere a informação mais recente da situação do agente na tabela.
						TableVerify.put(regulatedAgent, situationData);

						flag = false;

					}

					else if ((ce instanceof HasState)
							&& (TableVerify.containsKey(msg.getSender()))) {
						HasState hasState = (HasState) ce;
						AID regulatedAgent = hasState.getRegulatedAgent();

						AgentState agentState = hasState.getAgentState();

						addBehaviour(new VerifyTheAgentBehaviour(this.a,
								regulatedAgent, agentState, TableVerify
								.remove(regulatedAgent)));

						flag = false;

					}

					else if (flag) {
						System.out.println("VA: Unknown predicate "
								+ ce.getClass().getName()
								+ "\nMensagem inconveniente:\n" + msg);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				block();
			}

		}
	}

	private class QueryStateBehaviour extends OneShotBehaviour {
		AID regulatedAgent;

		public QueryStateBehaviour(Agent a, AID regulatedAgent) {
			super(a);
			this.regulatedAgent = regulatedAgent;
		}

		public void action() {
			try {

				// Prepare the message
				ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);
				AID receiver = regulatedAgent;

				msg.setSender(getAID());
				msg.addReceiver(receiver);
				msg.setLanguage(codec.getName());
				msg.setOntology(ontology.getName());

				// Fill the content
				Ontology onto = PlatDominiumOntology.getInstance();
				AbsVariable x = new AbsVariable("x",
						PlatDominiumOntology.AGENT_STATE);

				AbsPredicate hasState = new AbsPredicate(
						PlatDominiumOntology.HAS_STATE);
				hasState.set(PlatDominiumOntology.HAS_STATE_AGENT,
						(AbsTerm) onto.fromObject(regulatedAgent));
				hasState.set(PlatDominiumOntology.HAS_STATE_DATA, x);

				AbsIRE iota = new AbsIRE(SLVocabulary.IOTA);
				iota.setVariable(x);
				iota.setProposition(hasState);

				manager.fillContent(msg, iota);
				send(msg);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * @author Pier
	 *inserir a carga dos verificações de forma dinamica
	 *
	 */
	private class VerifyTheAgentBehaviour extends OneShotBehaviour {
		Agent a;
		AID regulatedAgent;
		AgentState agentState;
		List normSet = new ArrayList();
		List informationSet = new ArrayList();

		public VerifyTheAgentBehaviour(Agent a, AID regulatedAgent,
				AgentState agentState, SituationData situationData) {
			super(a);
			this.a = a;
			this.regulatedAgent = regulatedAgent;
			this.agentState = agentState;
			this.normSet = situationData.getNormSet();
			this.informationSet = situationData.getInformationSet();

		}

		public void action() {
			//
			//Aqui inserir o codigo para verificação das normas:

			// iterator sobre norma
			// carregar por reflexao usando norma.getname, parar informação e estado como parametro.

			final Class[] parameterTypes = { AID.class, AgentState.class,
					Norm.class, List.class };

			//visitar normas
			System.out
			.println("VerifierAgent:  Verificando Normas para agente "
					+ regulatedAgent.getLocalName());

			for (int i = 0; i < normSet.size(); i++) {

				Norm n = (Norm) normSet.get(i);

				String verifyName = ("dominium.verify." + n.getVerifyName());
				System.out.println("\n" + verifyName + "\n");
				try {

					Class clsVerify = Class.forName(verifyName);

					Constructor ctBeh = clsVerify
					.getConstructor(parameterTypes);

					Object[] parameters = { regulatedAgent, agentState, n,
							informationSet };
					Object verifier = ctBeh.newInstance(parameters);

					if (!(((Verify) verifier).result())) {
						//dispara consequencia
						addBehaviour(new InformViolationBehaviour(a,
								regulatedAgent, n.getConsequenceName()));

					}

				} catch (Throwable e) {
					System.out.println(e);
				}
			}

		}

		// se retorno falso/true, carregar o comportamento InformVioationCehaviour

	}

	private class InformViolationBehaviour extends OneShotBehaviour {
		private AID regulatedAgent;
		private String consequenceName;

		public InformViolationBehaviour(Agent a, AID regulatedAgent,
				String consequenceName) {
			super(a);
			this.regulatedAgent = regulatedAgent;
			this.consequenceName = consequenceName;
		}

		public void action() {
			try {
				System.out.println("\nVerifier inform Consequence Agent that "
						+ regulatedAgent.getLocalName()
						+ " violate a norm and the consequence "
						+ consequenceName + " must be aplied");

				// Prepare the message
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				AID receiver = new AID(Vocabulary.CONCEQUENCE_AGENT,
						AID.ISLOCALNAME);

				msg.setSender(getAID());
				msg.addReceiver(receiver);
				msg.addReceiver(regulatedAgent);
				msg.setLanguage(codec.getName());
				msg.setOntology(ontology.getName());

				// Fill the content
				Cause cause = new Cause();
				cause.setRegulatedAgent(regulatedAgent);
				cause.setConsequenceName(consequenceName);

				manager.fillContent(msg, cause);
				send(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}