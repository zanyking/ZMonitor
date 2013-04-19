ZMonitor
========

A noninvasive monitoring tool which leverages modern logger's API(Log4J, SLF4J) to help monitoring your Java application.

packages: 

	zmonitor-core:
		Each classLoader contains only one zmonitor instance.
		The core library of zmonitor, contains appender implementation for Log4J & Logback. 
		zmontior configuration infra.(zmonitor.xml)
		zmontior component modulization infra.
		Default Measure Seqence Handler.



	zmonitor-web:
		suport of Servlet Spec
			HttpRequestMonitorSequenceLifecycleManager.
			context variable: 
				config-url

				Simple UI:
					+. request URL
					+. Ajax Handler URL
				

	
	zmonitor-master-toolkit: [plan to do]
		A toolkit for ZMonitor Master Service development. Designed for IDE developer or clustered application developer to build their own sevice to handle the request from zmonitor agent.
		Topics of this toolkit might include:

		Protocol Design:
			Channel:
				(Http|Https) Post
				Socket
			Request format:
				channel Layer:
					Brust transmittion
				task Layer:
					task: ms report
						Data Format:
						Java Object Serialization with Base64 encoding
						JSON formatting through template
					task: response of cmd
						cmd[config-update]
						cmd[query]

		Security:
			Channel Encryption(encryption with public key)
			Agent Identity
			Agent Meta (IP, port, sysInfo)
			
		Command:
			Security management(encryption with private key)
			Runtime ZMonitor Configuration(no JMX)
			Query

		
		
		Statistic & Storage & Query:



	zmonitor-agent:[plan to do]
		with zmonitor-agent(requires zmonitor-core) installed in a server node, developer can monitor a nodes runtime execution thrugh Logging system and be able to manipulate the log information at zmonitor-server side.

	zmonitor-zk:
		[not done yet]
		An adaptation for ZK framework, monitoring ZK through ZK's Interceptors and Listeners.
