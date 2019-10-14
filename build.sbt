name := "api"

version := "1.0"

scalaVersion := "2.12.10"

resolvers ++= Seq(
  "Typesafe repository snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
  "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype repo"                    at "https://oss.sonatype.org/content/groups/scala-tools/",
  "Sonatype releases"                at "https://oss.sonatype.org/content/repositories/releases",
  "Sonatype snapshots"               at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype staging"                 at "http://oss.sonatype.org/content/repositories/staging",
  "Java.net Maven2 Repository"       at "http://download.java.net/maven/2/",
  "Twitter Repository"               at "http://maven.twttr.com",
  Resolver.bintrayRepo("websudos", "oss-releases")
)

libraryDependencies ++= {
  val akkaHttpV = "10.1.10"
  val akkaStreamV = "2.5.25"
  val cassandraV = "3.7.2"
  val nscalaV = "2.22.0"
  val logbackV = "1.2.3"
  val hasherV = "1.2.2"

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
    "com.github.nscala-time" %% "nscala-time" % nscalaV
  )
}

//lazy val api = (project in file("."))
//  .enablePlugins(Mobilizer)

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

mainClass in Compile := Some("com.measurinator.api.Api")

dockerBaseImage := "openjdk:jre"
dockerExposedPorts := Seq(8899)
packageName in Docker := "hkroger/measurinator-api"
