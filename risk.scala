import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._
import org.apache.spark.ml.feature.StringIndexer
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.Pipeline

object risk{
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("PostgreSQL Connection").master("local[*]").getOrCreate()

    val jdbcUrl = "jdbc:postgresql://localhost:5432/postgres"
    val connectionProperties = new java.util.Properties()
    connectionProperties.setProperty("user", "postgres")//give your user
    connectionProperties.setProperty("password", "211100")//give your password
    connectionProperties.setProperty("driver", "org.postgresql.Driver")

    //for reading data from database
    //val df: DataFrame = spark.read
     // .jdbc(jdbcUrl, "(SELECT * FROM dim_hospital LIMIT 1000) as blah", connectionProperties)

    //df.show()
    val patientDF = spark.read.jdbc(jdbcUrl, "dim_patient", connectionProperties)
    val hospitalDF = spark.read.jdbc(jdbcUrl, "dim_hospital", connectionProperties)
    val diagnosisDF = spark.read.jdbc(jdbcUrl, "dim_diagnosis", connectionProperties)
    val riskDF = spark.read.jdbc(jdbcUrl, "fact_patient_risk", connectionProperties)
    patientDF.show()
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

    //filter data function
    val filteredDF = cleanedDF.filter(col("age") > 0 && col("risk_score").isNotNull)

    //A pipeline is a sequence of steps:
    //StringIndexer: Converts categorical data to numbers.
    //VectorAssembler: Combines features into a single vector.
    //LogisticRegression: The actual ML algorithm used for classification.
    val genderIndexer = new StringIndexer()
      .setInputCol("gender")
      .setOutputCol("gender_index")
      .setHandleInvalid("keep") // This avoids crashing on unseen labels

    val hospitalTypeIndexer = new StringIndexer()
      .setInputCol("type")
      .setOutputCol("hospital_type_index")
      .setHandleInvalid("keep") // Same here


    val lr = new LogisticRegression()
      .setLabelCol("is_high_risk")
      .setFeaturesCol("features")

    val assembler = new VectorAssembler()
      .setInputCols(Array("age", "gender_index", "hospital_type_index", "risk_score"))
      .setOutputCol("features")

    val pipeline = new Pipeline().setStages(Array(genderIndexer, hospitalTypeIndexer, assembler, lr))

    // Assuming cleanedDF is already defined
    val featureDF = cleanedDF
      .withColumn("is_high_risk", when(col("risk_score") > 0.7, 1).otherwise(0))

    //You split the data into:
    //Training data (80%): Used to teach the model.
    //Test data (20%): Used to evaluate how well the model performs on unseen data.
    val Array(trainingData, testData) = featureDF.randomSplit(Array(0.8, 0.2), seed = 1234L)

    // Train the model
    val model = pipeline.fit(trainingData)

    // Use the model to make predictions
    val predictions = model.transform(testData)

    predictions.select("patient_id", "risk_score", "prediction", "probability").show()
  }
}

