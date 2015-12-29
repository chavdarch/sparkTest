package com.sony.chavdar.assignment

import java.io.PrintWriter

object PartaApp {

  //Create a	list	of	user IDs,	along	with	the	number	of	distinct	songs	each	user	has	played.
  def main(args: Array[String]) {
    withWriter(new PrintWriter("parta.tsv", "UTF-8")) { writer =>
      writer.println(s"#userId\tnumberOfSongs")
      dropHeader(userData).
        map(UserDataRecord.parse).
        map(r => (r.userId, r.song)).
        distinct().
        groupBy(_._1).
        mapValues(_.size).
        collect().foreach {
          case (k, v) => writer.println(s"$k\t$v")
        }
    }
  }


}