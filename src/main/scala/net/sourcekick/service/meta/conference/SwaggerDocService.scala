package net.sourcekick.service.meta.conference

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.swagger.akka.SwaggerHttpService

/**
  * Base configuration of the Swagger Akka Http extension that generates
  * the appropriate Json schema.
  *
  * <p>
  *   The SwaggerDocService will contain a routes property you can concatenate
  *   along with your existing akka-http routes. This will expose an endpoint
  *   at {{{<baseUrl>/<specPath>/<resourcePath>}}} with the specified
  *   `apiVersion`, `swaggerVersion` and resource listing for example
  *   `/api-docs/swagger.json` should produce the desired swagger file.
  * </p>
  *
  * @param system An actor system that will be used.
  * @param mat    An Actor materializer that will be used.
  */
class SwaggerDocService(system: ActorSystem, mat: ActorMaterializer) extends SwaggerHttpService {
  override val apiClasses: Set[Class[_]] = Set(classOf[ConferenceRoutes])
}
