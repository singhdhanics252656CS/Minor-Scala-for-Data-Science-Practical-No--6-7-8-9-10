import com.github.tototoshi.csv._
import java.io.File
import scala.util.Try

object Pract_9_MissngValues {
  def main(args: Array[String]): Unit = {
    val inputFile = new File("titanic.csv")
    val reader = CSVReader.open(inputFile)

    val allRows = reader.allWithHeaders()
    reader.close()

    val numericColumns = Seq("Age", "Fare") // update this based on your titanic.csv file

    // Step 1: Compute means and missing counts
    val stats: Map[String, (Double, Int)] = numericColumns.map { col =>
      val values = allRows.map(row => row.getOrElse(col, "").trim)
      val validNumbers = values.flatMap(v => Try(v.toDouble).toOption)
      val missingCount = values.count(v => Try(v.toDouble).isFailure)
      val mean = if (validNumbers.nonEmpty) validNumbers.sum / validNumbers.size else 0.0
      (col, (mean, missingCount))
    }.toMap

    // Step 2: Print stats for each column
    println("\n--- Missing Data Report ---")
    stats.foreach { case (col, (mean, missingCount)) =>
      println(f"Column: $col, Missing values: $missingCount, Replaced with mean: $mean%.2f")
    }

    // Step 3: Replace missing values
    val cleanedRows = allRows.map { row =>
      numericColumns.foldLeft(row) { (accRow, col) =>
        val value = accRow.getOrElse(col, "").trim
        val replaced = Try(value.toDouble).toOption match {
          case Some(_) => value // valid number
          case None    => f"${stats(col)._1}%.2f" // replace with mean
        }
        accRow.updated(col, replaced)
      }
    }

    // Step 4: Save cleaned CSV
    val outputFile = new File("titanic_cleaned.csv")
    val writer = CSVWriter.open(outputFile)
    val headers = cleanedRows.head.keys.toSeq
    writer.writeRow(headers)
    cleanedRows.foreach(row => writer.writeRow(headers.map(row)))
    writer.close()

    println("\n Missing values replaced and saved to titanic_cleaned.csv")
  }
}
