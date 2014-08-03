#!/bin/bash
java -cp ../WebContent/WEB-INF/lib/jooq-3.3.1.jar:../WebContent/WEB-INF/lib/jooq-codegen-3.3.1.jar:../WebContent/WEB-INF/lib/jooq-meta-3.3.1.jar:../WebContent/WEB-INF/lib/mysql-connector-java-5.1.16-bin.jar:. org.jooq.util.GenerationTool /jooq-config.xml
