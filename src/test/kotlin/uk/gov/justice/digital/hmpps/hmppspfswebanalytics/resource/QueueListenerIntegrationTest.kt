package uk.gov.justice.digital.hmpps.hmppspfswebanalytics.resource

import com.microsoft.applicationinsights.TelemetryClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.bean.override.mockito.MockitoBean
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.IntegrationTest
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.jpa.PrisonerAuditRepository
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.listeners.HMPPSAuditListener
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.services.AuditS3Client
import uk.gov.justice.hmpps.sqs.HmppsQueue
import uk.gov.justice.hmpps.sqs.countAllMessagesOnQueue

abstract class QueueListenerIntegrationTest : IntegrationTest() {

  @MockitoBean
  lateinit var telemetryClient: TelemetryClient

  @MockitoBean
  lateinit var prisonerAuditRepository: PrisonerAuditRepository

  @MockitoBean
  lateinit var auditS3Client: AuditS3Client

  @Autowired
  protected lateinit var listener: HMPPSAuditListener

  internal val prisonerAuditQueue by lazy { hmppsQueueService.findByQueueId("prisonerauditqueue") as HmppsQueue }
  internal val prisonerAuditQueueSqsClient by lazy { prisonerAuditQueue.sqsClient }
  internal val prisonerAuditQueueUrl by lazy { prisonerAuditQueue.queueUrl }
  internal val prisonerAuditSqsDlqClient by lazy { prisonerAuditQueue.sqsDlqClient as SqsAsyncClient }
  internal val prisonerAuditDlqUrl by lazy { prisonerAuditQueue.dlqUrl as String }

  fun getNumberOfMessagesCurrentlyOnPrisonerAuditQueue(): Int = prisonerAuditQueueSqsClient.countAllMessagesOnQueue(prisonerAuditQueueUrl).get()
  fun getNumberOfMessagesCurrentlyOnPrisonerAuditDlq(): Int = prisonerAuditSqsDlqClient.countAllMessagesOnQueue(prisonerAuditDlqUrl).get()
}
