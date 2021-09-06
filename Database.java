package system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
	
		static Connection con;
		static Statement stmt;
	
	static void executeQuery(String query) throws SQLException, ClassNotFoundException {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		con= DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:ORCL","system","system");
		stmt = con.createStatement();
		stmt.executeUpdate(query);
	}
	
	void close() throws SQLException {
		stmt.close();
		con.close();
	}
}
