package task2

import org.scalatest.{AsyncFreeSpec, Matchers}

import scala.annotation.tailrec
import scala.concurrent.Future

class MultiQueueTest extends AsyncFreeSpec with Matchers {
  "An empty queue has correct size and state" in {
    val emptyQueue = MultiQueue.empty(2)
    emptyQueue.size shouldBe 0
    emptyQueue.isEmpty shouldBe true
    Option(emptyQueue.deleteMin()) shouldBe None
  }

  "A not empty queue preserves correct properties while growing" in {
    val queue = MultiQueue.empty[Integer](2)
    queue.insert(1)
    queue.size shouldBe 1
    queue.isEmpty shouldBe false

    queue.insert(2)
    queue.size > 0 shouldBe true
    queue.isEmpty shouldBe false

    queue.insert(3)
    queue.size > 0 shouldBe true
    queue.isEmpty shouldBe false
  }

  "A not empty queue behaves correctly in multi-threaded environment" in {
    val numberOfInputs = 1000000
    val groupSize = 100000
    val allElements = (1 to numberOfInputs).toList
      .grouped(groupSize).toList
    val expected = allElements.flatten.toSet

    val queue = MultiQueue.empty[Integer](2)


    @tailrec
    def takeUntilEmpty(isEmpty: Boolean, takenSoFar: List[Integer]): (Boolean, List[Integer]) = {
      if (isEmpty) {
        (isEmpty, takenSoFar)
      } else {
        val elements = queue.deleteMin() :: takenSoFar
        val isEmpty = queue.isEmpty
        takeUntilEmpty(isEmpty, elements)
      }
    }

    val fInput = Future.traverse(allElements){ group =>
      Future.traverse(group) { element =>
        Future(queue.insert(element))
      }
    }

    Future.traverse(allElements){ _ =>
      Future(takeUntilEmpty(queue.isEmpty, List.empty[Integer]))
    }.map {
      _.flatMap {
        case (_, taken) => taken
      }
    }.zip(fInput).map {
      case (allTaken, _) =>
        val aggregatedAllTaken = allTaken.flatMap(Option(_))
        aggregatedAllTaken.toSet shouldBe expected

        queue.size shouldBe 0
        queue.isEmpty shouldBe true
        Option(queue.deleteMin()) shouldBe None
    }
  }
}
