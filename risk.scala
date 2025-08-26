import org.apache.spark.sql.{SparkSession, DataFrame}

object risk{
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("PostgreSQL Connection").master("local[*]").getOrCreate()

    val jdbcUrl = "jdbc:postgresql://localhost:5432/postgres"
    val connectionProperties = new java.util.Properties()
    connectionProperties.setProperty("user", "postgres")
    connectionProperties.setProperty("password", "211100")
    connectionProperties.setProperty("driver", "org.postgresql.Driver")

    //for reading data from database
    //val df: DataFrame = spark.read
     // .jdbc(jdbcUrl, "(SELECT * FROM dim_hospital LIMIT 1000) as blah", connectionProperties)

    //df.show()
    val patientDF = spark.read.jdbc(jdbcUrl, "dim_patient", connectionProperties)
    val hospitalDF = spark.read.jdbc(jdbcUrl, "dim_hospital", connectionProperties)
    val diagnosisDF = spark.read.jdbc(jdbcUrl, "dim_diagnosis", connectionProperties)
    val riskDF = spark.read.jdbc(jdbcUrl, "fact_patient_risk", connectionProperties)
    patientDF.show() // only show the tables
    hospitalDF.show()
    diagnosisDF.show()
    riskDF.show()

    //join tables
    val fullDF = riskDF
      .join(patientDF, "patient_id")
      .join(hospitalDF, "hospital_id")
      .join(diagnosisDF, "diagnosis_id")

    //Data Cleaning
    val cleanedDF = fullDF.na.fill("Unknown") // Fill missing strings
      .na.fill(0) // Fill missing numbers
      .dropDuplicates()
  }
}

