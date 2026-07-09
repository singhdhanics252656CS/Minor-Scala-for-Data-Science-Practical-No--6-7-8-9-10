import breeze.linalg._

object MatrixOperations {
  def main(args: Array[String]): Unit = {

    // Create two 3x3 matrices
    val matrixA = DenseMatrix(
      (2.0, 4.0, 6.0),
      (8.0, 10.0, 12.0),
      (14.0, 16.0, 18.0)
    )

    val matrixB = DenseMatrix(
      (1.0, 2.0, 3.0),
      (4.0, 5.0, 6.0),
      (7.0, 8.0, 9.0)
    )

    println(s"Matrix A:\n$matrixA")
    println(s"\nMatrix B:\n$matrixB")

    // Element-wise addition
    val addition = matrixA + matrixB
    println(s"\nAddition:\n$addition")

    // Element-wise subtraction
    val subtraction = matrixA - matrixB
    println(s"\nSubtraction:\n$subtraction")

    // Element-wise multiplication
    val multiplication = matrixA *:* matrixB
    println(s"\nElement-wise Multiplication:\n$multiplication")

    // Element-wise division
    val division = matrixA /:/ matrixB
    println(s"\nElement-wise Division:\n$division")
  }
}
