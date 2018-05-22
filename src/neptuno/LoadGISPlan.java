package neptuno;

import java.sql.Statement;

import dominium.geographic.ConSingletonPgsql;

public class LoadGISPlan {

	static final String northControlArea = "POLYGON((-38.56 4.5 , -28.0 4.5 , -28.0 -13.02 , -38.56  -13.02 , -38.56 4.5))";
	static final String brazilArea = "POLYGON(( -59.0 4.5 ,	-28.0 4.5 , -28.0 -33.83 , -59.0 -33.83 , -59.0 4.5))";
	static final String amazonArea = "POLYGON(( -59.0 4.5 , -38.56 4.5 , -38.56  -8 , -59.0 -8.0 , -59.0 4.5 ))";
	static final String centralArea = "POLYGON(( -38.56  -13.02	, -28.0  -13.02 , -28.0  -25.52 , -48.33 -25.52 , -38.56 -13.02))";
	static final String southArea = "POLYGON(( -48.33 -25.52 , -28.0 -25.52 , -28.0 -33.83 , -53.5 -33.83 , -48.33 -25.52))";

	static final String lowVisibility = "POLYGON(( -50 -30 , -57 -27 , -38 -13.5 , -30 -16 , -50 -30 ))";
	static final String sar = "POLYGON(( -33 -12 , -33 -14 , -35 -14 , -35 -12 , -33 -12))";
	static final String roughSea = "POLYGON(( -34 -8 ,	-25 -8 , -25 -35 ,	-34 -35 , -34 -8))";
	static final String camposPetroleumArea = "POLYGON(( -39.776 -21.511 ,	-39.243 -21.936 , -40.622 -23.355 ,	-41.185 -22.947 , -39.776 -21.511 ))";

	static public void executeQuery(String query) {

		System.out.println("executando a SQL: \n" + query);

		try {
			java.sql.Connection conn = ConSingletonPgsql.getInstance()
			.getConn();
			Statement s = conn.createStatement();
			s.execute(query);
			s.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// query format: insert into \"RegionCinematic\" values ( 'RegionName' ,
		// '0', 'ETA', 'RegionGeometry', 'Curse', 'Speed')
		// eta - milisec
		// etd - milisec
		// curse - degree - relative true north
		// speed - in degree/hour (60x1852mt/hour -> ~110km/h or 60knots)

		String query = "insert into \"RegionCinematic\" values ( 'brazilArea' , '30000' , '3600000' ,'"
			+ brazilArea + "' , '0', '0' )";
		executeQuery(query);
		query = "insert into \"RegionCinematic\" values ( 'northControlArea' , '45000' , '3600000' , '"
			+ northControlArea + " ' , '0', '0'  )";
		executeQuery(query);
		query = "insert into \"RegionCinematic\" values ( 'amazonArea' , '60000' , '3600000' , '"
			+ amazonArea + " ' , '0', '0' )";
		executeQuery(query);
		query = "insert into \"RegionCinematic\" values ( 'centralArea' , '75000' , '3600000' , '"
			+ centralArea + " ' , '0', '0' )";
		executeQuery(query);
		query = "insert into \"RegionCinematic\" values ( 'southArea' , '30000' , '3600000' , '"
			+ southArea + "  ' , '0', '0' )";
		executeQuery(query);

		query = "insert into \"RegionCinematic\" values ( 'lowVisibility' , '0' , '60000' , '"
			+ lowVisibility + " ' , '030', '200' )";
		executeQuery(query);
		query = "insert into \"RegionCinematic\" values ( 'sar' , '30000' , '180000' , '"
			+ sar + " ' , '090', '250' )";
		executeQuery(query);
		query = "insert into \"RegionCinematic\" values ( 'roughSea' , '120000' , '240000' , '"
			+ roughSea + " ' , '300', '300' )";
		executeQuery(query);
		query = "insert into \"RegionCinematic\" values ( 'camposPetroleumArea' , '30000' , '3600000' , '"
			+ camposPetroleumArea + " ' , '0', '0' )";
		executeQuery(query);
	}

}
