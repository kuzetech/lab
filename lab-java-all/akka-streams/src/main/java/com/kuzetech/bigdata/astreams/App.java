package com.kuzetech.bigdata.astreams;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.javadsl.Source;

import java.util.concurrent.CompletionStage;

public class App {
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("QuickStart");
        final Source<Integer, NotUsed> source = Source.range(1, 100);
        CompletionStage<Done> doneCompletionStage = source.runForeach(System.out::println, system);
        doneCompletionStage.thenRun(system::terminate);
    }
}
