/**
 * 
 */
package dominium.ontology;

/**
 * 
 * Classe de constantes de enderecamento da ontologia. 
 * Operacional
 * 
 * SUBSTITUA O CAMPO ontologyNameOWL, ATRIBUINDO O NOME DA ONTOLOGIA USADA.
 * 
 * @author Pier
 * @since abr 2007
 */
public class OntoAddress {

	public static final String ontologyNameOWL = "dominium.owl";
	public static final String ontologyNS = ("http://www.owl-ontologies.com/"
			+ ontologyNameOWL + "#");
	public static final String ontologyURI = ("http://www.owl-ontologies.com/" + ontologyNameOWL);
	private static String ontoAddressStatic;

	/**
	 * @return a Sring witch the *.owl address
	 */
	public static String getOntoAddress() {
		
		if (ontoAddressStatic == null) ontoAddressStatic = OntoAddress.class.getResource(ontologyNameOWL).toString();
		return ontoAddressStatic;
		
	}

	/**
	 * 
	 * class test
	 * @param args
	 */
	public static void main(String[] args) {

	
		System.out.println("ontologyNameOWL\n: " + ontologyNameOWL);
		System.out.println("ontologyURI\n: " + ontologyURI);
		System.out.println("ontologyNS\n: " + ontologyNS);
		System.out.println("ontologyFileAddress\n: " + getOntoAddress());
	}

}
