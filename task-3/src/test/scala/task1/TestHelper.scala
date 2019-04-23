package task1

import java.io.FileInputStream
import java.nio.file.Path
import java.util.zip.GZIPInputStream

import org.scalatest.{Assertion, Matchers}
import scalaz.effect.IO
import scalaz.effect.IO.putStrLn
import task1.Main.{Measured, Problem, getEntries}
import task1.modifyme.FoundEntries

import scala.io.Source
import scala.util.Try

trait TestHelper extends Matchers {
  protected final def readExpectedPaths: IO[(Int, Seq[String])] = {
    IO {
      val inputStream = new GZIPInputStream(getClass.getResourceAsStream("/paths.dat.gz"))
      val lines = Source.fromInputStream(inputStream).getLines().toList
      val linesCount = lines.head.toInt

      (linesCount, lines.drop(2))
    }
  }

  protected final def measuredEntries: IO[Measured[Seq[FoundEntries[Int, Int]]]] = for {
    graph        <- GenericGraph.intIntFromFile(getClass.getResource("/connections-small.dat").getPath)
    problem      <- readProblemDescription
    t1           <- IO { System.currentTimeMillis }
    _            <- putStrLn("Calculating paths...")
    entries      =  getEntries(graph, problem)
    t2           <- IO { System.currentTimeMillis }
  } yield Measured(t2 - t1, entries)

  protected final def readSavedPaths(tempFile: Path): IO[(Int, Seq[String])] = {
    IO {
      val inputStream = new FileInputStream(tempFile.toAbsolutePath.toString)
      val lines = Source.fromInputStream(inputStream).getLines().toList
      val linesCount = lines.head.toInt

      (linesCount, lines.drop(2))
    }
  }

  protected final def theSame(savedPaths: Seq[String], expectedPaths: Seq[String]): Assertion = {
    def toSet(paths: Seq[String]) = {
      paths.filter(_.nonEmpty).map {
        _.split(" ")
      }.flatMap { row =>
        Try((row(0), row(1), row(row.length - 1)))
          .fold[Option[(String, String, String)]](_ => None, Option.apply)
      }.toSet
    }

    toSet(savedPaths) shouldBe toSet(expectedPaths)
  }

  private def readProblemDescription: IO[Problem] = IO {

    val lines = scala.io.Source.fromResource("pois-small.dat").getLines

    val timeLimit = lines.next.trim.toInt

    val pois = lines.next.split("\\s").par.map { string =>
      string.toInt
    }.toArray

    Problem(timeLimit, pois)
  }
}
