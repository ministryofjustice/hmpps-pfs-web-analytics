package uk.gov.justice.digital.hmpps.hmppspfswebanalytics.resource

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.listeners.model.AuditEvent
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.listeners.model.AuditEventType
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.resource.model.AuditDto
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.services.AuditQueueService
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.services.AuditServiceFactory
import java.time.Instant
import java.util.UUID

@RestController
@RequestMapping("/internal/integration-test")
class AuditIntegrationTestController(
  private val auditQueueService: AuditQueueService,
  private val auditServiceFactory: AuditServiceFactory,
) {

  data class IntegrationTestResult(
    val passed: Boolean,
    val message: String,
    val actualResult: List<AuditDto>?,
    val expectedResult: AuditEvent,
  )

  @PostMapping("/audit-event/{auditEventType}")
  @PreAuthorize("hasRole('ROLE_AUDIT_INTEGRATION_TEST')")
  fun createAuditEvent(@PathVariable auditEventType: AuditEventType): AuditEvent {
    val createdAuditEvent = createTestAuditEvent()
    if (auditEventType == AuditEventType.PRISONER) {
      auditQueueService.sendPrisonerAuditEvent(createdAuditEvent)
    }
    Thread.sleep(10000) // Time needed for event to be processed by SQS
    return createdAuditEvent
  }

  private fun createTestAuditEvent(): AuditEvent = AuditEvent(
    what = "INTEGRATION_TEST",
    `when` = Instant.now(),
    operationId = UUID.randomUUID().toString(),
    subjectId = "some subject ID",
    subjectType = "some subject type",
    correlationId = UUID.randomUUID().toString(),
    who = "TEST_" + (1..5).map { ('A'..'Z').random() }.joinToString(""), // Random who to create a unique partition on every run
    service = "some service",
    details = "{\"key\": \"value\"}",
  )
}
