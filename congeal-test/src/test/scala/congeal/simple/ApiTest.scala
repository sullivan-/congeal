package congeal.simple

import congeal.sc._

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/** JUnit tests for basic usage of the `congeal.api` macro. */
@RunWith(classOf[JUnit4])
class ApiTest {

  // TODO: tests need to run in parallel
  // TODO: test with protected methods
  // TODO: test with private methods
  // TODO: test with private[this] methods
  // TODO: test with inheritance

  @Test
  def apiSaysHello() {
    compilingSourceProducesAppWithOutput(
      """|import congeal.api
         |object Test extends App {
         |  case class U(uName: String)
         |  trait URepository {
         |     def getU(uName: String): Option[U] = None // STUB
         |  }
         |  class URepositoryImpl extends URepository with api[URepository]
         |  val uRepository: api[URepository] = new URepositoryImpl
         |  println(uRepository.getU("testUName"))
         |}
      |""".stripMargin,
      "Test",
      "None\n")
  }

}
