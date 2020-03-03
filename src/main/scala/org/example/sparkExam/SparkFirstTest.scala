package org.example.sparkExam

import org.apache.spark.SparkContext._
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import com.crealytics.spark.excel._
import com.crealytics.spark.excel
import org.apache.spark.{SparkConf,SparkContext}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._


object SparkFirstTest {

  def main(args: Array[String]){

    val spark = SparkSession.builder().appName("Spark-Exam").getOrCreate()

    //1. Read superstore sales data from Excel sheet
    val ordersExcel = spark.read.format("com.crealytics.spark.excel").option("sheetName", "Orders").option("header", "true").option("inferSchema", "false").load("/user/rajujslns7936/SparkTest/Sample_Superstore.xls")
    val ordersDF = ordersExcel.select(
      ordersExcel.col("Row ID").cast("integer"),
      ordersExcel.col("Order ID"),
      ordersExcel.col("Order Date").cast("timestamp"),
      ordersExcel.col("Ship Date").cast("timestamp"),
      ordersExcel.col("Ship Mode"),
      ordersExcel.col("Customer ID"),
      ordersExcel.col("Customer Name"),
      ordersExcel.col("Segment"),
      ordersExcel.col("Country"),
      ordersExcel.col("City"),
      ordersExcel.col("State"),
      ordersExcel.col("Postal Code").cast("integer"),
      ordersExcel.col("Region"),
      ordersExcel.col("Product ID"),
      ordersExcel.col("Category"),
      ordersExcel.col("Sub-Category"),
      ordersExcel.col("Product Name"),
      ordersExcel.col("Sales").cast("double"),
      ordersExcel.col("Quantity").cast("integer"),
      ordersExcel.col("Discount").cast("double"),
      ordersExcel.col("Profit").cast("double")
    )
    val returnsDF = spark.read.format("com.crealytics.spark.excel").option("sheetName", "Returns").option("header", "true").option("inferSchema", "false").load("/user/rajujslns7936/SparkTest/Returns.xls")

    //2. Find customers who returned most items after delivery in descending order and write result in excel file.

    //3.Calculate state wise total and average sales and profits write result in excel file.
    val windowSpecAgg  = Window.partitionBy("State")
    val aggDF = ordersExcel.withColumn("total_sales", sum(col("Sales")).over(windowSpecAgg)).withColumn("avg_sales", avg(col("Sales")).over(windowSpecAgg)).withColumn("total_profits", sum(col("Profit")).over(windowSpecAgg)).withColumn("avg_profits", avg(col("Profit")).over(windowSpecAgg))
    val aggResultDF = aggDF.select("State","total_sales","avg_sales","total_profits","avg_profits").distinct()
    aggResultDF.write.format("com.crealytics.spark.excel").option("sheetName", "Sheet1").option("header", "true").mode("overwrite").save("/user/rajujslns7936/SparkTest/Results/Second_Results.xlsx")

  }

}
