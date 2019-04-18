package review

//import donotmodifyme.Scenario1._

import scala.util.Random

object Scenario1 {
  /*
   * Given a `blackBoxPositiveInt` 
   *  - a black box procedure returning `Int` that's outside of our control (i.e an external library call
   * or a call to a service)
   *
   * @return the amount of calls to `blackBoxPositiveInt` needed, so that the sum of all returned values from
   * `blackBoxPositiveInt` would be equal to @input `total` 
   *
   * blackBoxPositiveInt:
   *  - can side effect
   *  - is delivered by a third party, we don't know how it operates
   */

  private val r = new Random()

  private def blackBoxPositiveInt = r.nextInt(Int.MaxValue) + 1

  def process(total: Int): Int = {
    helper(total, 0)
  }

  def helper(total: Int, n: Int): Int = {
    if (total == 0) n
    else {
      helper(total - blackBoxPositiveInt, n + 1)
    }
  }

  /*This implementation is so wrong I don't know where to start from. :-)
  *
  * 1. The code of `helper` function somehow 'assumes' that `total` parameter is non-negative integer.
  * Actually, I see no restrictions about value of `total` parameter for `process` function. So it can also be any
  * negative integer value within the range of [Int.Min ... -1] including both marginal values. When I invoke `process(Int.Min)`
  * it is passed to `helper` method and, as it is guaranteed that `blackBoxPositiveInt` returns positive integer,
  * in the execution time there will be substracting performed on Int.Min, which obviously causes underflow.
  *
  * 2. Putting aside machine constraints and integer values representation, from the mathematics point of view this is
  * impossible for the negative integer to be represented as sum of positive integers. If we had a computer machine
  * without physical constraints, function `helper` would never end.
  *
  * 3. Currently `process` behaves correctly only for 0 input value as it ends on `if (total == 0) n`.
  *
  * 4. It seems that author of that code didn't noticed that value of expression total - blackBoxPositiveInt may not be
  * always equal or above zero and that difference may not decrease up to zero which is a stop condition for recursive
  * `helper` funtion. In fact we cannot assume anything about value of total - blackBoxPositiveInt. If we are unlucky
  *  enough that difference will bounce between positive and negative values (according to under- and overflowing Int
  *  values) and we will end up with stack overflow.
  *
  * 5. Apart correctness of above code it's worth to mention `helper` could be annotated with @tailrec. It will
  * prevent stack overflow but, if we again are unlucky enough, it may end up in neverending waiting for result.
  *
  * 6. If it would be possible, I would have searched for different library/implementation of blackBoxPositiveInt which
  * at least takes positive n as a parameter and returns, for example, positive output in range of [1...n]. Then `process` could have
  * basic input validation:
  *   def process(total: Int): Try[Int] = {
  *     if (total < 0)
  *       Failure(...)
  *    else
  *       Try(helper(total, 0))
  *   }
  *
  * 7. In fact we can be nearly sure, that `helper`, and consequently `process` functions are not pure, because,
  * apart 0, for the same input most probably it will return totally different results on next invocations. This
  * impure behaviour stems from uncertainty about `blackBoxPositiveInt`. Actually there is at least one implementation
  * of `blackBoxPositiveInt` that could work for positive input:
  *   def blackBoxPositiveInt: Int = 1
  * In that case for any n >=0 process(n) = n;
  * */
}
