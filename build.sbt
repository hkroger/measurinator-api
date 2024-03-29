name := "api"

version := "1.0"

scalaVersion := "2.13.7"

resolvers ++= Seq(
  "Typesafe repository snapshots" at "https://repo.typesafe.com/typesafe/snapshots/",
  "Typesafe repository releases" at "https://repo.typesafe.com/typesafe/releases/",
  "Sonatype repo" at "https://oss.sonatype.org/content/groups/scala-tools/",
  "Sonatype releases" at "https://oss.sonatype.org/content/repositories/releases",
  "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype staging" at "https://oss.sonatype.org/content/repositories/staging",
  "Java.net Maven2 Repository" at "https://download.java.net/maven/2/",
  "Twitter Repository" at "https://maven.twttr.com",
  Resolver.bintrayRepo("websudos", "oss-releases")
)

libraryDependencies ++= {
  val akkaHttpV = "10.2.7"
  val akkaStreamV = "2.6.18"
  val cassandraV = "3.11.0"
  val nscalaV = "2.30.0"
  val logbackV = "1.2.10"
  val hasherV = "1.2.2"
  val scalaTestV = "3.2.2"
  val scalaLoggingV = "3.9.4"

  Seq(
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.typesafe.akka" %% "akka-stream" % akkaStreamV,
    "ch.qos.logback" % "logback-classic" % logbackV,
    "ch.qos.logback" % "logback-core" % logbackV,
    "com.outr" %% "hasher" % hasherV,
    "com.datastax.cassandra" % "cassandra-driver-core" % cassandraV,
    "com.datastax.cassandra" % "cassandra-driver-mapping" % cassandraV,
    "com.github.nscala-time" %% "nscala-time" % nscalaV,
    "org.scalactic" %% "scalactic" % scalaTestV,
    "org.scalatest" %% "scalatest" % scalaTestV % "test",
  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingV
  )
}

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

mainClass in Compile := Some("com.measurinator.api.Api")

dockerBaseImage := "openjdk:11-slim"
dockerExposedPorts := Seq(8899)
packageName in Docker := "hkroger/measurinator-api"
