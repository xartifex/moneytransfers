package com.xartifex.moneytransfers.server;

/**
 * author: xartifex
 * since: 02.06.2017
 */

import com.xartifex.moneytransfers.server.model.*;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

@Path(Const.REST_PREFIX)
public class APILayerProcessing {

    private final Logger logger = LoggerFactory.getLogger(APILayerProcessing.class);

    @POST
    @Path("/send")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public void send(final SendOrder sendOrder, @Context Vertx vertx, @Suspended final AsyncResponse asyncResponse) {

        vertx.eventBus().<JsonObject>send("db", JsonObject.mapFrom(sendOrder).put(Const.REQUEST_TYPE_KEY, RequestType.SEND_ORDER)
                , msg -> {
                    if (msg.succeeded()) {
                        JsonObject jsonObject = msg.result().body();

                        if (jsonObject != null) {
                            if (jsonObject.getString(Const.INVALID_SEND_ORDER_KEY) != null) {
                                jsonObject.remove(Const.INVALID_SEND_ORDER_KEY);
                                asyncResponse.resume(Response.status(Response.Status.BAD_REQUEST)
                                        .entity(jsonObject.mapTo(InvalidSendOrder.class))
                                        .type(MediaType.APPLICATION_JSON_TYPE)
                                        .build());
                            } else {
                                asyncResponse.resume(Response.status(Response.Status.OK)
                                        .entity(jsonObject.mapTo(SendOrder.class))
                                        .type(MediaType.APPLICATION_JSON_TYPE)
                                        .build());
                            }
                        } else {
                            asyncResponse.resume(Response.status(Response.Status.NOT_FOUND)
                                    .entity(new ServerError("Resource not found!", Response.Status.NOT_FOUND.getStatusCode()))
                                    .type(MediaType.APPLICATION_JSON_TYPE)
                                    .build());
                        }
                    } else {
                        logger.error("Internal server error occurred during balance send request: ", msg.cause());
                        asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                                entity(new ServerError("Internal server error occurred during balance send request!",
                                        Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                                .type(MediaType.APPLICATION_JSON_TYPE).build());
                    }
                });
    }

    @GET
    @Path("/balance/{accountId}")
    @Produces({MediaType.APPLICATION_JSON})
    public void send(@PathParam("accountId") String accountId, @Context Vertx vertx, @Suspended final AsyncResponse asyncResponse) {
        if (accountId == null) {
            asyncResponse.resume(Response.status(Response.Status.BAD_REQUEST).
                    entity(new ServerError("accountId is null!", Response.Status.BAD_REQUEST.getStatusCode()))
                    .type(MediaType.APPLICATION_JSON_TYPE).build());
            return;
        }

        vertx.eventBus().<JsonObject>send("db", new JsonObject().put(Const.REQUEST_TYPE_KEY, RequestType.ACCOUNT_INFO)
                        .put(Const.ACCOUNT_ID_KEY, accountId)
                , msg -> {
                    if (msg.succeeded()) {
                        JsonObject jsonObject = msg.result().body();

                        if (jsonObject != null) {
                            asyncResponse.resume(Response.status(Response.Status.OK).
                                    entity(jsonObject.mapTo(AccountInfo.class)).type(MediaType.APPLICATION_JSON_TYPE).build());
                        } else {
                            asyncResponse.resume(Response.status(Response.Status.NOT_FOUND)
                                    .entity(new ServerError("Account not found!", Response.Status.NOT_FOUND.getStatusCode()))
                                    .type(MediaType.APPLICATION_JSON_TYPE)
                                    .build());
                        }
                    } else {
                        logger.error("Internal server error occurred during balance request: ", msg.cause());
                        asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                                entity(new ServerError("Internal server error occurred during balance request!",
                                        Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                                .type(MediaType.APPLICATION_JSON_TYPE).build());
                    }
                });
    }
}