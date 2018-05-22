package dominium.platform.suport;

import jade.content.onto.*;
import jade.content.schema.*;

public class PlatDominiumOntology extends Ontology {

	private static final long serialVersionUID = 1L;

	// The name identifying this ontology
	public static final String ONTOLOGY_NAME = "PlatDominiumOntology";
	// VOCABULARY

	// the state must be defined for the domain
	public static final String AGENT_STATE = "AgentState";
	public static final String AGENT_STATE_COURSE = "course"; // for NEPTUNO
	public static final String AGENT_STATE_SPEED = "speed"; // for NEPTUNO
	public static final String AGENT_STATE_POSITION = "positionWKT"; // for
	

	// general concepts :
	public static final String SITUATION_DATA = "SituationData";
	public static final String SITUATION_DATA_AGENT_AID = "regulatedAgent";
	public static final String NORM_SET = "normSet";
	public static final String INFORMATION_SET = "informationSet";

	public static final String NORM = "Norm";
	public static final String NORM_NAME = "normName";
	public static final String NORM_DELAY = "normDelay";
	public static final String NORM_PARAMETER = "normParameter";
	public static final String CONSEQUENCE_NAME = "consequenceName";
	public static final String VERIFY_NAME = "verifyName";

	public static final String AGENT_AND_SPATIAL_CONTEXT_SET = "AgentAndSpatialContext";
	public static final String AGENT_AID = "regulatedAgent";
	public static final String SPATIAL_CONTEXT_SET = "spatialContextSet";

	// predicates (properties)
	public static final String IS_IN = "IsIn";
	public static final String IS_IN_POSITION_WKT = "positionWKT";
	public static final String IS_IN_SPATIAL_CONTEXT_SET = "spatialContextSet";

	public static final String HAS_SPATIAL_CONTEXT_SET = "HasSpatialContextSet";
	public static final String HAS_SPATIAL_CONTEXT_SET_AGENT = "regulatedAgent";
	public static final String HAS_SPATIAL_CONTEXT_SET_CONTEXT_SET = "spatialContextSet";

	public static final String HAS_SITUATION_DATA = "HasSituationData";
	public static final String HAS_SITUATION_DATA_AGENT = "regulatedAgent";
	public static final String HAS_SITUATION_DATA_SITUATION_DATA = "situationData";

	public static final String HAS_SITUATION_DATA_2 = "HasSituationData2";
	public static final String HAS_SITUATION_DATA_AGENT_AND_SPATIAL_CONTEXT_SET = "agentAndSpatialContext";
	public static final String HAS_SITUATION_DATA_SITUATION_DATA_2 = "situationData2";

	public static final String HAS_STATE = "HasState";
	public static final String HAS_STATE_AGENT = "regulatedAgent";
	public static final String HAS_STATE_DATA = "agentState";

	public static final String HAS_POSITION = "HasPosition";
	public static final String HAS_POSITION_AGENT = "regulatedAgent";
	public static final String HAS_POSITION_WKT = "positionWKT";

	public static final String CAUSE = "Cause";
	public static final String CAUSE_AGENT = "regulatedAgent";
	public static final String CAUSED_CONSEQUENCE = "consequenceName";

	// The singleton instance of this ontology
	private static Ontology theInstance = new PlatDominiumOntology();

	// Retrieve the singleton Book-trading ontology instance

	public static Ontology getInstance() {
		return theInstance;
	}

