/*
 * Copyright (c) 2018 "Neo4j, Inc." [https://neo4j.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opencypher.gremlin.translation

import org.neo4j.cypher.internal.frontend.v3_3.phases._
import org.neo4j.cypher.internal.frontend.v3_3.{CypherException, InputPosition, SemanticErrorDef, SyntaxException}

class EmptyParserContext(queryText: String, offset: Option[InputPosition]) extends BaseContext {
  override val tracer: CompilationPhaseTracer = CompilationPhaseTracer.NO_TRACING

  override val notificationLogger: InternalNotificationLogger = devNullLogger

  override val exceptionCreator: (String, InputPosition) => CypherException =
    (message: String, position: InputPosition) => {
      val adjustedPosition = position.withOffset(offset)
      new SyntaxException(s"$message ($adjustedPosition)", queryText, adjustedPosition)
    }

  override def monitors: Monitors = ???

  override def errorHandler: Seq[SemanticErrorDef] => Unit =
    errors => errors.foreach(e => throw exceptionCreator(e.msg, e.position))
}

object EmptyParserContext {
  def apply(queryText: String, offset: Option[InputPosition]): EmptyParserContext =
    new EmptyParserContext(queryText, offset)
}
