package uk.gov.justice.digital.hmpps.hmppspfswebanalytics.jpa

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.hmpps.hmppspfswebanalytics.jpa.model.AuditUser

@Repository
interface AuditUserRepository : CrudRepository<AuditUser, Long>
