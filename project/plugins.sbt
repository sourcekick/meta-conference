addSbtPlugin("com.eed3si9n"       % "sbt-buildinfo"       % "0.7.0")
addSbtPlugin("com.typesafe.sbt"   % "sbt-git"             % "0.9.3")
addSbtPlugin("de.heikoseeberger"  % "sbt-header"          % "4.0.0")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"             % "0.2.27")
addSbtPlugin("com.typesafe.sbt"   % "sbt-native-packager" % "1.3.2")
addSbtPlugin("com.jsuereth"       % "sbt-pgp"             % "1.1.0")
addSbtPlugin("com.lucidchart"     % "sbt-scalafmt"        % "1.14")
addSbtPlugin("org.wartremover"    % "sbt-wartremover"     % "2.2.1")

libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.25" // Needed by sbt-git
