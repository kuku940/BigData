package cn.xiaoyu.spark

import cn.xiaoyu.spark.common.Constant
import org.apache.spark.{SparkConf, SparkContext}

import java.io.File

object WordCount {
  def main(args: Array[String]): Unit = {
    var masterUrl = "local[2]"
    var inputPath = Constant.LOCAL_FILE_PREX + "/spark/data/resources/wc_data"
    var outputPath = "tmp/scala_wc_result"

    if (args.length == 1) masterUrl = args(0) else if (args.length == 3) {
      masterUrl = args(0)
      inputPath = args(1)
      outputPath = args(2)
    }

    println(s"masterUrl:$masterUrl, inputPath: $inputPath, outputPath: $outputPath")
    val sparkConf = new SparkConf().setMaster(masterUrl).setAppName(this.getClass.getName)
    val sc = new SparkContext(sparkConf)

    val rowRdd = sc.textFile(inputPath)
    val resultRdd = rowRdd.flatMap(line => line.split("\\s+"))
      .map(word => (word, 1)).reduceByKey(_ + _)

    val file = new File(outputPath)
    if (file.exists()) {
      println(file.getPath)
      dirDel(file)
    }

    resultRdd.saveAsTextFile(outputPath)

    val tuples = resultRdd.collect()
    for (a <- tuples) {
      println(a)
    }
  }

  /**
   * 删除目录和文件
   */
  def dirDel(path: File) {
    if (!path.exists())
      return
    else if (path.isFile) {
      path.delete()
      println(path + ":  文件被删除")
      return
    }

    val file = path.listFiles()
    for (d <- file) {
      dirDel(d)
    }

    path.delete()
    println(path + ":  目录被删除")
  }
}
