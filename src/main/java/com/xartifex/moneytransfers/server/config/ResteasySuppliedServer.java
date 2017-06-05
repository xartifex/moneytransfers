package com.xartifex.moneytransfers.server.config;

/**
 * author: xartifex
 * since: 02.06.2017
 */


import com.xartifex.moneytransfers.db.MoneyTransfersImpl;
import com.xartifex.moneytransfers.server.APILayerProcessing;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import org.jboss.resteasy.plugins.server.vertx.VertxRequestHandler;
import org.jboss.resteasy.plugins.server.vertx.VertxResteasyDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResteasySuppliedServer extends AbstractVerticle {
    private final Logger logger = LoggerFactory.getLogger(ResteasySuppliedServer.class);

    @Override
    public void start() throws Exception {
        vertx.executeBlocking(future -> {
            VertxResteasyDeployment deployment = new VertxResteasyDeployment();
            deployment.getProviders().add(new GlobalExceptionHandler());
            deployment.start();
            deployment.getRegistry().addPerInstanceResource(APILayerProcessing.class);


            HttpServer server = vertx.createHttpServer();

            Router router = Router.router(vertx);

            router.route("/rest/*").handler(r -> {
                new VertxRequestHandler(vertx, deployment).handle(r.request());
            });
            router.route().handler(StaticHandler.create());

            server.requestHandler(router::accept).listen(8080, result ->
            {
                if (result.succeeded()) {
                    logger.info("Server started on port: " + result.result().actualPort());
                } else {
                    logger.error("Server start failure: ", result.cause());
                }
            });

        }, res -> {
            if (res.failed()) {
                logger.error("Blocking task failure:" + res.cause());
            }
        });
    }
}