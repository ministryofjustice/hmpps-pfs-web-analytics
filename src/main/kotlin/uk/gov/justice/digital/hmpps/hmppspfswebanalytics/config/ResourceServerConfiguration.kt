package uk.gov.justice.digital.hmpps.hmppspfswebanalytics.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.gov.justice.hmpps.kotlin.auth.dsl.ResourceServerConfigurationCustomizer

@Configuration
class ResourceServerConfiguration {
  @Bean
  fun resourceServerConfigurationCustomizer(): ResourceServerConfigurationCustomizer = ResourceServerConfigurationCustomizer {
    unauthorizedRequestPaths {
      addPaths = setOf(
        "/queue-admin/retry-all-dlqs",
      )
    }
  }
}
