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
    val numberOfInputs = 10000
    val groupSize = 1000
    val allElements = (1 to numberOfInputs).toList
      .grouped(groupSize).toList

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

    Future.traverse(allElements){ group =>
      Future.traverse(group) { element =>
        Future(queue.insert(element))
      }
    }
      .map(_ => takeUntilEmpty(queue.isEmpty, List.empty[Integer]))
      .map {
        case (_, allTaken) =>
          val aggregatedAllTaken = allTaken.flatMap(Option(_))
          val expected = allElements.flatten
          aggregatedAllTaken should contain theSameElementsAs expected

          queue.size shouldBe 0
          queue.isEmpty shouldBe true
          Option(queue.deleteMin()) shouldBe None
      }
  }
}
