package com.vitruvius.nitrogen.message

/**
 * User: Eric
 * Date: 5/13/2015
 */
class MessageConfiguration {

	static def defaultConfig

	def filterMap = [:] as Map<String, Object>

	private MessageConfiguration(){}

	static def loadConfiguration(String filename) {
		loadConfiguration(new File(filename).newReader())
	}

	static def loadConfiguration(Reader configReader) {
		MessageConfiguration config = new MessageConfiguration()

		GroovyShell shell = new GroovyShell()
		def script = shell.parse(configReader)

		// delegate all missing things to the config
		script.metaClass.methodMissing = { String methodName, args ->
			config."$methodName"(*args)
		}
		script.metaClass.propertyMissing = { String propertyName ->
			config."$propertyName"
		}
		script.metaClass.propertyMissing << { String propertyName, newValue ->
			config."$propertyName" = newValue
		}

		// execute the config script
		script.run()

		return config
	}

	def registerFilter(Class filter) {
		def instance = filter.newInstance()
		instance.metaClass.actions = filter.methods.findAll { it.name.startsWith('on') }.collectEntries {
			[it.name, it]
		}
		filterMap[filter.simpleName] = instance
	}

	public static void main(String[] args) {
		println loadConfiguration('testConfig.groovy').filterMap.values()*.actions
	}
}