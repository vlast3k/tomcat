# custom-tomcat-utils

SimpleRequestDumperFilter
------------------------
${CATALINA_HOME}/conf/Server.xml:

<filter>
	<filter-name>requestdumper</filter-name>
	<filter-class>es.sisifo.tomcatutil.filters.SimpleRequestDumperFilter</filter-class>
</filter>
<filter-mapping>
	<filter-name>requestdumper</filter-name>
	<url-pattern>*</url-pattern>
</filter-mapping>


${CATALINA_HOME}/conf/logging.propertie:

handlers = (...), 5request-dumper.org.apache.juli.FileHandler
(....)
5request-dumper.org.apache.juli.FileHandler.level = INFO
5request-dumper.org.apache.juli.FileHandler.directory = ${catalina.base}/logs
5request-dumper.org.apache.juli.FileHandler.prefix = request-dumper.
5request-dumper.org.apache.juli.FileHandler.formatter = org.apache.juli.VerbatimFormatter
es.sisifo.tomcatutil.filters.SimpleRequestDumperFilter.level = INFO
es.sisifo.tomcatutil.filters.SimpleRequestDumperFilter.handlers = \
5request-dumper.org.apache.juli.FileHandler