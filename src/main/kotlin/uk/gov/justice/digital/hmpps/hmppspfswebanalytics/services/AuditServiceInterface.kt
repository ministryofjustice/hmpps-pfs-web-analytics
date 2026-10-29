package uk.gov.justice.digital.hmpps.hmppspfswebanalytics.services

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.listeners.model.AuditEvent
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.listeners.model.AuditEventType
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.model.AuditFilterDto
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.model.AuditQueryRequest
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.model.AuditQueryResponse
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.resource.model.AuditDto

interface AuditServiceInterface {
  fun saveAuditEvent(auditEvent: AuditEvent)

  fun findAll(): List<AuditDto>

  fun findPage(
    pageable: Pageable = Pageable.unpaged(),
    auditFilterDto: AuditFilterDto,
  ): Page<AuditDto>

  fun triggerQuery(
    queryRequest: AuditQueryRequest,
    auditEventType: AuditEventType,
  ): AuditQueryResponse

  fun getQueryResults(queryExecutionId: String): AuditQueryResponse
}
