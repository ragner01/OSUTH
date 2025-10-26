package ng.osun.his.loadtest

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class OpdLoadTest extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8081")
    .header("Authorization", "Bearer ${token}")

  // Authentication
  val auth = scenario("Authenticate")
    .exec(http("Get Token")
      .post("/auth/realms/osun-his-realm/protocol/openid-connect/token")
      .formParam("grant_type", "client_credentials")
      .formParam("client_id", "osun-his-client")
      .formParam("client_secret", "secret")
      .check(jsonPath("$.access_token").saveAs("token")))

  // Peak OPD Day Scenario
  val opdDay = scenario("Peak OPD Day")
    .exec(auth)
    .repeat(100) {
      exec(http("Search Patients")
        .get("/api/patients")
        .queryParam("query", "test")
        .queryParam("page", "0")
        .queryParam("size", "20"))
    }
    .repeat(50) {
      exec(http("Create Appointment")
        .post("/api/appointments")
        .body(StringBody("""{
          "patientId": "123",
          "clinicId": "clinic-1",
          "providerId": "provider-1",
          "appointmentDate": "2024-12-01T10:00:00Z"
        }""")))
    }
    .repeat(30) {
      exec(http("Queue Position")
        .get("/api/queue/position")
        .queryParam("clinicId", "clinic-1")
        .queryParam("patientId", "123"))
    }

  // Load Profile
  setUp(
    opdDay.inject(
      rampUsersPerSec(1) to 50 during 60 seconds,  // Peak ramp-up
      constantUsersPerSec(50) during 300 seconds,  // Sustained load
      rampUsersPerSec(50) to 1 during 60 seconds   // Ramp-down
    )
  ).protocols(httpProtocol)
    .assertions(
      global.responseTime.max.lt(2000),  // p100 < 2s
      global.successfulRequests.percent.gt(99),  // 99% success rate
      details("Search Patients").responseTime.percentile3.lt(500),  // p95 < 500ms
      details("Create Appointment").responseTime.percentile3.lt(800)
    )
}

