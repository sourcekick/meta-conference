package net.sourcekick.service.meta.conference

import java.time.Instant

case class Conference(uuid: String, name: String, from: Instant, to: Instant)
