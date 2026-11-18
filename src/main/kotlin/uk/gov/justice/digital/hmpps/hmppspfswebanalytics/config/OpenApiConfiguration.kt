package uk.gov.justice.digital.hmpps.hmppspfswebanalytics.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfiguration(buildProperties: BuildProperties) {
  private val version: String = buildProperties.version!!

  @Bean
  fun customOpenAPI(): OpenAPI = OpenAPI()
    .servers(
      listOf(
        Server().url("https://hmpps-pfs-web-analytics.hmpps.service.justice.gov.uk").description("Prod"),
        Server().url("https://hmpps-pfs-web-analytics-preprod.hmpps.service.justice.gov.uk").description("PreProd"),
        Server().url("https://hmpps-pfs-web-analytics-staging.hmpps.service.justice.gov.uk").description("Staging"),
        Server().url("https://hmpps-pfs-web-analytics-dev.hmpps.service.justice.gov.uk").description("Development"),
        Server().url("http://localhost:8080").description("Local"),
      ),
    )
    .info(
      Info().title("PFS Web Analytics")
        .version(version)
        .description("Web analytics API")
        .contact(Contact().name("HMPPS PFS Connect").email("thehub@digital.justice.gov.uk")),
    )
}
