import com.github.tototoshi.csv._
import java.io.File

object FilterRowThreshold {

  def main(args: Array[String]): Unit = {

    val reader = CSVReader.open(new File("Table_1.csv"))
    val data = reader.allWithHeaders()
    reader.close()

    // Column name as it appears in the CSV header
    val filterColumn = "Experience (YY.MM)"
    val threshold = 5.0

    // Filter rows where Experience > threshold
    // (blank values and non-numeric entries like "NTBD" are safely skipped via toDoubleOption)
    val filteredRows = data.filter { row =>
      row.get(filterColumn).exists(value => value.trim.toDoubleOption.exists(_ > threshold))
    }

    println("=" * 100)
    println(s"          EMPLOYEE DATASET - FILTER BY $filterColumn > $threshold YEARS")
    println("=" * 100)

    println(s"\nTotal Rows with $filterColumn > $threshold: ${filteredRows.length}\n")

    if (filteredRows.nonEmpty) {

      // Use a fixed header order (Map iteration order isn't guaranteed to match across rows)
      val headers = data.head.keys.toList

      // Compute the widest value (including the header itself) for each column
      val columnWidths = headers.map { col =>
        val maxDataLen = filteredRows.map(row => row.getOrElse(col, "").length).max
        col -> math.max(col.trim.length, maxDataLen)
      }.toMap

      // Print header row, each column padded to its computed width
      println(headers.map(h => h.trim.padTo(columnWidths(h), ' ')).mkString("  "))
      println("-" * (columnWidths.values.sum + headers.length * 2))

      // Print filtered rows using the same fixed header order and widths
      filteredRows.foreach { row =>
        println(headers.map(h => row.getOrElse(h, "").padTo(columnWidths(h), ' ')).mkString("  "))
      }

      // --- Summary statistics on the filtered rows' Experience values ---
      val expValues = filteredRows.flatMap(_.get(filterColumn).flatMap(_.trim.toDoubleOption))

      println("\n" + "=" * 100)
      println(s"$filterColumn SUMMARY (filtered rows)")
      println("=" * 100)
      println(f"Minimum : ${expValues.min}%.2f years")
      println(f"Maximum : ${expValues.max}%.2f years")
      println(f"Average : ${expValues.sum / expValues.size}%.2f years")

      // --- Feature: breakdown of Stay vs Left within the filtered rows ---
      val stayCount = filteredRows.count(_.get("Stay/Left").contains("Stay"))
      val leftCount = filteredRows.count(_.get("Stay/Left").contains("Left"))

      println("\n" + "-" * 100)
      println("STAY / LEFT BREAKDOWN (filtered rows)")
      println("-" * 100)
      println(f"Stay : $stayCount%-6d (${stayCount * 100.0 / filteredRows.length}%.1f%%)")
      println(f"Left : $leftCount%-6d (${leftCount * 100.0 / filteredRows.length}%.1f%%)")

      // --- Feature: export filtered rows to a new CSV file ---
      val outputFile = "filtered_high_experience.csv"
      val writer = CSVWriter.open(new File(outputFile))
      writer.writeRow(headers)
      filteredRows.foreach { row =>
        writer.writeRow(headers.map(h => row.getOrElse(h, "")))
      }
      writer.close()

      println(s"\nFiltered rows exported to: $outputFile")

    } else {
      println("No rows found matching the given threshold.")
    }

    println("\n" + "=" * 100)
    println("Filtering completed successfully!")
    println("=" * 100)
  }
}