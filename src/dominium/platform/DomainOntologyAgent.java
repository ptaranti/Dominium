package dominium.platform;

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
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import jade.util.leap.List;

import java.util.HashSet;
import java.util.Vector;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.impl.OntModelImpl;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import dominium.ontology.DominiumOntoModels;
import dominium.ontology.OntoAddress;
import dominium.platform.suport.AgentAndSpatialContext;
import dominium.platform.suport.HasSituationData;
import dominium.platform.suport.Norm;
import dominium.platform.suport.PlatDominiumOntology;
import dominium.platform.suport.SituationData;

public class DomainOntologyAgent extends Agent {

	private ContentManager manager = (ContentManager) getContentManager();
	// This agent "speaks" the SL language
	private Codec codec = new SLCodec();
	// This agent "knows" the PlatDominiumOntology()
	private Ontology ontology = PlatDominiumOntology.getInstance();

	protected void setup() {
		manager.registerLanguage(codec);
		manager.registerOntology(ontology);
		addBehaviour(new HandleQueryBehaviour(this));
	}

	private class HandleQueryBehaviour extends CyclicBehaviour {

		public HandleQueryBehaviour(Agent a) {
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
								PlatDominiumOntology.HAS_SITUATION_DATA_2)
								&& p
								.getAbsTerm(PlatDominiumOntology.HAS_SITUATION_DATA_SITUATION_DATA_2) instanceof AbsVariable) {

							AbsConcept absAgentAndSpatialContextSet = (AbsConcept) p
							.getAbsTerm(PlatDominiumOntology.HAS_SITUATION_DATA_AGENT_AND_SPATIAL_CONTEXT_SET);
							AgentAndSpatialContext agentAndSpatialContext = (AgentAndSpatialContext) ontology
							.toObject(absAgentAndSpatialContextSet);

							// na linha abaixo fazer chamada para calculo da
							// Situação

							addBehaviour(new InformSituationDataBehaviour(
									myAgent, agentAndSpatialContext));

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

	private class InformSituationDataBehaviour extends OneShotBehaviour {
		Agent a;
		private AID regulatedAgent;
		private List spatialContextSet;
		private SituationData situationData = new SituationData();

		public InformSituationDataBehaviour(Agent a,
				AgentAndSpatialContext agentAndSpatialContext) {
			super(a);
			this.a = a;
			this.regulatedAgent = agentAndSpatialContext.getRegulatedAgent();
			this.spatialContextSet = agentAndSpatialContext
			.getSpatialContextSet();
		}

		public void action() {

			// montando o objeto situacao do agente
			HashSet<String> sc = new HashSet<String>();
			Iterator iter = spatialContextSet.iterator();
			while (iter.hasNext()) {
				String element = (String) iter.next();
				sc.add(element);
			}

			ContextOfAgent coa = new ContextOfAgent(regulatedAgent
					.getLocalName(), sc);

			List temp = coa.getInformationSet();

			if (!(temp.isEmpty()))
				situationData.setInformationSet(temp);

			situationData.setNormSet(coa.getNormSet());
			situationData.setRegulatedAgent(regulatedAgent);

			// inserir codigo
			// para calculo da Situação
			// regulated agent
			// gerar situationData

			try {

				// Prepare the message
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				AID receiver = new AID(Vocabulary.COMPMODELQUERY_AGENT,
						AID.ISLOCALNAME);

				msg.setSender(getAID());
				msg.addReceiver(receiver);
				msg.setLanguage(codec.getName());
				msg.setOntology(ontology.getName());

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

	private class ContextOfAgent {

		// private

		/**
		 * @param args
		 */

		private HashSet<InnerNorm> tempInnerNormSet = new HashSet<InnerNorm>();

		private HashSet<Resource> tempInformationSet = new HashSet<Resource>();

		private HashSet<InnerNorm> innerNormSet = new HashSet<InnerNorm>();

		private HashSet<String> informationSet = new HashSet<String>();

		private HashSet<InnerNorm> obstructedInnerNormSet = new HashSet<InnerNorm>();

		private HashSet<String> obstructedInformationSet = new HashSet<String>();

		public ContextOfAgent(String agent, HashSet<String> spatialContext) {

			if (spatialContext == null)
				spatialContext = new HashSet<String>();

			createTempSets(agent, spatialContext);
			setInformation();
			setInnerNorms();

		}

		public ContextOfAgent(String agent) {

			this(agent, null);
		}

		private void createTempSets(String agent, HashSet<String> spatialContext) {

			// referencia para Ontologia Inferida
			OntModelImpl inferDominium = DominiumOntoModels
			.getDominiumInferModel();
			Resource agentOntResource = inferDominium
			.getResource(OntoAddress.ontologyNS + agent);
			// Instanciando as Classes a serem testadas
			OntClass norm = inferDominium.getOntClass(OntoAddress.ontologyNS
					+ "Norm");

			// Instanciando as propriedades necessarias
			Vector<Property> props = new Vector<Property>();

			// verifica se existe contextoGeo, ou seja se é georeferenciado
			// se for = null, admite o posicionamento geografico pela
			// propriedade
			// is_in
			// da ontologia

			if (spatialContext.isEmpty()) {
				Property isIn = inferDominium
				.getObjectProperty(OntoAddress.ontologyNS + "is_in");
				props.add(isIn);
			}
			Property play = inferDominium
			.getObjectProperty(OntoAddress.ontologyNS + "play");
			props.add(play);
			Property subordinatedOf = inferDominium
			.getObjectProperty(OntoAddress.ontologyNS
					+ "subordinated_of");
			props.add(subordinatedOf);
			Property hasData = inferDominium
			.getObjectProperty(OntoAddress.ontologyNS + "has_data");

			// tempResource is a list of all spatial and social contexts of the
			// agent
			HashSet<Resource> tempResource = new HashSet<Resource>();
			for (String sc : spatialContext) {
				Resource scOntResource = inferDominium
				.getResource(OntoAddress.ontologyNS + sc);
				tempResource.add(scOntResource);
			}

			for (Property p : props) {
				for (StmtIterator i = inferDominium.listStatements(
						agentOntResource, p, (RDFNode) null); i.hasNext();) {
					Statement stmt1 = i.nextStatement();
					tempResource.add(inferDominium.getResource(stmt1
							.getResource().getURI()));
				}
			}

			// adicionando o agente na lista para capturar informações do agente
			tempResource.add(agentOntResource);

			for (Resource ind : tempResource) {

				for (StmtIterator j = inferDominium.listStatements(ind,
						hasData, (RDFNode) null); j.hasNext();) {
					Statement stmt2 = j.nextStatement();
					Resource rscTemp = stmt2.getResource();
					if (inferDominium.contains(rscTemp, RDF.type, norm))
						tempInnerNormSet.add(new InnerNorm(rscTemp));
					else
						tempInformationSet.add(rscTemp);
				}
			}
		}

		private void setInformation() {

			OntModelImpl inferDominium = DominiumOntoModels
			.getDominiumInferModel();
			HashSet<Resource> tempResource = new HashSet<Resource>();
			Property obstructorTo = inferDominium
			.getObjectProperty(OntoAddress.ontologyNS + "obstructor_to");

			for (Resource rsc1 : tempInformationSet)
				tempResource.add(rsc1);

			for (Resource rsc1 : tempInformationSet) {

				boolean valid = true;

				for (Resource rsc2 : tempResource) {

					if (inferDominium.contains(rsc1, obstructorTo, rsc2)) {
						obstructedInformationSet.add(rsc1.getLocalName());
						valid = false;
						break;
					}
				}
				if (valid)
					informationSet.add(rsc1.getLocalName());
			}
		}

		private void setInnerNorms() {

			HashSet<InnerNorm> tempInnerNormSet2 = new HashSet<InnerNorm>();

			for (InnerNorm nor1 : tempInnerNormSet)
				tempInnerNormSet2.add(nor1);

			for (InnerNorm nor1 : tempInnerNormSet) {

				boolean valid = true;

				for (InnerNorm nor2 : tempInnerNormSet2) {

					switch (0) {

					case 0:
						// para comparaçoes se for mesma norma
						if ((nor1.getInnerNormName().equals(nor2
								.getInnerNormName())))
							break;

					case 1:
						/*
						 * Verifica se normas executam acoes diferentes, caso
						 * sim, verifica se as normas se obstruem
						 */

						if (!(nor1.getOrder().equals(nor2.getOrder()))) {
							if (verifyInnerNormObstruct(nor1, nor2)) {
								valid = false;
								obstructedInnerNormSet.add(nor1);

								break;
							} else
								break;

						}
						// System.out.println("passou teste obstrucao");

					case 2:
						// fluxo continua para normas que regulam mesma ação

						/*
						 * Verifica se alguma das normas tem maior nível de
						 * prioridade interrompendo neste caso
						 * 
						 */

						if (nor1.getLevel() > nor2.getLevel()) {
							break;
						} else if (nor1.getLevel() < nor2.getLevel()) {
							valid = false;
							break;
						}

						// System.out.println("passou teste nivel");

					case 3:

						// fluxo continua para normas que regulam mesma ação
						// e tem mesmo nivel de prioridade

						/*
						 * Verifica casos de contradição pela lógica deontica
						 * priorizando Obigatório sobre o Permitido Se Norma1
						 * ProibidaAcao, caso Norma2 PremitidaAcao ou
						 * ObrigatoriaAcao, estabelece contradicao entre as
						 * normas
						 * 
						 * Solucao de confitos deve ser feita estabelecendo
						 * Nivel mais alto a norma que deve vigorar
						 */

						String nor1OntClass = nor1.getInnerNormClass();
						String nor2OntClass = nor2.getInnerNormClass();

						// se normas de mesma classe não ha contradicao
						if (!(nor1OntClass.equals(nor2OntClass))) {
							// se sao de classes diferentes e uma eh proibicao ,
							// existe contradicao
							if ((nor1OntClass == "Prohibition")
									|| (nor2OntClass == "Prohibition")) {
								valid = false;
								obstructedInnerNormSet.add(nor1);
								break;
							}
							// neste ponto uma norma eh permiaao e outra eh
							// obrigacao, necessariamente
							// prioriza-se obrigacao
							else if (nor1OntClass == "Obligation")
								break;
							else {
								valid = false;
								break;
							}
						}

						// passou teste log. deontica

					case 4:
						/*
						 * Verificação do Parametro da norma- prioriza-se o de
						 * maior modulo (decisão arbitraria)
						 */

						if (Math.abs(nor1.getInnerNormParameter()) < Math
								.abs(nor2.getInnerNormParameter()))
							valid = false;

					}
				}

				if (valid)
					innerNormSet.add(nor1);

			}

		}

		private boolean verifyInnerNormObstruct(InnerNorm nor1, InnerNorm nor2) {

			OntModelImpl ontoDominium = DominiumOntoModels
			.getDominiumOntoModel();
			boolean teste = false;

			Property obstructorTo = ontoDominium
			.getObjectProperty(OntoAddress.ontologyNS + "obstructor_to");

			Resource OntoClassRSC1 = ontoDominium
			.getResource(OntoAddress.ontologyNS + nor1.getOrder());

			Resource OntoClassRSC2 = ontoDominium
			.getResource(OntoAddress.ontologyNS + nor2.getOrder());

			if (ontoDominium.contains(OntoClassRSC1, obstructorTo,
					OntoClassRSC2)) {
				if (nor1.getInnerNormClass().equals(nor2.getInnerNormClass()))
					teste = true;
				else if ((nor1.getInnerNormClass() == "Prohibition")
						|| (nor2.getInnerNormClass() == "Prohibition"))
					teste = false;
				else
					teste = true;

			}

			return teste;

		}

		/**
		 * @return the informationSet
		 */
		public List getInformationSet() {

			List is = new ArrayList();

			for (String inf : informationSet) {
				is.add(inf);
			}
			return is;
		}

		/**
		 * @return the normSet
		 */
		public List getNormSet() {
			List ns = new ArrayList();

			Norm NormTemp;
			for (InnerNorm nor : innerNormSet) {
				NormTemp = new Norm();
				NormTemp.setNormName(nor.getInnerNormName());
				NormTemp.setNormDelay(nor.getDelay());
				NormTemp.setNormParameter(nor.getInnerNormParameter());
				NormTemp.setConsequenceName(nor.getCanCause());
				NormTemp.setVerifyName(nor.getExecute());
				ns.add(NormTemp);
			}

			return ns;
		}

		/**
		 * @return the obstructedInformationSet
		 */
		public HashSet<String> getObstructedInformationSet() {
			return obstructedInformationSet;
		}

		/**
		 * @return the obstructedNormSet
		 */
		public HashSet<InnerNorm> getObstructedNormSet() {
			return obstructedInnerNormSet;
		}

	}

	private class InnerNorm {

		private String innerNormName;

		private String innerNormClass;

		private String order;

		private String execute;

		private String canCause;

		private int level;

		private int delay;

		private int innerNormParameter;

		public InnerNorm(Resource rsc) {

			OntModelImpl ontoDominium = DominiumOntoModels
			.getDominiumOntoModel();

			innerNormName = rsc.getLocalName();

			innerNormClass = (ontoDominium.getProperty(rsc, RDF.type))
			.getObject().asNode().getLocalName();

			ObjectProperty executeProperty = ontoDominium
			.getObjectProperty(OntoAddress.ontologyNS + "execute");
			Resource executeResource = (Resource) (ontoDominium.getProperty(
					rsc, executeProperty)).getObject();
			execute = executeResource.getLocalName();

			ObjectProperty orderProperty = ontoDominium
			.getObjectProperty(OntoAddress.ontologyNS + "order");
			Resource orderResource = (Resource) (ontoDominium.getProperty(rsc,
					orderProperty)).getObject();
			order = orderResource.getLocalName();

			ObjectProperty canCauseProperty = ontoDominium
			.getObjectProperty(OntoAddress.ontologyNS + "can_cause");
			canCause = (ontoDominium.getProperty((Resource) executeResource,
					canCauseProperty)).getObject().asNode().getLocalName();

			DatatypeProperty levelProperty = ontoDominium
			.getDatatypeProperty(OntoAddress.ontologyNS + "level");
			Statement stmTmp = ontoDominium.getProperty(rsc, levelProperty);
			if (stmTmp == null)
				level = 1;
			else
				level = stmTmp.getInt();

			DatatypeProperty delayProperty = ontoDominium
			.getDatatypeProperty(OntoAddress.ontologyNS + "delay");
			stmTmp = ontoDominium.getProperty(rsc, delayProperty);
			if (stmTmp == null)
				delay = 0;
			else
				delay = stmTmp.getInt();

			DatatypeProperty innerNormParameterProperty = ontoDominium
			.getDatatypeProperty(OntoAddress.ontologyNS + "norm_value");
			stmTmp = ontoDominium.getProperty(rsc, innerNormParameterProperty);
			if (stmTmp == null)
				innerNormParameter = 0;
			else
				innerNormParameter = stmTmp.getInt();

		}

		/**
		 * @return the canCause
		 */
		public String getCanCause() {
			return canCause;
		}

		/**
		 * @return the delay
		 */
		public int getDelay() {
			return delay;
		}

		/**
		 * @return the execute
		 */
		public String getExecute() {
			return execute;
		}

		/**
		 * @return the level
		 */
		public int getLevel() {
			return level;
		}

		/**
		 * @return the normClass
		 */
		public String getInnerNormClass() {
			return innerNormClass;
		}

		/**
		 * @return the normName
		 */
		public String getInnerNormName() {
			return innerNormName;
		}

		/**
		 * @return the normParameter
		 */
		public int getInnerNormParameter() {
			return innerNormParameter;
		}

		/**
		 * @return the order
		 */
		public String getOrder() {
			return order;
		}

	}

}
