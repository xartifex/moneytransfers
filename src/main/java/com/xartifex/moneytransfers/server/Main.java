package com.xartifex.moneytransfers.server;

/**
 * author: xartifex
 * since: 02.06.2017
 */

import com.xartifex.moneytransfers.server.config.ResteasySuppliedServer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.util.function.Consumer;

public class Main extends AbstractVerticle {
    //todo: implement order in progress
    public static void main(String[] args) {
        Consumer<Vertx> runner = vertx -> {
            vertx.deployVerticle(Main.class.getName());
        };
        Vertx vertx = Vertx.vertx();
        runner.accept(vertx);
    }

    @Override
    public void start(Future<Void> future) throws Exception {
        DeploymentOptions deploymentOptions = new DeploymentOptions()
                .setWorker(true).setMultiThreaded(true);
        vertx.deployVerticle(DBLayerProcessing.class.getName(), deploymentOptions, res -> {
            if (res.succeeded()) {
                future.complete();
            } else {
                future.failed();
            }
        });
        vertx.deployVerticle(ResteasySuppliedServer.class.getName());
    }
}