package task2

import java.util.concurrent.atomic.AtomicIntegerArray

import scala.annotation.tailrec
import scala.util.Random

class MultiQueue[A >: Null] private (
  // nQueues is guaranteed to be at least 2
  nQueues: Int
  // other arguments here if needed
  )(implicit ordering: Ordering[A]) {

  private val underlyingQueues = (1 to nQueues).map(_ => Queue.empty[A]).toArray
  private val locks = new AtomicIntegerArray(nQueues)
  private val random = new Random()

  def isEmpty: Boolean = underlyingQueues.forall(_.isEmpty)
  /*Really "best effort" which is not accurate most of the time.*/
  def size: Int        = underlyingQueues.count(!_.isEmpty)

  def insert(element: A): Unit = {
    val initialTriedIndex = random.nextInt(nQueues)
    val (effectivelyLockedIndex, _) =
      lockOn(
        index = initialTriedIndex,
        locked = locks.compareAndSet(initialTriedIndex, 0, 1)
      )
    underlyingQueues(effectivelyLockedIndex).enqueue(element)
    val _ = locks.compareAndSet(effectivelyLockedIndex, 1, 0)
  }

  @tailrec
  private def lockOn(index: Int, locked: Boolean): (Int, Boolean) = {
    if (locked) {
      (index, locked)
    } else {
      val nextTriedIndex = random.nextInt(nQueues)
      lockOn(nextTriedIndex, locks.compareAndSet(nextTriedIndex, 0, 1))
    }
  }

  /*
   * Smallest elements (non-strictly) first.
   */
  def deleteMin(): A = {
    val initialTriedIndexI = random.nextInt(nQueues)
    val (effectivelyLockedIndex, _, _) =
      lockOn(
        indexI = initialTriedIndexI,
        indexJ = random.nextInt(nQueues),
        locked = locks.compareAndSet(initialTriedIndexI, 0, 1)
      )
    val result = underlyingQueues(effectivelyLockedIndex).deleteMin()
    locks.compareAndSet(effectivelyLockedIndex, 1, 0)
    result
  }

  @tailrec
  private def lockOn(indexI: Int, indexJ: Int, locked: Boolean): (Int, Int, Boolean) = {
    if (locked) {
      (indexI, indexJ, locked)
    } else {
      val nextTriedIndexI = random.nextInt(nQueues)
      val nextTriedIndexJ = random.nextInt(nQueues)
      val elementFromI = underlyingQueues(nextTriedIndexI).peekMin
      val elementFromJ = underlyingQueues(nextTriedIndexJ).peekMin
      (elementFromI, elementFromJ) match {
        case (null, null) =>
          (nextTriedIndexI, nextTriedIndexJ, true)
        case (null, _) =>
          (nextTriedIndexJ, nextTriedIndexI, true)
        case (_, null) =>
          (nextTriedIndexI, nextTriedIndexJ, true)
        case _ =>
          if (ordering.compare(elementFromI, elementFromJ) > 0) {
            lockOn(nextTriedIndexJ, nextTriedIndexI, locks.compareAndSet(nextTriedIndexJ, 0, 1))
          } else {
            (nextTriedIndexI, nextTriedIndexJ, locks.compareAndSet(nextTriedIndexJ, 0, 1))
          }
      }
    }
  }
}

object MultiQueue {
  // You can ignore the scaling factor and the actuall amount of processors just use the given nQueues.
  def empty[A >: Null](nQueues: Int)(implicit ordering: Ordering[A]): MultiQueue[A] = {
    new MultiQueue(math.max(2, nQueues))
  }
}
