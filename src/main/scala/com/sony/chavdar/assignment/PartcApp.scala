package com.sony.chavdar.assignment

import java.io.PrintWriter

object PartcApp {
  //Say	we define	a	user’s	“session”	of	Last.fm	usage	to	be	comprised	of	one	or	more	songs played	by	that
  //user,	where	each	song	is	started	within	20	minutes	of	the	previous	song’s	start	time.		Create	a	list	of	the
  //top	10 longest	sessions,	with	the	following	information	about	each	session:		userid,	timestamp	of	first
  //and	last	songs	in	the	session,	and	the	list	of	songs	played	in	the	session	(in	order	of	play).
  def main(args: Array[String]) {
    withWriter(new PrintWriter("partc.tsv", "UTF-8")) { writer =>
      writer.println(s"userId\tfirstTime\tlastTime\tsongs")

      //sort by date
      //generate sliding windows
      dropHeader(userData).
        map(UserDataRecord.parse).
        groupBy(r => r.userId).
        flatMap{
          case(k,v) => UserDataRecord.toSession(v)
        }.
        top(10) {
        new Ordering[Session]() {
          override def compare(x: Session, y: Session): Int = x.duration.compareTo(y.duration)
        }
      }.foreach(s => writer.println(s.asTsvLine))
    }
  }
}

