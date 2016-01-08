package prolod.preprocessing

import akka.actor.{Actor, Props}
import prolod.preprocessing.ImportCommands.ImportTriples

object AkkaMain {

	def main(args: Array[String]): Unit = {
		akka.Main.main(Array(classOf[ImportAkka].getName))
	}
}

class ImportAkka extends Actor {

	var remaining = 2

	override def preStart(): Unit = {
		val importDataset = context.actorOf(Props[ImportTriplesAkka], "ImportTriples")
		println("1 start import")
		importDataset ! new ImportTriples("a","b","c","d","e")

	  val updateTriples = context.actorOf(Props[UpdateClusterSizesAkka], "UpdateClusterSizes")
		println("2 start update")
		updateTriples ! ImportCommands.Start
	}

	def receive = {
		case ImportCommands.Done => {
			remaining -= 1
			println("done")
			if (remaining <= 0) {
				context.stop(self)
				println("terminating")
			}
		}
	}
}