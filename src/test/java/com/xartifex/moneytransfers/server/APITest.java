package com.xartifex.moneytransfers.server;

import com.xartifex.moneytransfers.server.model.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.*;
import org.junit.runner.RunWith;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;


@RunWith(VertxUnitRunner.class)
public class APITest {

    private static Vertx vertx;


    @BeforeClass
    public static void setUpGlobal(TestContext context) {
        RestAssured.baseURI = "http://localhost/rest/v1";
        RestAssured.port = Integer.getInteger("http.port", 8080);

        vertx = Vertx.vertx();
        vertx.deployVerticle(Main.class.getName(), context.asyncAssertSuccess());
    }

    @AfterClass
    public static void tearDownGlobal(TestContext context) {

        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void checkThatWeCanTransferMoney() {
        SendOrder sendOrder = given()
                .body(new SendOrder(1L, 2L, "1.05")).contentType(ContentType.JSON).request()
                .post("/send").thenReturn().as(SendOrder.class);
        assertThat(sendOrder.getStatusCode()).isEqualTo(200);
        assertThat(sendOrder.getStatus()).isEqualTo(SendOrderStatus.COMPLETED);
        //check that money actually reached specified account
        get("/balance/2").then()
                .assertThat()
                .statusCode(200)
                .body("balance", equalTo("21.05"));

    }

    @Test
    public void checkThatWeCannotSendNegativeAmount() {
        InvalidSendOrder invalidSendOrder = given()
                .body(new SendOrder(1L, 2L, "-1")).contentType(ContentType.JSON).request()
                .post("/send").thenReturn().as(InvalidSendOrder.class);
        assertThat(invalidSendOrder.getStatusCode()).isEqualTo(400);
        assertThat(invalidSendOrder.getError()).contains("Amount must be positive");
    }

    @Test
    public void checkThatWeCannotSendToOurselves() {
        InvalidSendOrder invalidSendOrder = given()
                .body(new SendOrder(1L, 1L, "1")).contentType(ContentType.JSON).request()
                .post("/send").thenReturn().as(InvalidSendOrder.class);
        assertThat(invalidSendOrder.getStatusCode()).isEqualTo(400);
        assertThat(invalidSendOrder.getError()).contains("Cannot send money to the same account");
    }

    @Test
    public void checkThatWeCannotSendFromNonExistingAccount() {
        InvalidSendOrder invalidSendOrder = given()
                .body(new SendOrder(999999L, 1L, "1")).contentType(ContentType.JSON).request()
                .post("/send").thenReturn().as(InvalidSendOrder.class);
        assertThat(invalidSendOrder.getStatusCode()).isEqualTo(400);
        assertThat(invalidSendOrder.getError()).contains("The following sender is not found");
    }

    @Test
    public void checkThatWeCannotSendToNonExistingAccount() {
        InvalidSendOrder invalidSendOrder = given()
                .body(new SendOrder(1L, 999999L, "1")).contentType(ContentType.JSON).request()
                .post("/send").thenReturn().as(InvalidSendOrder.class);
        assertThat(invalidSendOrder.getStatusCode()).isEqualTo(400);
        assertThat(invalidSendOrder.getError()).contains("The following receiver is not found");
    }

    @Test
    public void checkThatWeCannotSendMoreThanWeHave() {
        InvalidSendOrder invalidSendOrder = given()
                .body(new SendOrder(1L, 2L, "999999999999")).contentType(ContentType.JSON).request()
                .post("/send").thenReturn().as(InvalidSendOrder.class);
        assertThat(invalidSendOrder.getStatusCode()).isEqualTo(400);
        assertThat(invalidSendOrder.getError()).contains("Sender has insufficient funds to send the following amount");
    }
    
    @Test
    public void checkThatWeGet400WithIllegalRequest() {
        given().body("{\"someIllegal\":\"request\"}").contentType(ContentType.JSON).post("/send").then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    public void checkThatWeCanGetAccountInfoWithBalance() {
        AccountInfo accountInfo = get("/balance/20").thenReturn().as(AccountInfo.class);
        assertThat(accountInfo.getId()).isEqualTo(20);
        assertThat(accountInfo.getName()).isEqualTo("20");
        assertThat(accountInfo.getStatusCode()).isEqualTo(200);
        assertThat(accountInfo.getBalance()).isEqualTo("200");
    }

    @Test
    public void checkThatWeGet404ForNonExisitngAccount() {
        ServerError serverError = get("/balance/99999").thenReturn().as(ServerError.class);
        assertThat(serverError.getStatusCode()).isEqualTo(404);
        assertThat(serverError.getError()).contains("Account not found");
    }

    @Test
    public void checkThatWeGet404ForNonExistingResource() {
        get("/someNonExistingResource").then()
                .assertThat()
                .statusCode(404);
    }
}