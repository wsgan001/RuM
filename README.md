# RuM
RuM - Rule Mining platform

Initial documentation will not be public untill alpha version is ready

Issue tracking will not be used untill alpha version is ready

##Deployment info
Runs on virgo-tomcat-server-3.6.4.RELEASE

In file "java6-server.profile" add “JavaSE-1.7” under the section “org.osgi.framework.executionenvironment”

In file "serviceability.xml" add

```XML
<!-- RuM logging -->
	<logger level="INFO" additivity="false" name="ee.ut.cs.rum.virgoConsole">
		<appender-ref ref="EVENT_LOG_STDOUT" />
		<appender-ref ref="SIFTED_LOG_FILE" />
		<appender-ref ref="LOG_FILE" />
</logger>
```

Create a Postgesql 9.4 database named RuM (username/password 'postgres')

Bundles should be started in the following order:
1.	RuM_Database, RuM_Plugin_Development
2.	RuM_Plugin_Configuration
3.	RuM_Plugins, RuM_Scheduler
4.	RuM_Administration, RuM_Workspace
5.	RuM

