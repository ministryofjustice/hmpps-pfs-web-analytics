package uk.gov.justice.digital.hmpps.hmppspfswebanalytics

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.test.web.reactive.server.WebTestClient
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.integration.endtoend.CommandLineProfilesResolver
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.services.AuditQueueService
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.services.PrisonerAuditService
import uk.gov.justice.hmpps.sqs.HmppsQueueFactory
import uk.gov.justice.hmpps.sqs.HmppsQueueService
import uk.gov.justice.hmpps.sqs.HmppsSqsProperties
import uk.gov.justice.hmpps.sqs.MissingQueueException
import uk.gov.justice.hmpps.test.kotlin.auth.JwtAuthorisationHelper

@AutoConfigureWebTestClient
@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTest.SqsConfig::class, JwtAuthorisationHelper::class)
@ActiveProfiles(resolver = CommandLineProfilesResolver::class)
abstract class IntegrationTest {
  @Autowired
  lateinit var webTestClient: WebTestClient

  @MockitoSpyBean
  protected lateinit var prisonerAuditService: PrisonerAuditService

  @MockitoSpyBean
  protected lateinit var auditQueueService: AuditQueueService

  @Autowired
  protected lateinit var jwtAuthHelper: JwtAuthorisationHelper

  internal fun setAuthorisation(
    user: String = "hmpps-audit-client",
    roles: List<String> = listOf(),
    scopes: List<String> = listOf(),
  ): (HttpHeaders) -> Unit = jwtAuthHelper.setAuthorisationHeader(username = user, roles = roles, scope = scopes)

  @Autowired
  private lateinit var hmppsSqsProperties: HmppsSqsProperties

  @Autowired
  protected lateinit var hmppsQueueService: HmppsQueueService

  protected val prisonerAuditQueueConfig by lazy {
    hmppsQueueService.findByQueueId("prisonerauditqueue") ?: throw MissingQueueException("HmppsQueue prisonerauditqueue not found")
  }

  protected val awsSqsPrisonerAuditDlqClient by lazy { prisonerAuditQueueConfig.sqsDlqClient as SqsAsyncClient }
  protected val awsSqsPrisonerAuditUrl by lazy { prisonerAuditQueueConfig.queueUrl }
  protected val awsSqsPrisonerAuditDlqUrl by lazy { prisonerAuditQueueConfig.dlqUrl as String }

  @MockitoSpyBean
  @Qualifier("prisonerauditqueue-sqs-client")
  protected lateinit var awsSqsClient: SqsAsyncClient

  @TestConfiguration
  class SqsConfig(private val hmppsQueueFactory: HmppsQueueFactory) {
    @Bean("prisonerauditqueue-sqs-client")
    fun outboundPrisonerAuditQueueSqsClient(
      hmppsSqsProperties: HmppsSqsProperties,
      @Qualifier("prisonerauditqueue-sqs-dlq-client") outboundPrisonerAuditQueueSqsDlqClient: SqsAsyncClient,
    ): SqsAsyncClient = with(hmppsSqsProperties) {
      val config = queues["prisonerauditqueue"]
        ?: throw MissingQueueException("HmppsSqsProperties config for prisonerauditqueue not found")
      hmppsQueueFactory.createSqsAsyncClient(config, hmppsSqsProperties, outboundPrisonerAuditQueueSqsDlqClient)
    }
  }
}
