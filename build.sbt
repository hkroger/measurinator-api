name := "api"

version := "1.0"

scalaVersion := "2.11.8"

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
  val akkaV = "2.4.4"
  val cassandraV = "3.2.0"

  Seq(
    "com.typesafe.akka" %% "akka-http-core" % akkaV,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaV,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "ch.qos.logback" % "logback-core" % "1.2.3",
    "com.roundeights" %% "hasher" % "1.2.0",
    "com.datastax.cassandra" % "cassandra-driver-core" % cassandraV,
    "com.datastax.cassandra" % "cassandra-driver-mapping" % cassandraV

  )
}

lazy val api = (project in file("."))
  .enablePlugins(Mobilizer)

// Define deployment environments.
import fi.onesto.sbt.mobilizer.DeploymentEnvironment

deployEnvironments := Map(
  'node2 -> DeploymentEnvironment(
    hosts         = Seq("hkroger.info"),
    port          = 2222,
    rsyncOpts     = Seq("-e", "ssh -p 2222"),
    rootDirectory = "/opt/measurinator-api"),
  'node3 -> DeploymentEnvironment(
    hosts         = Seq("hkroger.info"),
    port          = 2223,
    rsyncOpts     = Seq("-e", "ssh -p 2223"),
    rootDirectory = "/opt/measurinator-api"),
  'node4 -> DeploymentEnvironment(
    hosts         = Seq("hkroger.info"),
    port          = 2224,
    rsyncOpts     = Seq("-e", "ssh -p 2224"),
    rootDirectory = "/opt/measurinator-api")


)
