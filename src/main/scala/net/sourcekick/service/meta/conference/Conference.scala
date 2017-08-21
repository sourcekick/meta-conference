/**
  * Copyright (C) Recogizer Group GmbH - All Rights Reserved
  * Unauthorized copying of this file, via any medium is strictly prohibited
  * Proprietary and confidential
  * Created on 21.08.17.
  */
package net.sourcekick.service.meta.conference

import java.time.Instant

case class Conference(uuid: String, name: String, from: Instant, to: Instant)
