package com.sony.chavdar

import java.io.PrintWriter

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by cchernashki on 20/12/2015.
 */
package object assignment {

  def userData: RDD[String] = {
    val logFile = getClass.getClassLoader.getResource("userid-timestamp-artid-artname-traid-traname.tsv").toExternalForm // Should be some file on your system
    val conf = new SparkConf().setMaster("local").setAppName("Simple Application")
    val sc = new SparkContext(conf)

    sc.textFile(logFile, 2).cache()
  }


  //first partition will contain the first line and strip it out that way
  def dropHeader(data: RDD[String]): RDD[String] = {
    data.mapPartitionsWithIndex((idx, lines) => {
      if (idx == 0) {
        lines.drop(1)
      }
      lines
    })
  }

  def withWriter(writer: PrintWriter)(action : PrintWriter => Unit): Unit = {
    try{
      action(writer)
    }finally {
      writer.close()
    }
  }


}
