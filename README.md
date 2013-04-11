ZMonitor
========

A noninvasive monitoring tool which leverages modern logger's API(Log4J, SLF4J) to help monitoring your Java application.

packages: 

	zmonitor-core:
		The core library of zmonitor, contains adaptors of Log4J & SLF4J, can be used in a simple java application or a Java EE web application.

	zmonitor-server & zmonitor-agent:
		A toolkit for clustered system monitoring, with zmonitor-agent and zmonitor-core installed in each server node, developer now is able to manipulate the information at zmonitor-server side.

	zmonitor-zk:
		An adatpation implementation for ZK framework, monitoring ZK through ZK's Interceptors and Listeners.
