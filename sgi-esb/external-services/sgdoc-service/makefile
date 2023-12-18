MIGRATION_LABEL = "to-be-changed"
DATE_WITH_TIME := $(shell /bin/date "+%Y%m%d%H%M%S")

makeFullChangeset:
	rm -f src/main/resources/db/changelog/changes/0000000000000-initial-database.xml
	./mvnw -X liquibase:diff -Dliquibase.propertyFile=src/main/resources/liquibase-initial.properties -Dliquibase.diffChangeLogFile=src/main/resources/db/changelog/changes/0000000000000-initial-database.xml
	sed -r 's/(\sid=")([0-9]+)(-[0-9]+">)/\10000000000000\3/g' -i src/main/resources/db/changelog/changes/0000000000000-initial-database.xml

makeMigration:
	./mvnw -X liquibase:diff -Dliquibase.propertyFile=src/main/resources/liquibase.properties -Dliquibase.diffChangeLogFile=src/main/resources/db/changelog/changes/${DATE_WITH_TIME}-${MIGRATION_LABEL}.xml
	@echo "  - include:" >> src/main/resources/db/changelog/db.changelog-master.yaml
	@echo "      file: classpath*:db/changelog/changes/$(DATE_WITH_TIME)-$(MIGRATION_LABEL).xml" >> src/main/resources/db/changelog/db.changelog-master.yaml
