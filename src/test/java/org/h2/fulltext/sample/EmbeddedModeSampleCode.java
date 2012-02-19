package org.h2.fulltext.sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.h2.fulltext.FullTextLucene;
import org.h2.fulltext.FullTextLuceneEx;

public class EmbeddedModeSampleCode {

	public static void main(String[] args) throws Exception {

		System.setProperty("h2.luceneAnalyzer", "org.apache.lucene.analysis.ja.JapaneseAnalyzer");
		System.setProperty("h2.luceneVersion", "35");
		System.setProperty("h2.isTriggerCommit", "false");
		System.setProperty("h2.useRamDirectory", "false");

		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection("jdbc:h2:test", "", "");
		conn.setAutoCommit(false);

		Statement stmt = conn.createStatement();
		stmt.execute("CREATE TABLE SAMPLE (ID IDENTITY PRIMARY KEY, TEXT VARCHAR(256))");
		FullTextLuceneEx.init(conn);

		PreparedStatement ps = conn.prepareStatement("INSERT INTO SAMPLE (TEXT) VALUES ( ? )");
		ps.setString(1, "サンプルレコード");
		ps.execute();

		FullTextLuceneEx.createIndex(conn, "PUBLIC", "SAMPLE", "TEXT");
		FullTextLuceneEx.commitAll(conn);

		PreparedStatement ps2 = conn
				.prepareStatement("SELECT S.ID, S.TEXT FROM FTL_SEARCH_DATA(?, 1000, 0) FT, SAMPLE S WHERE FT.TABLE='SAMPLE' AND S.ID=FT.KEYS[0];");
		ps2.setString(1, "レコード");
		ResultSet rs = ps2.executeQuery();
		while (rs.next()) {
			System.out.println(rs.getString(1) + ", " + rs.getString(2));
		}

		conn.close();
	}
}
