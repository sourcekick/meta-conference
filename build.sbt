// *****************************************************************************
// Projects
// *****************************************************************************

// Calculate the current year for usage in copyright notices and license headers.
lazy val currentYear: Int = java.time.OffsetDateTime.now().getYear

lazy val metaConference =
  project
    .in(file("."))
    .enablePlugins(GitVersioning, GitBranchPrompt)
    .enablePlugins(AshScriptPlugin, JavaAppPackaging)
    .settings(settings)
    .settings(
      name := "meta-conference",
      buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion, BuildInfoKey.action("buildTime") {
        java.time.ZonedDateTime.now()
      }),
      buildInfoPackage := "net.sourcekick.service.meta.conference",
      //scalacOptions += "-Ystatistics", // Print compiler statistics.
      libraryDependencies ++= Seq(
        library.akkaSlf4j,
        library.akkaStream,
        library.akkaHttp,
        library.akkaHttpCirce,
        library.swaggerAkkaHttp,
        library.circeCore,
        library.circeGeneric,
        library.circeJavaTime,
        library.circeJawn,
        library.circeNumbers,
        library.circeParser,
        library.flywayCore,
        library.jwt,
        library.logbackClassic,
        library.postgresJdbcDriver,
        library.scalaTime,
        library.slick,
        library.slickHikaricp,
        library.slickPg,
        library.slickPgCirceJson,
        library.akkaTestkit % Test,
        library.akkaHttpTestkit % Test,
        library.akkaStreamTestkit % Test,
        library.h2Database % Test,
        library.scalaCheck % Test,
        library.scalaTest % Test
      ),
      parallelExecution in Test := true
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val h2db = "1.4.193"
      // Akka
      val akka = "2.5.4"
      val akkaHttp = "10.0.9"
      val akkaHttpCirce = "1.17.0"
      val swaggerAkkaHttp = "0.10.1"
      // JSON
      val circe = "0.8.0"
      val flyway = "4.2.0"
      // JWT
      val jwtCirce = "0.14.0"
      // time
      val scalaTime = "0.4.1"
      // database access
      val postgresJdbcDriver = "9.4.1212"
      val slick = "3.2.1"
      val slickPgVersion = "0.15.2"
      // logging
      val logback = "1.1.7"
      // testing
      val scalaCheck = "1.13.5"
      val scalaTest = "3.0.1"
    }
    val h2Database = "com.h2database" % "h2" % Version.h2db
    // AKka
    val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % Version.akka
    val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % Version.akka
    val akkaStream = "com.typesafe.akka" %% "akka-stream" % Version.akka
    val akkaStreamTestkit = "com.typesafe.akka" %% "akka-stream-testkit" % Version.akka
    val akkaHttp = "com.typesafe.akka" %% "akka-http" % Version.akkaHttp
    val akkaHttpTestkit = "com.typesafe.akka" %% "akka-http-testkit" % Version.akkaHttp
    val akkaHttpCirce = "de.heikoseeberger" %% "akka-http-circe" % Version.akkaHttpCirce
    val swaggerAkkaHttp = "com.github.swagger-akka-http" %% "swagger-akka-http" % Version.swaggerAkkaHttp
    // JSON
    val circeCore = "io.circe" %% "circe-core" % Version.circe
    val circeGeneric = "io.circe" %% "circe-generic" % Version.circe
    val circeJavaTime = "io.circe" %% "circe-java8" % Version.circe
    val circeJawn = "io.circe" %% "circe-jawn" % Version.circe
    val circeNumbers = "io.circe" %% "circe-numbers" % Version.circe
    val circeParser = "io.circe" %% "circe-parser" % Version.circe
    val flywayCore = "org.flywaydb" % "flyway-core" % Version.flyway
    // JWT
    val jwt = "com.pauldijou" %% "jwt-circe" % Version.jwtCirce
    // time
    val scalaTime = "codes.reactive" %% "scala-time" % Version.scalaTime
    // database access
    val slick = "com.typesafe.slick" %% "slick" % Version.slick
    val slickHikaricp = "com.typesafe.slick" %% "slick-hikaricp" % Version.slick
    val slickPg = "com.github.tminglei" %% "slick-pg" % Version.slickPgVersion
    val slickPgCirceJson = "com.github.tminglei" %% "slick-pg_circe-json" % Version.slickPgVersion
    val postgresJdbcDriver = "org.postgresql" % "postgresql" % Version.postgresJdbcDriver
    // logging
    val logbackClassic = "ch.qos.logback" % "logback-classic" % Version.logback
    val scalaCheck = "org.scalacheck" %% "scalacheck" % Version.scalaCheck
    val scalaTest = "org.scalatest" %% "scalatest" % Version.scalaTest
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
  commonSettings ++
    gitSettings ++
    packagingSettings ++
    scalafmtSettings

lazy val commonSettings =
  Seq(
    scalaVersion := "2.12.3",
    organization := "net.sourcekick",
    organizationName := "sourcekick",
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding",
      "UTF-8",
      "-feature",
      "-target:jvm-1.8",
      "-unchecked",
      "-Xfatal-warnings", // This should really be enabled!
      //"-Xfuture",
      "-Xlint",
      "-Ydelambdafy:method",
      "-Ywarn-adapted-args", // This should really be `Yno-adapted-args`!
      "-Ywarn-numeric-widen",
      "-Ywarn-unused-import",
      "-Ywarn-value-discard"
    ),
    javacOptions ++= Seq(
      "-encoding",
      "UTF-8",
      "-source",
      "1.8",
      "-target",
      "1.8"
    ),
    javaOptions ++= Seq(
      "-jvm-debug 20025"
    ),
    incOptions := incOptions.value.withNameHashing(nameHashing = true),
    unmanagedSourceDirectories.in(Compile) := Seq(scalaSource.in(Compile).value),
    unmanagedSourceDirectories.in(Test) := Seq(scalaSource.in(Test).value),
    wartremoverWarnings in (Compile, compile) ++= Warts.unsafe
  )

lazy val gitSettings =
  Seq(
    git.useGitDescribe := true
  )

lazy val packagingSettings =
  Seq(
    mappings in Universal += {
      // we are using the reference.conf as default application.conf
      // the user can override settings here
      val conf = (resourceDirectory in Compile).value / "reference.conf"
      conf -> "conf/application.conf"
    },
    scriptClasspath := Seq("../conf/") ++ scriptClasspath.value,
    daemonUser.in(Docker) := "root",
    maintainer.in(Docker) := "sourcekick",
    version.in(Docker) := version.value,
    dockerBaseImage := "java:8-jre-alpine",
    dockerExposedPorts := Seq(20022),
    dockerExposedVolumes in Docker := Seq("/config"),
    dockerRepository := Option("sourcekick"),
    mappings in Universal += {
      var appjar = (packageBin in Test).value
      appjar -> s"lib/${appjar.getName}"
    }
  )

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
    scalafmtVersion := "1.0.0-RC4"
  )
// Enable scalafmt for the IntegrationTest scope.
//inConfig(IntegrationTest)(com.lucidchart.sbt.scalafmt.ScalafmtCorePlugin.autoImport.scalafmtSettings)
