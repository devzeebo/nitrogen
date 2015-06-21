package com.vitruvius.nitrogen.message

/**
 * User: Eric
 * Date: 5/13/2015
 */
class Message {

	static void send(String messageType, def message = null, Closure callback = null) {
		send(messageType, message, MessageConfiguration.defaultConfig, callback)
	}

	static void send(String messageType, def message, MessageConfiguration config, Closure callback = null) {

		config.filterMap.values().findAll { it.actions.containsKey(messageType) }.each { filter ->
			Thread.startDaemon {
				def result = filter.actions[messageType].invoke(filter, message)
				callback?.call(result)
			}
		}
	}
}