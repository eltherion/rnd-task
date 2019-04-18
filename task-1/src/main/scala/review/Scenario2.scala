package review

import donotmodifyme.Scenario2._
import database._

/*
 * We need to store `donotmodifyme.Scenario2.Datum` in an efficient manner. We found a very efficient database
 * implementation (donotmodifyme.Scenario2.database.*` now we need to provide a well behaved wrapper for a
 * java the libary `database`.
 */
object Scenario2 {
  class DatabaseUser(credentials: DatabaseCredentials) {
    def obtain: DatabaseConnection = {
      DatabaseConnection.open(credentials)
    }

    /*The return type should be explicitly defined, as it it public function.*/
    def put(connection: DatabaseConnection, datum: Datum) = {
      connection.put(datum.key, datum.serializeContent)
    }

    def getAll(connection: DatabaseConnection): Seq[Datum] = {
      val keys = connection.keys.toList

      val builder = Seq.newBuilder[Datum]
      keys.foreach { key =>
        val bytes = connection.fetch(key)
        Datum.deserialize(bytes) match {
          case Left(_) =>
           /*Currently `error` variable is unused, but it could be for example to log errors.
           In present code form we would have no idea how many times deserialization has failed, it just falls quietly.
           Moreover, output type from this pattern `match`'ing is inferred as `Any` which is not good practice.*/
          case Right(datum) =>
            builder += datum
        }
      }

      builder.result
    }

    def close(connection: DatabaseConnection): Unit = {
      connection.close
    }
  }
  /* I assume that all calls to the database that originate from within the same application are eligible to use the
   * same credentials (taken perhaps from configuration file).
   * Proposed changes:
   *
   * 1. Currently you can try to open many connections to database. I would introduce some kind of resource sharing (in this
   * case for DatabaseConnection).
   * One way to achieve this would be by utilizing one Akka Actor responsible for calling `DatabaseConnection.open`.
   *  It would internally open new connection (only after checking, that it hasn't opened connection yet!), store
   * `DatabaseConnection`, receive messages to open connection, put/fetch data, return all keys or close connection
   * (if there is open one). It would also close connection on end of life cycle.
   * 2. I would change result types of obtain, put, getAll functions exposed from class `DatabaseUser` to be
   * wrapped into Future[_].
   * 3. Name of the class `DatabaseUser` is quite inadequate and confusing. I would change it to `DatumRepository`.
   * 4. I would refactor deserializing of data into `Datum` to at least different function, more preferably - to another
   * class. That way deserializing would be easy to test and class doing deserialization would have single reponsibility.
   * 5. Using Seq.newBuilder[Datum] is not necessary. After asking actor for all keys (which would be of type
   * Future[Iterator[String]] one could call flatMap on it and use Future.traverse(keys)(deserializeF) to sequentially
   * ask actor for all data bounded with each key. I would introduce mentioned `deserializeF` function that returns
   * Future[Datum] and fails if deserialization was unsuccessful. That way we either successfully fetch and deserialize
   * all data or fail for some reason.
   * 6. It it more type safe to wrap Array[Byte] result from `DatabaseConnection.fetch` into an `Option`. `None` should
   * be returned if `DatabaseConnection.fetch` results in null, otherwise Option(resultArray).
   * */
}
