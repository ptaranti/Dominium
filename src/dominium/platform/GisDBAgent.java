package dominium.platform;

import jade.content.ContentElement;
import jade.content.ContentManager;
import jade.content.abs.AbsConcept;
import jade.content.abs.AbsIRE;
import jade.content.abs.AbsPredicate;
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTReader;

import dominium.geographic.ConSingletonPgsql;
import dominium.platform.suport.HasPosition;
import dominium.platform.suport.HasSpatialContextSet;
import dominium.platform.suport.PlatDominiumOntology;

public class GisDBAgent extends Agent {

	private ContentManager manager = (ContentManager) getContentManager();
	// This agent "speaks" the SL language
	private Codec codec = new SLCodec();
	// This agent "knows" the PlatDominiumOntology()
	private Ontology ontology = PlatDominiumOntology.getInstance();

	protected void setup() {
		manager.registerLanguage(codec);
		manager.registerOntology(ontology);

		// addBehaviour(new HandleInformBehaviour(this));--versao anterior
		addBehaviour(new HandleQueryBehaviour(this));

	}

	// SELLER handles queries received from BUYER
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
								PlatDominiumOntology.HAS_SPATIAL_CONTEXT_SET)) {

							AbsConcept absRegAg = (AbsConcept) p
							.getAbsTerm(PlatDominiumOntology.HAS_SPATIAL_CONTEXT_SET_AGENT);
							AID regulatedAgent = (AID) ontology
							.toObject(absRegAg);

							addBehaviour(new InformSpatialContextBehaviour(
									myAgent, regulatedAgent));

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

	private class InformSpatialContextBehaviour extends OneShotBehaviour {
		Agent a;
		private AID regulatedAgent;
		String agentPointWKT;
		private List spatialContextSet = new ArrayList();

		public InformSpatialContextBehaviour(Agent a, AID regulatedAgent) {
			super(a);
			this.a = a;
			this.regulatedAgent = regulatedAgent;
		}

		public void action() {

			//
			// inserir codigo
			// para calculo do contexo
			//
			// espacial do
			// regulated agent no spatialContextSet

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
				HasSpatialContextSet hasSpatialContextSet = new HasSpatialContextSet();
				hasSpatialContextSet.setRegulatedAgent(regulatedAgent);

				// verifyGisDB

				agentPointWKT = getAgentPoint(regulatedAgent.getLocalName());

				if (agentPointWKT != null) {
					spatialContextSet = getSpatialContexts(agentPointWKT);
				}

				hasSpatialContextSet.setSpatialContextSet(spatialContextSet);

				// se não possuir contexto

				manager.fillContent(msg, hasSpatialContextSet);
				send(msg);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		public String getAgentPoint(String agentLocalName) {

			String agentPosWKT = null;

			// recebendo conector

			try {
				java.sql.Connection conn = ConSingletonPgsql.getInstance()
				.getConn();
				Statement s = conn.createStatement();

				ResultSet r = s
				.executeQuery("select AsText(position) "
						+ "from \"AgentTable\" "
						+ "where \"agentLocalName\" = '"
						+ agentLocalName + "'");
				while (r.next()) {
					/*
					 * Retrieve the geometry as an object then cast it to the
					 * geometry type. Print things out.
					 */
					//				
					agentPosWKT = (String) r.getObject(1);

				}
				r.close();
				s.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

			return agentPosWKT;
		}

		public List getSpatialContexts(String coordWKT) {

			List spatialContext = new ArrayList();

			// recebendo conector

			try {
				java.sql.Connection conn = ConSingletonPgsql.getInstance()
				.getConn();
				Statement s = conn.createStatement();

				ResultSet r = s.executeQuery("select \"regionLocalName\" "
						+ "from \"RegionTable\" "
						+ "where Contains(\"regionBorders\", GeomFromText('"
						+ coordWKT + "'))");
				while (r.next()) {
					/*
					 * Retrieve the geometry as an object then cast it to the
					 * geometry type. Print things out.
					 */

					String name = (String) r.getObject(1);

					spatialContext.add(name);
				}
				s.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

			return spatialContext;
		}

	}

}
