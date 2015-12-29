package com.sony.chavdar.assignment

import java.text.{ParseException, SimpleDateFormat}
import java.util.Date

import scala.collection.mutable

class InvalidLineException(message: String) extends RuntimeException(message)

object Session {
  def apply(record: UserDataRecord): Session = {
    Session(record.userId, record.date, record.date, Seq(Song(record.artistName, record.trackName)))
  }
}

case class Session(userId: String, firstTime: Date, lastTime: Date, songs: Seq[Song]) {
  val asTsvLine: String = s"$userId\t$firstTime\t$lastTime\t${songs.map(_.asTsvLine)}"
  val duration = lastTime.getTime - firstTime.getTime
}

case class Song(artistName: String, trackName: String){
  val asTsvLine = s"$artistName\t$trackName"
}

case class UserDataRecord(userId: String, date: Date, artistId: String, artistName: String, trackId: String, trackName: String) {
  require(userId.nonEmpty, "userId must not be empty")
  require(date != null, "date must not be null")
  require(artistName.nonEmpty, "artistName must not be empty")
  require(trackName.nonEmpty, "trackName must not be empty")

  val song = Song(artistName, trackName)
}


object UserDataRecord {
  val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

  def parseDateSafely(dateString: String): Date = {
    try {
      return format.parse(dateString)
    }
    catch {
      case exc: ParseException => {
        throw new InvalidLineException(exc.getMessage)
      }
    }
  }

  //2009-04-08T01:53:56Z
  def parse(line: String): UserDataRecord = {
    val record = line.split("\\t")
    if (record.length != 6)
      throw new InvalidLineException("Line doesn't have 6 fields:" + line)

    UserDataRecord(
      userId = record(0),
      date = parseDateSafely(record(1)),
      artistId = record(2),
      artistName = record(3),
      trackId = record(4),
      trackName = record(5)
    )
  }

  def toSession(records: Iterable[UserDataRecord]): Seq[Session] = {
    require(records.map(_.userId).toSet.size == 1) //must be all records of the same user id

    val sessions: mutable.Stack[Session] = new mutable.Stack[Session]()
    records.toSeq.sortBy(_.date).foldLeft(sessions) { (currentSessions, record) =>
      if (currentSessions.isEmpty)
        currentSessions.push(Session(record))
      else if (record.date.getTime < (currentSessions.top.lastTime.getTime + 20 * 60 * 1000l)) {
        val lastSession = currentSessions.pop()
        currentSessions.push {
          lastSession.copy(lastTime = record.date, songs = lastSession.songs :+ record.song)
        }
      }
      else
        currentSessions.push(Session(record))

      currentSessions
    }

    sessions
  }
}