	// Private constructor
	private PlatDominiumOntology() {
		// The Book-trading ontology extends the basic ontology
		super(ONTOLOGY_NAME, BasicOntology.getInstance());

		try {
			add(new ConceptSchema(AGENT_STATE), AgentState.class);
			add(new ConceptSchema(SITUATION_DATA), SituationData.class);
			add(new ConceptSchema(NORM), Norm.class);
			add(new ConceptSchema(AGENT_AND_SPATIAL_CONTEXT_SET),
					AgentAndSpatialContext.class);

			add(new PredicateSchema(IS_IN), IsIn.class);
			add(new PredicateSchema(HAS_SPATIAL_CONTEXT_SET),
					HasSpatialContextSet.class);
			add(new PredicateSchema(HAS_SITUATION_DATA), HasSituationData.class);
			add(new PredicateSchema(HAS_SITUATION_DATA_2),
					HasSituationData2.class);

			add(new PredicateSchema(HAS_STATE), HasState.class);
			add(new PredicateSchema(CAUSE), Cause.class);
			add(new PredicateSchema(HAS_POSITION), HasPosition.class);

			// Structure of the schema for the Agent State concept

			ConceptSchema cs = (ConceptSchema) getSchema(AGENT_STATE);
			cs.add(AGENT_STATE_COURSE,
					(PrimitiveSchema) getSchema(BasicOntology.INTEGER));
			cs.add(AGENT_STATE_SPEED,
					(PrimitiveSchema) getSchema(BasicOntology.INTEGER));
			cs.add(AGENT_STATE_POSITION,
					(PrimitiveSchema) getSchema(BasicOntology.STRING));

			// Structure of the schema for the Norm concept

			cs = (ConceptSchema) getSchema(NORM);
			cs
			.add(NORM_NAME,
					(PrimitiveSchema) getSchema(BasicOntology.STRING));
			cs.add(NORM_DELAY,
					(PrimitiveSchema) getSchema(BasicOntology.INTEGER),
					ObjectSchema.OPTIONAL);
			cs.add(NORM_PARAMETER,
					(PrimitiveSchema) getSchema(BasicOntology.INTEGER),
					ObjectSchema.OPTIONAL);
			cs.add(CONSEQUENCE_NAME,
					(PrimitiveSchema) getSchema(BasicOntology.STRING));
			cs.add(VERIFY_NAME,
					(PrimitiveSchema) getSchema(BasicOntology.STRING));

			// Structure of the schema for the Situation-Data concept

			cs = (ConceptSchema) getSchema(SITUATION_DATA);
			cs.add(SITUATION_DATA_AGENT_AID,
					(ConceptSchema) getSchema(BasicOntology.AID));
			cs.add(NORM_SET, (ConceptSchema) getSchema(NORM), 0,
					ObjectSchema.UNLIMITED);
			cs.add(INFORMATION_SET,
					(PrimitiveSchema) getSchema(BasicOntology.STRING), 0,
					ObjectSchema.UNLIMITED);

			// Structure of the schema for the AGENT_AND_SPATIAL_CONTEXT_SET
			// concept
			cs = (ConceptSchema) getSchema(AGENT_AND_SPATIAL_CONTEXT_SET);
			cs.add(AGENT_AID, (ConceptSchema) getSchema(BasicOntology.AID));
			cs.add(SPATIAL_CONTEXT_SET,
					(PrimitiveSchema) getSchema(BasicOntology.STRING), 0,
					ObjectSchema.UNLIMITED);

			// Structure of the schema for the IS_IN predicate
			PredicateSchema ps = (PredicateSchema) getSchema(IS_IN);
			ps.add(IS_IN_POSITION_WKT,
					(PrimitiveSchema) getSchema(BasicOntology.STRING));
			ps.add(IS_IN_SPATIAL_CONTEXT_SET,
					(PrimitiveSchema) getSchema(BasicOntology.STRING), 0,
					ObjectSchema.UNLIMITED);

			// Structure of the schema for the HAS_SPATIAL_CONTEXT_SET predicate
			ps = (PredicateSchema) getSchema(HAS_SPATIAL_CONTEXT_SET);
			ps.add(HAS_SPATIAL_CONTEXT_SET_AGENT,
					(ConceptSchema) getSchema(BasicOntology.AID));
			ps.add(HAS_SPATIAL_CONTEXT_SET_CONTEXT_SET,
					(PrimitiveSchema) getSchema(BasicOntology.STRING), 0,
					ObjectSchema.UNLIMITED);

			// Structure of the schema for the HAS_SITUATION_DATA predicate
			ps = (PredicateSchema) getSchema(HAS_SITUATION_DATA);
			ps.add(HAS_SITUATION_DATA_AGENT,
					(ConceptSchema) getSchema(BasicOntology.AID));
			ps.add(HAS_SITUATION_DATA_SITUATION_DATA,
					(ConceptSchema) getSchema(SITUATION_DATA));

			// Structure of the schema for the HAS_SITUATION_DATA_2 predicate
			ps = (PredicateSchema) getSchema(HAS_SITUATION_DATA_2);
			ps.add(HAS_SITUATION_DATA_AGENT_AND_SPATIAL_CONTEXT_SET,
					(ConceptSchema) getSchema(AGENT_AND_SPATIAL_CONTEXT_SET));
			ps.add(HAS_SITUATION_DATA_SITUATION_DATA_2,
					(ConceptSchema) getSchema(SITUATION_DATA));

			// Structure of the schema for the HAS_STATE predicate
			ps = (PredicateSchema) getSchema(HAS_STATE);
			ps.add(HAS_STATE_AGENT,
					(ConceptSchema) getSchema(BasicOntology.AID));
			ps.add(HAS_STATE_DATA, (ConceptSchema) getSchema(AGENT_STATE));

			// Structure of the schema for the CAUSE predicate
			ps = (PredicateSchema) getSchema(CAUSE);
			ps.add(CAUSE_AGENT, (ConceptSchema) getSchema(BasicOntology.AID));
			ps.add(CAUSED_CONSEQUENCE,
					(PrimitiveSchema) getSchema(BasicOntology.STRING));

			ps = (PredicateSchema) getSchema(HAS_POSITION);
			ps.add(HAS_POSITION_AGENT,
					(ConceptSchema) getSchema(BasicOntology.AID));
			ps.add(HAS_POSITION_WKT,
					(PrimitiveSchema) getSchema(BasicOntology.STRING));

		} catch (OntologyException oe) {
			oe.printStackTrace();
		}
	}

	public static void main(String[] args) {

		PlatDominiumOntology b = (PlatDominiumOntology) dominium.platform.suport.PlatDominiumOntology
		.getInstance();
		System.out.println(b.toString());
	}

}
