name := "ScalaExcercise"
version := "0.1.0-SNAPSHOT"

scalaVersion := "2.13.14"

lazy val akkaHttpVersion = "10.5.3"
lazy val akkaVersion     = "2.8.5"
lazy val circeVersion    = "0.14.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"                        % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json"             % akkaHttpVersion,

  "com.typesafe.akka" %% "akka-actor-typed"                 % akkaVersion,
  "com.typesafe.akka" %% "akka-stream"                      % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-typed"           % akkaVersion,

  "com.typesafe.akka" %% "akka-coordination"                % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster"                     % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools"               % akkaVersion,

  "com.typesafe.akka" %% "akka-persistence-cassandra"       % "1.1.1",

  "io.circe"          %% "circe-core"                       % circeVersion,
  "io.circe"          %% "circe-generic"                    % circeVersion,
  "io.circe"          %% "circe-parser"                     % circeVersion,

  "com.amazonaws"       % "aws-java-sdk-kinesis"            % "1.9.5", // For AWS Kinesis
  "com.sksamuel.avro4s" %% "avro4s-core"                    % "4.0.11", // For Avro Serializer
  "ch.qos.logback"      % "logback-classic"                 % "1.2.3", // For logging
  "com.lightbend.akka"  %% "akka-stream-alpakka-kinesis"    % "6.0.2", // For Commiting messages from AWS Kinesis
  "org.jsoup"           % "jsoup"                           % "1.15.3"
)
