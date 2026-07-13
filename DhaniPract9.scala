import com.github.tototoshi.csv._
import java.io.File
import scala.util.Try

object MissingValueHandling {

  // Function to calculate mean of a numeric column
  def calculateMean(data: List[Map[String, String]], column: String): Double = {

    val values = data.flatMap { row =>
      row.get(column).flatMap(value => Try(value.toDouble).toOption)
    }

    if (values.nonEmpty)
      values.sum / values.size
    else
      0.0
  }


  def main(args: Array[String]): Unit = {

    // Reading CSV file from your location
    val reader = CSVReader.open(
      new File("C:\\ScalaForDS\\csvcodes9and10\\Dhani9and10\\medicine_dataset.csv")
    )

    val data = reader.allWithHeaders()
    reader.close()


    println("=" * 80)
    println("          MEDICINE DATASET - MISSING VALUE HANDLING")
    println("=" * 80)


    // Detect numeric columns automatically
    val numericColumns = data.head.keys.filter { column =>
      data.exists(row =>
        row.get(column).exists(value => Try(value.toDouble).isSuccess)
      )
    }.toSeq


    // Calculate mean values
    val columnMeans = numericColumns.map { column =>
      column -> calculateMean(data, column)
    }.toMap


    // Replace missing values
    val updatedData = data.map { row =>

      numericColumns.foldLeft(row) { (currentRow, column) =>

        val value = currentRow.getOrElse(column, "")

        if (value.trim.isEmpty || value.equalsIgnoreCase("NA")
          || value.equalsIgnoreCase("null")) {

          currentRow.updated(
            column,
            f"${columnMeans(column)}%.2f"
          )

        } else {
          currentRow
        }
      }
    }


    // Display column statistics
    println("\nCOLUMN STATISTICS")
    println("-" * 80)
    println(f"${"Column"}%-25s ${"Missing Values"}%-20s ${"Mean"}%-15s")
    println("-" * 80)


    numericColumns.foreach { column =>

      val missingCount = data.count { row =>
        val value = row.getOrElse(column, "")
        value.trim.isEmpty ||
          value.equalsIgnoreCase("NA") ||
          value.equalsIgnoreCase("null")
      }

      println(
        f"$column%-25s $missingCount%-20d ${columnMeans(column)}%-15.2f"
      )
    }


    // Display updated dataset (fixed alignment)
    println("\n" + "=" * 80)
    println("UPDATED DATASET")
    println("=" * 80)

    // Use a fixed header order (Map iteration order isn't guaranteed to match across rows)
    val headers = updatedData.head.keys.toList

    // Compute the widest value (including the header itself) for each column
    val columnWidths = headers.map { col =>
      val maxDataLen = updatedData.map(row => row.getOrElse(col, "").length).max
      col -> math.max(col.length, maxDataLen)
    }.toMap

    // Print header row, each column padded to its computed width
    println(headers.map(h => h.padTo(columnWidths(h), ' ')).mkString("  "))
    println("-" * (columnWidths.values.sum + headers.length * 2))

    // Print data rows using the same fixed header order and widths
    updatedData.foreach { row =>
      println(headers.map(h => row.getOrElse(h, "").padTo(columnWidths(h), ' ')).mkString("  "))
    }


    println("\n" + "=" * 80)
    println("Missing values replaced successfully!")
    println("=" * 80)
  }
}