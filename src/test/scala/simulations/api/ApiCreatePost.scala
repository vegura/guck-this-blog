package simulations.api

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.language.postfixOps

class ApiCreatePost extends Simulation {
  var httpConfig = http.baseUrl("http://51.116.187.213/blog/api/")
    .header("Accept", "text/plain,application/json")

  val accessCookies = Cookie(".AUXBLOGENGINE-96d5b379-7e1d-4dac-a6ba-1e50db561b04",
    "FEDE016C2D509081F64688DC878F9C07B96FF524255E2578BF5DC985106C90BBE42683B2E76316" +
      "A76E3E70BD6E819CF8E71535775B5BA8AA8A90BFB9FC8EB3661C6C48EC4D7CC62BA6BD913FB26E1E7F8" +
      "8EB22B17132BBE411F41097E021B056673F9C5EC2B7EAF33E1FB045FF9799773A9F29425247CC69A1D3" +
      "B1D8E0665717991D469E66FC4446AD07C734F68290778F4C74DB3FDA86810962B8158AA99C2C3742DA5" +
      "9DB875CA0CDE10B3C634EB7B9BD4E932408518766017313015F6D30F4F4084FC545FAA2D72EBF7787E9" +
      "3D411689DDFA47AB9296CD4F6CFD8B4E680F38665A7989895B7BE01CE618F5619AA1BED6F23C728E131" +
      "B6A0D80FB98B5AC2C948801BD4AA1352DFB43B3D8971796A3939D3153C8F521D49EEEBF515C90629E05" +
      "13B1EB3B6EC9B492C8F9E2BED3F0D8FF8EE569098327B0D65B0034A413A37DA6A6D6BD9E23D788A8C88" +
      "F5A35997A8D871738A3C3B1901758BAC01FED47B023DC7853C268AEFF409651EFAADA0AA9454C42314A58")


  def getBunchOfPosts(): ChainBuilder = {
    exec(
      http("Fetching 10 posts")
        .get("/posts")
        .check(status.is(200)),
    )
  }

  def createNewPost(): ChainBuilder = {
    exec(
      http("Creating some posts")
        .post("/posts")
        .body(StringBody("{\"Id\":\"\",\"Title\":\"Post\",\"Author\":\"Mary Gniedykh\"," +
          "\"Content\":\"<p>post 1</p>\",\"DateCreated\":\"2021-12-17 14:09\",\"Slug\":\"post\",\"Categories\":[]," +
          "\"Tags\":[],\"Comments\":\"\",\"HasCommentsEnabled\":true,\"IsPublished\":true}"))
    )
  }

  val scn = scenario("Testing GET posts")
    .exec(addCookie(accessCookies))
    .exec(getBunchOfPosts())
    .exec(createNewPost())
    .exec(getBunchOfPosts())

  setUp(scn.inject(
    nothingFor(1 seconds),
//        atOnceUsers(10),
    rampUsersPerSec(1).to(5).during(1 minutes)
  ).protocols(httpConfig.inferHtmlResources()))
}