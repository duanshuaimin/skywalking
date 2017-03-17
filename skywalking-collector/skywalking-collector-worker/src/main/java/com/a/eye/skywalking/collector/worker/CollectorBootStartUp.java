package com.a.eye.skywalking.collector.worker;

import com.a.eye.skywalking.collector.cluster.ClusterConfig;
import com.a.eye.skywalking.collector.cluster.ClusterConfigInitializer;
import com.a.eye.skywalking.collector.worker.httpserver.HttpServer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * @author pengys5
 */
public class CollectorBootStartUp {

    public static void main(String[] args) throws Exception {
        ClusterConfigInitializer.initialize("collector.config");

        final Config config = ConfigFactory.parseString("akka.remote.netty.tcp.hostname=" + ClusterConfig.Cluster.Current.hostname).
                withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=" + ClusterConfig.Cluster.Current.port)).
                withFallback(ConfigFactory.parseString("akka.cluster.roles=" + ClusterConfig.Cluster.Current.roles)).
                withFallback(ConfigFactory.parseString("akka.actor.provider=" + ClusterConfig.Cluster.provider)).
                withFallback(ConfigFactory.parseString("akka.cluster.seed-nodes=" + ClusterConfig.Cluster.nodes)).
                withFallback(ConfigFactory.load("application.conf"));

//        ActorSystem system = ActorSystem.create(ClusterConfig.Cluster.appname, config);
//        WorkersCreator.INSTANCE.boot(system);
        HttpServer.INSTANCE.boot();
//        EsClient.boot();
    }
}