package uk.gov.justice.digital.hmpps.hmppspfswebanalytics.services

import com.microsoft.applicationinsights.TelemetryClient
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.config.trackEvent
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.jpa.PrisonerAuditRepository
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.jpa.model.toAuditEvent
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.listeners.model.AuditEvent
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.listeners.model.AuditEventType
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.listeners.model.asMap
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.listeners.model.toPrisonerAuditEvent
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.model.AuditFilterDto
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.resource.model.AuditDto
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.services.AuditQueueService.Companion.log
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.services.SafeLogSanitizer.sanitize

@Service
class PrisonerAuditService(
  private val telemetryClient: TelemetryClient,
  private val prisonerAuditRepository: PrisonerAuditRepository,
) : AuditServiceInterface {
  override fun saveAuditEvent(
    auditEvent: AuditEvent,
  ) {
    prisonerAuditRepository.save(auditEvent.toPrisonerAuditEvent())

    telemetryClient.trackEvent(AuditEventType.PRISONER.description, auditEvent.asMap())
  }

  override fun findAll(): List<AuditDto> = prisonerAuditRepository.findAll(Sort.by(DESC, "when")).map { AuditDto(it.toAuditEvent()) }

  override fun findPage(
    pageable: Pageable,
    auditFilterDto: AuditFilterDto,
  ): Page<AuditDto> {
    with(auditFilterDto) {
      log.info(
        "Searching audit events by startDate {} endDate {} service {} subjectId {} subjectType {} correlationId {} what {} who {}",
        sanitize(startDateTime),
        sanitize(endDateTime),
        sanitize(service),
        sanitize(subjectId),
        sanitize(subjectType),
        sanitize(correlationId),
        sanitize(what),
        sanitize(who),
      )
      return prisonerAuditRepository.findPage(
        pageable,
        startDateTime,
        endDateTime,
        service,
        subjectId,
        subjectType,
        correlationId,
        what,
        who,
      )
        .map { AuditDto(it.toAuditEvent()) }
    }
  }
}
