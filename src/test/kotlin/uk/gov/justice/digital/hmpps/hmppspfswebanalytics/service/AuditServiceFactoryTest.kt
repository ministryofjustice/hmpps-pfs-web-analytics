package uk.gov.justice.digital.hmpps.hmppspfswebanalytics.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.listeners.model.AuditEventType
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.services.AuditServiceFactory
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.services.PrisonerAuditService

class AuditServiceFactoryTest {

  private val prisonerAuditService: PrisonerAuditService = mock()

  private val auditServiceFactory = AuditServiceFactory(
    prisonerAuditService,
  )

  @Test
  fun `should return PrisonerAuditService for PRISONER event type`() {
    val service = auditServiceFactory.getAuditService(AuditEventType.PRISONER)
    assertEquals(prisonerAuditService, service)
  }
}
