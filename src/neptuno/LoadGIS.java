package neptuno;

import java.sql.Statement;
import dominium.geographic.ConSingletonPgsql;

public class LoadGIS {

	static final String northControlArea = "POLYGON((-38.56 4.5 , -28.0 4.5 , -28.0 -13.02 , -38.56  -13.02 , -38.56 4.5))";
	static final String brazilArea = "POLYGON(( -59.0 4.5 ,	-28.0 4.5 , -28.0 -33.83 , -59.0 -33.83 , -59.0 4.5))";
	static final String amazonArea = "POLYGON(( -59.0 4.5 , -38.56 4.5 , -38.56  -8 , -59.0 -8.0 , -59.0 4.5 ))";
	static final String centralArea = "POLYGON(( -38.56  -13.02	, -28.0  -13.02 , -28.0  -25.52 , -48.33 -25.52 , -38.56 -13.02))";
	static final String southArea = "POLYGON(( -48.33 -25.52 , -28.0 -25.52 , -28.0 -33.83 , -53.5 -33.83 , -48.33 -25.52))";

	static final String lowVisibility = "POLYGON(( -50 -30 , -57 -27 , -38 -13.5 , -30 -16 , -50 -30 ))";
	static final String sar = "POLYGON(( -33 -12 , -33 -14 , -35 -14 , -35 -12 , -33 -12))";
	static final String roughSea = "POLYGON(( -34 -8 ,	-25 -8 , -25 -35 ,	-34 -35 , -34 -8))";

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

		String query = "insert into \"RegionTable\" values ( 'brazilArea' , '"
			+ brazilArea + " ' )";
		executeQuery(query);
		query = "insert into \"RegionTable\" values ( 'northControlArea' , '"
			+ northControlArea + " ' )";
		executeQuery(query);
		query = "insert into \"RegionTable\" values ( 'amazonArea' , '"
			+ amazonArea + " ' )";
		executeQuery(query);
		query = "insert into \"RegionTable\" values ( 'centralArea' , '"
			+ centralArea + " ' )";
		executeQuery(query);
		query = "insert into \"RegionTable\" values ( 'southArea' , '"
			+ southArea + "  ' )";
		executeQuery(query);

		query = "insert into \"RegionTable\" values ( 'lowVisibility' , '"
			+ lowVisibility + " ' )";
		executeQuery(query);
		query = "insert into \"RegionTable\" values ( 'sar' , '" + sar + " ' )";
		executeQuery(query);
		query = "insert into \"RegionTable\" values ( 'roughSea' , '"
			+ roughSea + " ' )";
		executeQuery(query);

	}

}
