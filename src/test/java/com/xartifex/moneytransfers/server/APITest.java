package com.xartifex.moneytransfers.server;

import com.xartifex.moneytransfers.server.model.AccountInfo;
import com.xartifex.moneytransfers.server.model.SendOrder;
import com.xartifex.moneytransfers.server.model.SendOrderStatus;
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
    public void checkWeCanTransferMoney() {
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
    public void checkWeCanGetAccountInfoWithBalance() {
        AccountInfo accountInfo = get("/balance/20").thenReturn().as(AccountInfo.class);
        assertThat(accountInfo.getId()).isEqualTo(20);
        assertThat(accountInfo.getName()).isEqualTo("20");
        assertThat(accountInfo.getStatusCode()).isEqualTo(200);
        assertThat(accountInfo.getBalance()).isEqualTo("200");
    }

    //todo: write more tests, for instance to test for erroneous inputs
}