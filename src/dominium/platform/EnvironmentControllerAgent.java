package dominium.platform;

import jade.content.ContentElement;
import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

import dominium.geographic.ConSingletonPgsql;
import dominium.platform.suport.HasPosition;
import dominium.platform.suport.PlatDominiumOntology;

public class EnvironmentControllerAgent extends Agent {

	private ContentManager manager = (ContentManager) getContentManager();
	// This agent "speaks" the SL language
	private Codec codec = new SLCodec();
	// This agent "knows" the PlatDominiumOntology()
	private Ontology ontology = PlatDominiumOntology.getInstance();

	private long startTime;
	private long currentTime;

	protected void setup() {
		manager.registerLanguage(codec);
		manager.registerOntology(ontology);
		startTime = System.currentTimeMillis();

		addBehaviour(new HandleInformBehaviour(this));
		addBehaviour(new SetRegions(this, 4000));
	}

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
					if (ce instanceof HasPosition) {
						HasPosition hasPosition = (HasPosition) ce;
						AID regulatedAgent = hasPosition.getRegulatedAgent();

						String positionWKT = hasPosition.getpositionWKT();

						// Incluir codigo para atualizar o agente no GisDB
						try {
							java.sql.Connection conn = ConSingletonPgsql
									.getInstance().getConn();
							Statement s = conn.createStatement();

							PreparedStatement ps = conn
									.prepareStatement("select * "
											+ "from \"AgentTable\" "
											+ "where \"agentLocalName\" = ?");
							ps.setString(1, regulatedAgent.getLocalName());

							ResultSet r = ps.executeQuery();

							String agent = new String();
							while (r.next()) {
								agent = (String) r.getObject(1);

								s.executeUpdate("update \"AgentTable\" "
										+ "set position='" + positionWKT + "' "
										+ "where \"agentLocalName\" = '"
										+ agent + "'");

							}

							ps.close();
							r.close();

							if (!(agent.equals(regulatedAgent.getLocalName()))) {
								s.executeUpdate("insert into \"AgentTable\" "
										+ "values ( '"
										+ regulatedAgent.getLocalName()
										+ "' , '" + positionWKT + "'  )");
							}

							s.close();

						} catch (Exception e) {
							e.printStackTrace();
						}
						//
						//
					}

					else {
						System.out.println("ECA: Unknown predicate "
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

	private class SetRegions extends TickerBehaviour {

		public SetRegions(Agent a, long mili) {
			super(a, mili);
		}

		public void onTick() {

			currentTime = System.currentTimeMillis();
			long timeInExecution = currentTime - startTime;

			try {
				java.sql.Connection conn = ConSingletonPgsql.getInstance()
						.getConn();
				Statement s1 = conn.createStatement();
				Statement s2 = conn.createStatement();

				ResultSet r = s1
						.executeQuery("select \"RegionName\", \"ETD\", \"ETA\", asText(\"RegionGeometry\"), \"Curse\", \"Speed\" from \"RegionCinematic\"");

				String regionName = new String();

				while (r.next()) {

					// cond para verificar se eta foi atingido
					// neste caso regiao é retirada do contexto espacial
					regionName = r.getString(1);
					if (r.getDouble(3) < timeInExecution) {
						s2
								.executeUpdate("delete from \"RegionTable\" where \"regionLocalName\" = '"
										+ regionName + "'");
						s2
								.executeUpdate("delete from \"RegionCinematic\" where \"RegionName\" = '"
										+ regionName + "'");
					}
					// se ETA ainda não chegou
					else {
						// vrf se ETD iniciou
						double etd = r.getDouble(2);
						if (etd < timeInExecution) {

							// calculo do deslocamento da geometria

							WKTReader wktReader = new WKTReader();
							Geometry geo = wktReader.read(r.getString(4));

							Coordinate[] geoCoords = geo.getCoordinates();

							int curse = r.getInt(5);
							int speed = r.getInt(6);

							// calculo de deslocamento
							// tempo em horas
							double deltaTime;
							deltaTime = (timeInExecution - etd)
									/ (1000 * 60 * 60); // tempo em horas

							double curseRad = (curse * (2 * Math.PI)) / 360;
							double deltaSpace = speed * deltaTime;
							Coordinate transCoord = new Coordinate();

							if (curseRad <= (Math.PI / 2)) {
								transCoord.y = deltaSpace * Math.cos(curseRad);
								transCoord.x = deltaSpace * Math.sin(curseRad);
							} else if (curseRad <= (Math.PI)) {
								curseRad = Math.PI - curseRad;
								transCoord.y = (-1) * deltaSpace
										* Math.cos(curseRad);
								transCoord.x = deltaSpace * Math.sin(curseRad);
							} else if (curseRad <= (Math.PI * 3 / 2)) {
								curseRad = (Math.PI * 3 / 2) - curseRad;
								transCoord.y = (-1) * deltaSpace
										* Math.sin(curseRad);
								transCoord.x = (-1) * deltaSpace
										* Math.cos(curseRad);
							} else if (curseRad <= (Math.PI * 2)) {
								curseRad = (Math.PI * 2) - curseRad;
								transCoord.y = deltaSpace * Math.cos(curseRad);
								transCoord.x = (-1) * deltaSpace
										* Math.sin(curseRad);
							}

							String wktNewGeo = "POLYGON((";

							int k = 0;
							for (Coordinate c : geoCoords) {

								Coordinate coordN = new Coordinate(c.x
										+ transCoord.x, c.y + transCoord.y);
								wktNewGeo = wktNewGeo
										+ (((Double) (coordN.x)).toString()
												+ " "
												+ ((Double) (coordN.y))
														.toString() + ",");
								k = k + 1;
							}
							// retirar ultima virgula
							wktNewGeo = wktNewGeo.substring(0, (wktNewGeo
									.length() - 1));
							wktNewGeo = wktNewGeo + ("))");

							// verifica se Regiao já está na regionTable
							PreparedStatement ps = conn
									.prepareStatement("select * "
											+ "from \"RegionTable\" "
											+ "where \"regionLocalName\" = ?");
							ps.setString(1, regionName);

							ResultSet r2 = ps.executeQuery();

							String regionT = new String();

							while (r2.next()) {
								regionT = (String) r2.getObject(1);
								s2.executeUpdate("update \"RegionTable\" "
										+ "set \"regionBorders\" = '"
										+ wktNewGeo + "' "
										+ "where \"regionLocalName\" = '"
										+ regionT + "'");

							}

							// se ainda não estava na lista

							if (!(regionName.equals(regionT))) {
								s2
										.executeUpdate("insert into \"RegionTable\" values ( '"
												+ regionName
												+ "' , '"
												+ wktNewGeo + "' )");
							}
							r2.close();
							ps.close();
						}

					}
				}
				r.close();
				s1.close();
				s2.close();
			}

			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}