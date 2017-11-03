/*
 * Copyright 2017 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.front50.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.spinnaker.front50.exceptions.DuplicateEntityException
import com.netflix.spinnaker.front50.exceptions.InvalidRequestException
import com.netflix.spinnaker.front50.model.intent.Intent
import com.netflix.spinnaker.front50.model.intent.IntentDAO
import spock.lang.Specification
import spock.lang.Subject


class IntentControllerSpec extends Specification {
  def intentDAO = Mock(IntentDAO)

  @Subject
  def controller = new IntentController(
    intentDAO: intentDAO,
    objectMapper:  new ObjectMapper(),
  )

  def "should update intent if exists"() {
    given:
    def intent = new Intent(
      id: "ahoymatey",
      kind: "Parrot",
      schema: "1",
      spec: [
        application: "keel",
        deescription: "hello"
      ],
      status: "ACTIVE"
    )
    def oldIntent = new Intent(
      id: "ahoymatey",
      kind: "Parrot",
      schema: "1",
      spec: [
        application: "keel",
        deescription: "ahoy"
      ],
      status: "ACTIVE"
    )

    when:
    intentDAO.all() >> { [oldIntent] }
    Intent updatedIntent = controller.upsert(intent)

    then:
    noExceptionThrown()
    updatedIntent == intent

  }

  def "should create intent if it doesn't exist"() {
    given:
    def intent = new Intent(
      id: "ahoymatey",
      kind: "Parrot",
      schema: "1",
      spec: [
        application: "keel",
        deescription: "hello"
      ],
      status: "ACTIVE"
    )

    when:
    intentDAO.all() >> { }
    Intent createdIntent = controller.upsert(intent)

    then:
    noExceptionThrown()
    createdIntent == intent
  }
}
