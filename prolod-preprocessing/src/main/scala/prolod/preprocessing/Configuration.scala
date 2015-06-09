package prolod.preprocessing

import com.typesafe.config.ConfigFactory

case class Configuration() {
	var conf = ConfigFactory.load()
	var dbMainSchema = conf.getString("db.mainSchema")
	var dbDefaultUserView = conf.getString("db.defaultUserView")
	var dbDb2Password = conf.getString("db.db2.password")
	var dbDb2Host = conf.getString("db.db2.host")
	var dbDb2Port = conf.getInt("db.db2.port")
	var dbDb2Database = conf.getString("db.db2.database")
	var dbDb2Username = conf.getString("db.db2.username")
	var db2Driver = conf.getString("db.db2.driver")
}
