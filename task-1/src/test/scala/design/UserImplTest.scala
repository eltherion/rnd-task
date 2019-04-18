package design

import design.Scenario3.User._
import eu.timepit.refined._
import eu.timepit.refined.collection._
import eu.timepit.refined.numeric.NonNegative
import eu.timepit.refined.string._
import org.scalatest.{FreeSpec, Matchers}

class UserImplTest extends FreeSpec with Matchers {
  "only valid free user can be created" in {
    val nameStr = "Name"
    val surnameStr = "Surname"
    val usernameStr = "user.name_1"
    val dailyPostLimitInt = 3
    val freeUser = for {
      name <- refineV[NonEmpty](nameStr)
      surname <- refineV[NonEmpty](surnameStr)
      username <- refineV[MatchesRegex[W.`"[a-zA-Z0-9-._]+"`.T]](usernameStr)
      dailyPostLimit <- refineV[NonNegative](dailyPostLimitInt)
    } yield createFreeUser(
      name = name,
      surname = surname,
      username = username,
      dailyPostLimit = dailyPostLimit
    )

    freeUser.isRight shouldBe true

    freeUser.map { user =>
      user.name.value shouldBe nameStr
      user.surname.value shouldBe surnameStr
      user.username.value shouldBe usernameStr
      user.dailyPostLimit.value shouldBe dailyPostLimitInt
    }
  }

  "free user with invalid name cannot be created" in {
    val nameStr = ""
    val surnameStr = "Surname"
    val usernameStr = "user.name_1"
    val dailyPostLimitInt = 3
    val freeUser = for {
      name <- refineV[NonEmpty](nameStr)
      surname <- refineV[NonEmpty](surnameStr)
      username <- refineV[MatchesRegex[W.`"[a-zA-Z0-9-._]+"`.T]](usernameStr)
      dailyPostLimit <- refineV[NonNegative](dailyPostLimitInt)
    } yield createFreeUser(
      name = name,
      surname = surname,
      username = username,
      dailyPostLimit = dailyPostLimit
    )

    freeUser.isRight shouldBe false
  }

  "free user with invalid surname cannot be created" in {
    val nameStr = "Name"
    val surnameStr = ""
    val usernameStr = "user.name_1"
    val dailyPostLimitInt = 3
    val freeUser = for {
      name <- refineV[NonEmpty](nameStr)
      surname <- refineV[NonEmpty](surnameStr)
      username <- refineV[MatchesRegex[W.`"[a-zA-Z0-9-._]+"`.T]](usernameStr)
      dailyPostLimit <- refineV[NonNegative](dailyPostLimitInt)
    } yield createFreeUser(
      name = name,
      surname = surname,
      username = username,
      dailyPostLimit = dailyPostLimit
    )

    freeUser.isRight shouldBe false
  }

  "free user with invalid username cannot be created" in {
    val nameStr = "Name"
    val surnameStr = "Surname"
    val usernameStr = ""
    val dailyPostLimitInt = 3
    val freeUser = for {
      name <- refineV[NonEmpty](nameStr)
      surname <- refineV[NonEmpty](surnameStr)
      username <- refineV[MatchesRegex[W.`"[a-zA-Z0-9-._]+"`.T]](usernameStr)
      dailyPostLimit <- refineV[NonNegative](dailyPostLimitInt)
    } yield createFreeUser(
      name = name,
      surname = surname,
      username = username,
      dailyPostLimit = dailyPostLimit
    )

    freeUser.isRight shouldBe false
  }

  "free user with invalid daily post limit cannot be created" in {
    val nameStr = "Name"
    val surnameStr = "Surname"
    val usernameStr = "user.name_1"
    val dailyPostLimitInt = -1
    val freeUser = for {
      name <- refineV[NonEmpty](nameStr)
      surname <- refineV[NonEmpty](surnameStr)
      username <- refineV[MatchesRegex[W.`"[a-zA-Z0-9-._]+"`.T]](usernameStr)
      dailyPostLimit <- refineV[NonNegative](dailyPostLimitInt)
    } yield createFreeUser(
      name = name,
      surname = surname,
      username = username,
      dailyPostLimit = dailyPostLimit
    )

    freeUser.isRight shouldBe false
  }

  "only valid paid user can be created" in {
    val nameStr = "Name"
    val surnameStr = "Surname"
    val usernameStr = "user.name_1"
    val paidDaysRemainingInt = 30
    val paidUser = for {
      name <- refineV[NonEmpty](nameStr)
      surname <- refineV[NonEmpty](surnameStr)
      username <- refineV[MatchesRegex[W.`"[a-zA-Z0-9-._]+"`.T]](usernameStr)
      paidDaysRemaining <- refineV[NonNegative](paidDaysRemainingInt)
    } yield createPaidUser(
      name = name,
      surname = surname,
      username = username,
      paidDaysRemaining = paidDaysRemaining
    )

    paidUser.isRight shouldBe true

    paidUser.map { user =>
      user.name.value shouldBe nameStr
      user.surname.value shouldBe surnameStr
      user.username.value shouldBe usernameStr
      user.paidDaysRemaining.value shouldBe paidDaysRemainingInt
    }
  }

  "paid user with invalid name cannot be created" in {
    val nameStr = ""
    val surnameStr = "Surname"
    val usernameStr = "user.name_1"
    val paidDaysRemainingInt = 30
    val paidUser = for {
      name <- refineV[NonEmpty](nameStr)
      surname <- refineV[NonEmpty](surnameStr)
      username <- refineV[MatchesRegex[W.`"[a-zA-Z0-9-._]+"`.T]](usernameStr)
      paidDaysRemaining <- refineV[NonNegative](paidDaysRemainingInt)
    } yield createPaidUser(
      name = name,
      surname = surname,
      username = username,
      paidDaysRemaining = paidDaysRemaining
    )

    paidUser.isRight shouldBe false
  }

