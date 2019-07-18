package eu.ha3.x.sff.system.postgres

import liquibase.change.custom.CustomTaskChange
import liquibase.database.Database
import liquibase.exception.ValidationErrors
import liquibase.resource.ResourceAccessor

class LB005AddUuidToExistingDocs : CustomTaskChange {
    override fun validate(p0: Database?): ValidationErrors {
        return ValidationErrors()
    }

    override fun setUp() {
    }

    override fun setFileOpener(p0: ResourceAccessor?) {
    }

    override fun getConfirmationMessage(): String {
        return ""
    }

    override fun execute(database: Database) {
        database.connection.
    }

}