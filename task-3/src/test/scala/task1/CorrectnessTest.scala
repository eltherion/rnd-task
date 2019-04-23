package task1

import java.nio.file.Files

import org.scalatest._
import scalaz.effect.IO._
import task1.Main._

class CorrectnessTest extends WordSpec with Matchers with TestHelper {

  "Finding paths algorithm" should {
    "return correct paths" in {
      {
        for {
          expectedCountWithPaths <- readExpectedPaths
          (expectedCount, expectedPaths) = expectedCountWithPaths
          measured                       <- measuredEntries
          count                          =  getCount(measured.data)
          _                              <- putStrLn(s"Found $count paths in ${measured.ms} [ms]")
          tempFile                       =  Files.createTempFile("", "")
          _                              <- writeOutputFile(tempFile.toAbsolutePath.toString, measured.data, count)
          savedCountWithPaths            <- readSavedPaths(tempFile)
          (savedCount, savedPaths)       =  savedCountWithPaths
          _                              <- putStrLn(s"Comparing with expected output")
          _                              =  savedCount shouldBe expectedCount
          _                              =  theSame(savedPaths, expectedPaths)
          _                              <- putStrLn(s"Found results are the same as expected")
        } yield ()
      }.unsafePerformIO()
    }
  }
}