import org.apache.spark.sql.{SparkSession, DataFrame}

object risk{
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("PostgreSQL Connection").master("local[*]").getOrCreate()

    val jdbcUrl = "jdbc:postgresql://localhost:5432/postgres"
    val connectionProperties = new java.util.Properties()
    connectionProperties.setProperty("user", "postgres")
    connectionProperties.setProperty("password", "211100")
    connectionProperties.setProperty("driver", "org.postgresql.Driver")

    val df: DataFrame = spark.read
      .jdbc(jdbcUrl, "(SELECT * FROM dim_hospital LIMIT 1000) as blah", connectionProperties)

    df.show()
  }
}

