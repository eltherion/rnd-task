package design

import eu.timepit.refined._
import eu.timepit.refined.api._
import eu.timepit.refined.auto._
import eu.timepit.refined.collection._
import eu.timepit.refined.numeric._
import eu.timepit.refined.string._

import scala.util._

object Scenario3 {
  /**
    * Write a class to represent and create a user
    *
    * - User has a name/surname
    * - User has a username
    * - Username can only consist of characters "[a-z][A-Z][0-9]-._"
    * - User has a level
    * - User starts from level 0 and can only increase.
    * - User has experience
    * - User gets experience each time he posts or is reposted.
    * - The experience transfers to levels on midnight each day
    * - The experience can't ever be negative
    * - An user is either a free user or a paid user.
    * - A free user has a limit to the amount of posts he can write per day.
    * - A paid user has a counter of the remaining paid days
    */
  sealed abstract class User {
    val name: String Refined NonEmpty

    val surname: String Refined NonEmpty

    val username: String Refined MatchesRegex[W.`"[a-zA-Z0-9-._]+"`.T]

    val postsCounter: Int Refined NonNegative

    val level: Int Refined NonNegative

    val experience: Int Refined NonNegative

    private[design] def levelUp: Either[String, User]

    private val nextLevelExp: Int Refined NonNegative = 1000

    /* 1) give a level for each 1000exp, the remaining experience goes to the next day */
    protected def nextLevelAndExperience = {
      val nextLevel = level.value + experience.value / nextLevelExp.value
      val nextExperience = experience.value % nextLevelExp.value
      (
        refineV[NonNegative](nextLevel),
        refineV[NonNegative](nextExperience)
      )
    }
  }

  object User {

    final case class FreeUser private(name: String Refined NonEmpty,
                                      surname: String Refined NonEmpty,
                                      username: String Refined MatchesRegex[W.`"[a-zA-Z0-9-._]+"`.T],
                                      postsCounter: Int Refined NonNegative,
                                      level: Int Refined NonNegative,
                                      experience: Int Refined NonNegative,
                                      dailyPostLimit: Int Refined NonNegative
                                     ) extends User {

      def levelUp: Either[String, User] = {
        val (nextLevelE, nextExperienceE) = nextLevelAndExperience
        for {
          nextLevel <- nextLevelE
          nextExperience <- nextExperienceE
        } yield copy(
          level = nextLevel,
          experience = nextExperience,
          dailyPostLimit = refreshedPostsLimit
        )
      }

      /* 2) if a free user is under 3 posts refresh the number of posts he can publish to 3 */
      private def refreshedPostsLimit: Int Refined NonNegative = {
        if (dailyPostLimit.value < 3) {
          refineMV[NonNegative](3)
        } else {
          dailyPostLimit
        }
      }
    }

    final case class PaidUser private(name: String Refined NonEmpty,
                                      surname: String Refined NonEmpty,
                                      username: String Refined MatchesRegex[W.`"[a-zA-Z0-9-._]+"`.T],
                                      postsCounter: Int Refined NonNegative,
                                      level: Int Refined NonNegative,
                                      experience: Int Refined NonNegative,
                                      paidDaysRemaining: Int Refined NonNegative
                                     ) extends User {
      def levelUp: Either[String, User] = {
        val (nextLevelE, nextExperienceE) = nextLevelAndExperience

        for {
          nextLevel <- nextLevelE
          nextExperience <- nextExperienceE
          decreased <- decreasedPaidDaysRemaining
        } yield copy(
          level = nextLevel,
          experience = nextExperience,
          paidDaysRemaining = decreased
        )
      }

      /* 3) paid users reduce their days remaining count */
      private def decreasedPaidDaysRemaining: Either[String, Int Refined NonNegative] = {
        if (paidDaysRemaining.value > 0) {
          refineV[NonNegative](paidDaysRemaining.value - 1)
        } else {
          Right(paidDaysRemaining)
        }
      }
    }

    def createFreeUser(name: String Refined NonEmpty,
                       surname: String Refined NonEmpty,
                       username: String Refined MatchesRegex[W.`"[a-zA-Z0-9-._]+"`.T],
                       dailyPostLimit: Int Refined NonNegative
                      ): FreeUser = {
      FreeUser(
        name,
        surname,
        username,
        postsCounter = 0,
        level = 0,
        experience = 0,
        dailyPostLimit
      )
    }

    def createPaidUser(name: String Refined NonEmpty,
                       surname: String Refined NonEmpty,
                       username: String Refined MatchesRegex[W.`"[a-zA-Z0-9-._]+"`.T],
                       paidDaysRemaining: Int Refined NonNegative
                      ): PaidUser = {
      PaidUser(
        name,
        surname,
        username,
        postsCounter = 0,
        level = 0,
        experience = 0,
        paidDaysRemaining
      )
    }
  }
  object UserLogic {
    /*
     * This logic will be run each midnight every day. It should:
     *   1) give a level for each 1000exp, the remaining experience goes to the next day
     *   2) if a free user is under 3 posts refresh the number of posts he can publish to 3
     *   3) paid users reduce their days remaining count
     *
     * Other functions (that you don't need to write) modify the amount of posts a user can
     * still post when they post, give experience for posts, might increase or decrease a free users limit
     * etc.
     */

    def runAtMidnight(user: User): User = {
      user
        .levelUp
        .fold(scala.sys.error, identity)
    }
  }
}
