package com.sasaen.monitor.db;

import org.hsqldb.util.DatabaseManagerSwing;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.stereotype.Controller;

/**
 * Database controller that creates an embeded HSQL database, creates an USER
 * table and insert 3 rows in the table.
 * 
 * Also it opens a handy Swing Database Manager to CRUD on the database. 
 * 
 * @author sasaen
 *
 */
@Controller
public class DatabaseController {

	public DatabaseController() {

		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		builder.setType(EmbeddedDatabaseType.HSQL).addScript("db/sql/create-db.sql").addScript("db/sql/insert-data.sql")
				.build();

		startDBManager();
	}

	public void startDBManager() {

		// To prevent HeadLessException
		System.setProperty("java.awt.headless", "false");

		// Open HSQL Swing Database Manager
		DatabaseManagerSwing.main(new String[] { "--url", "jdbc:hsqldb:mem:testdb", "--user", "sa", "--password", "" });
	}

}