  "paid user with invalid surname cannot be created" in {
    val nameStr = "Name"
    val surnameStr = ""
    val usernameStr = "user.name_1"
    val paidDaysRemainingInt = 30
    val paidUser = for {
      name <- refineV[NonEmpty](nameStr)
      surname <- refineV[NonEmpty](surnameStr)
      username <- refineV[MatchesRegex[W.`"[a-zA-Z0-9-._]+"`.T]](usernameStr)
      paidDaysRemaining <- refineV[NonNegative](paidDaysRemainingInt)
    } yield createPaidUser(
      name = name,
      surname = surname,
      username = username,
      paidDaysRemaining = paidDaysRemaining
    )

    paidUser.isRight shouldBe false
  }

  "paid user with invalid username cannot be created" in {
    val nameStr = "Name"
    val surnameStr = "Surname"
    val usernameStr = ""
    val paidDaysRemainingInt = 30
    val paidUser = for {
      name <- refineV[NonEmpty](nameStr)
      surname <- refineV[NonEmpty](surnameStr)
      username <- refineV[MatchesRegex[W.`"[a-zA-Z0-9-._]+"`.T]](usernameStr)
      paidDaysRemaining <- refineV[NonNegative](paidDaysRemainingInt)
    } yield createPaidUser(
      name = name,
      surname = surname,
      username = username,
      paidDaysRemaining = paidDaysRemaining
    )

    paidUser.isRight shouldBe false
  }

  "paid user with invalid remaining paid days count cannot be created" in {
    val nameStr = "Name"
    val surnameStr = "Surname"
    val usernameStr = "user.name_1"
    val paidDaysRemainingInt = -1
    val paidUser = for {
      name <- refineV[NonEmpty](nameStr)
      surname <- refineV[NonEmpty](surnameStr)
      username <- refineV[MatchesRegex[W.`"[a-zA-Z0-9-._]+"`.T]](usernameStr)
      paidDaysRemaining <- refineV[NonNegative](paidDaysRemainingInt)
    } yield createPaidUser(
      name = name,
      surname = surname,
      username = username,
      paidDaysRemaining = paidDaysRemaining
    )

    paidUser.isRight shouldBe false
  }

  "leveling up free user should work correctly" in {
    val nameStr = "Name"
    val surnameStr = "Surname"
    val usernameStr = "user.name_1"
    val dailyPostLimitInt = 3
    val postsCounterInt = 2
    val freeUser = for {
      name <- refineV[NonEmpty](nameStr)
      surname <- refineV[NonEmpty](surnameStr)
      username <- refineV[MatchesRegex[W.`"[a-zA-Z0-9-._]+"`.T]](usernameStr)
      dailyPostLimit <- refineV[NonNegative](dailyPostLimitInt)
      experience <- refineV[NonNegative](1001)
      currentPostsCount <- refineV[NonNegative](postsCounterInt)
    } yield createFreeUser(
      name = name,
      surname = surname,
      username = username,
      dailyPostLimit = dailyPostLimit
    ).copy(
      postsCounter = currentPostsCount,
      experience = experience,
    )

    freeUser
      .flatMap(_.levelUp)
      .map {
        case freeUsr: FreeUser =>
          freeUsr.postsCounter.value shouldBe postsCounterInt
          freeUsr.level.value shouldBe 1
          freeUsr.experience.value shouldBe 1
          freeUsr.dailyPostLimit.value shouldBe dailyPostLimitInt
        case _ =>
          fail
      }
  }

  "leveling up paid user should work correctly" in {
    val nameStr = "Name"
    val surnameStr = "Surname"
    val usernameStr = "user.name_1"
    val paidDaysRemainingInt = 30
    val paidUserWithPaidDaysLeft = for {
      name <- refineV[NonEmpty](nameStr)
      surname <- refineV[NonEmpty](surnameStr)
      username <- refineV[MatchesRegex[W.`"[a-zA-Z0-9-._]+"`.T]](usernameStr)
      paidDaysRemaining <- refineV[NonNegative](paidDaysRemainingInt)
      experience <- refineV[NonNegative](1001)
    } yield createPaidUser(
      name = name,
      surname = surname,
      username = username,
      paidDaysRemaining = paidDaysRemaining
    ).copy(
      experience = experience
    )

    paidUserWithPaidDaysLeft
      .flatMap(_.levelUp)
      .map {
        case paidUsr: PaidUser =>
          paidUsr.level.value shouldBe 1
          paidUsr.experience.value shouldBe 1
          paidUsr.paidDaysRemaining.value shouldBe paidDaysRemainingInt - 1
        case _ =>
          fail
      }

    val paidUserWithoutPaidDaysLeft = for {
      name <- refineV[NonEmpty](nameStr)
      surname <- refineV[NonEmpty](surnameStr)
      username <- refineV[MatchesRegex[W.`"[a-zA-Z0-9-._]+"`.T]](usernameStr)
      paidDaysRemaining <- refineV[NonNegative](0)
      experience <- refineV[NonNegative](1001)
    } yield createPaidUser(
      name = name,
      surname = surname,
      username = username,
      paidDaysRemaining = paidDaysRemaining
    ).copy(
      experience = experience
    )

    paidUserWithoutPaidDaysLeft
      .flatMap(_.levelUp)
      .map {
        case paidUsr: PaidUser =>
          paidUsr.level.value shouldBe 1
          paidUsr.experience.value shouldBe 1
          paidUsr.paidDaysRemaining.value shouldBe 0
        case _ =>
          fail
      }
  }
}
