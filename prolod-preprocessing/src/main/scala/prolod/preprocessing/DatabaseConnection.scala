package prolod.preprocessing

import java.sql.{Driver, DriverManager, Connection}
import prolod.preprocessing.Configuration
import com.typesafe.slick.driver.db2.DB2Driver.api._


object DatabaseConnection {

	def main(config : Configuration) {
		val driver = config.db2Driver
		val url = "jdbc:db2://"+config.dbDb2Host+":"+config.dbDb2Port+"/"+config.dbDb2Database
		var username = config.dbDb2Username
		var password = config.dbDb2Password

		try {
			// val db = Database.forURL("jdbc:db2:"+config.dbDb2Host+":"+config.dbDb2Port+":"+config.dbDb2Database, driver="com.typesafe.slick.driver.db2.DB2Driver")

			var driver = com.typesafe.slick.driver.db2.DB2Driver.api
			var driverName ="com.typesafe.slick.driver.db2.DB2Driver.api"
			val db = Database.forDriver(driver, "jdbc:db2://"+config.dbDb2Host+":"+config.dbDb2Port+"/"+config.dbDb2Database, username, password)

			/*

			Class.forName("com.typesafe.slick.driver.db2.DB2Driver")
			var connection:Connection = DriverManager.getConnection(url, username, password)

			// create the statement, and run the select query
			val statement = connection.createStatement()
			val resultSet = statement.executeQuery("SELECT host, user FROM user")
			while ( resultSet.next() ) {
				val host = resultSet.getString("host")
				val user = resultSet.getString("user")
				println("host, user = " + host + ", " + user)
			}
			connection.close()
			*/
		} catch {
			case e => e.printStackTrace
		}

	}

}