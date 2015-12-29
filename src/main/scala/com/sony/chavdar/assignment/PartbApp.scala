package com.sony.chavdar.assignment

import java.io.PrintWriter

object PartbApp {
  //Create	a	list	of	the	100	most	popular	songs (artist	and	title) in	the	dataset,	with	the	number	of	times
  //each	was	played.
  def main(args: Array[String]) {
    withWriter(new PrintWriter("partb.tsv", "UTF-8")) { writer =>
      writer.println(s"#artistName\ttrackName\tcount")

      dropHeader(userData).
        map(UserDataRecord.parse).
        groupBy(_.song).
        map {
          case (k, v) => k -> v.size
        }.top(100) {
        new Ordering[(Song, Int)]() {
          override def compare(x: (Song, Int), y: (Song, Int)): Int = x._2.compareTo(y._2)
        }
      }.foreach {
        case ( Song(artist, track), count) => writer.println(s"$artist\t$track\t$count")
      }
    }
  }
}

