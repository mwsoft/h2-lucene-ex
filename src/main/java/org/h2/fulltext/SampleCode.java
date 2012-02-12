package org.h2.fulltext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class SampleCode {

	public static void main(String[] args) throws Exception {

		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test", "sa", "");
		conn.setAutoCommit(false);

		Statement stmt = conn.createStatement();
		stmt.execute("CREATE TABLE SAMPLE (ID IDENTITY PRIMARY KEY, TEXT VARCHAR(256))");
		stmt.execute("CREATE ALIAS IF NOT EXISTS FTL_INIT FOR \"org.h2.fulltext.FullTextLuceneJa.init\"");
		stmt.execute("CALL FTL_INIT()");

		PreparedStatement ps = conn.prepareStatement("INSERT INTO SAMPLE (TEXT) VALUES ( ? )");

		ps.setString(1, "サンプルレコード");
		ps.execute();

		stmt.execute("CALL FTL_CREATE_INDEX('PUBLIC', 'SAMPLE', 'TEXT')");
		stmt.execute("commit;");
		conn.close();
	}
}
