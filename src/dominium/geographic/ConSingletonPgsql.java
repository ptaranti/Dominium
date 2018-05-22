package dominium.geographic;

import java.sql.*;

import org.postgis.*;
import org.postgresql.*;

public class ConSingletonPgsql {

	private static ConSingletonPgsql singleton = null;
	private PGConnection pgConn;
	private java.sql.Connection conn;

	private ConSingletonPgsql() {

		try {
			/*
			 * Load the JDBC driver and establish a connection.
			 */
			Class.forName("org.postgresql.Driver");
			String url = "jdbc:postgresql://localhost:5432/Dominium";
			conn = DriverManager.getConnection(url, "postgres", "dominium");
			pgConn = (PGConnection) conn;

			/*
			 * Add the geometry types to the connection. Note that you
			 * must cast the connection to the pgsql-specific connection * implementation before calling the addDataType() method.
			 */
			DriverWrapper.addGISTypes80(pgConn);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ConSingletonPgsql getInstance() {
		if (singleton == null) {
			singleton = new ConSingletonPgsql();
		}
		return singleton;
	}

	/**
	 * @return the conn
	 */
	public java.sql.Connection getConn() {
		return conn;
	}
}
