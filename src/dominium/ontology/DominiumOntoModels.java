package dominium.ontology;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.impl.OntModelImpl;
import dominium.ontology.OntoAddress;

/**
 * 
 * Class with static references to de raw and infered ontology model
 * 
 * @author Pier
 * @since abr 2007
 *
 */
public class DominiumOntoModels {

	private static OntModelImpl rawDominiumOntModel;

	private static OntModelImpl inferDominiumOntModel;

	/**
	 * Method for refresh the onto models (raw and infered)
	 * uses this after alter the ontology
	 */
	public static void chargeModels() {
		String ontologyFileAddress = OntoAddress.getOntoAddress();

		// charge the raw ontology
		rawDominiumOntModel = new OntModelImpl(OntModelSpec.OWL_DL_MEM);
		rawDominiumOntModel.getDocumentManager().addAltEntry(
				ontologyFileAddress, ontologyFileAddress);
		rawDominiumOntModel.read(ontologyFileAddress);

		// charge the infered ontology

		inferDominiumOntModel = new OntModelImpl(
				OntModelSpec.OWL_DL_MEM_RULE_INF);
		inferDominiumOntModel.getDocumentManager().addAltEntry(
				ontologyFileAddress, ontologyFileAddress);
		inferDominiumOntModel.read(ontologyFileAddress);

	}
	
	/**
	 * @return OntModelImpl, that reference the raw ontology model
	 */
	public static OntModelImpl getDominiumOntoModel(){
		if (inferDominiumOntModel == null)chargeModels();
		return rawDominiumOntModel;
	}

	/**
	 * @return OntModelImpl, that reference the infered ontology model and use the owl-dl rules
	 */
	public static OntModelImpl getDominiumInferModel(){
		if (inferDominiumOntModel == null)chargeModels();
				return inferDominiumOntModel;
	}

	public static void main(String[] args) {

		// new OntoAddress();
		chargeModels();
		System.out.println("\n\n" + rawDominiumOntModel.toString());
		System.out.println("\n" + inferDominiumOntModel.toString());
		System.out.println("\n" + inferDominiumOntModel.getReasoner().toString());
		System.out.println("\n" + OntoAddress.getOntoAddress());

	}

}
