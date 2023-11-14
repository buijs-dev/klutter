package dev.buijs.klutter.kore.tasks.project

import dev.buijs.klutter.kore.tasks.Executor

class Exeggutor extends Executor {

    /**
     * Map of expected CLI executions.
     *
     * Key: String absolute path where command is to be executed.
     * Value: List<String> commands to be executed.
     */
    private def expectations = new HashMap<String,List<String>>()

    /**
     * Execute action when an expectation is met.
     */
    private def actionsAfterExpectation = new HashMap<String, Closure>()

    @Override
    String execute(File runFrom, Long timeout, String command, Map<String, String> env) {
        if(expectations.containsKey(runFrom.absolutePath)) {
            if(expectations[runFrom.absolutePath].contains(command)) {
                def actionOrNull = actionsAfterExpectation[command]
                if(actionOrNull != null)
                    actionOrNull.call()
                return ""
            }
        }

        throw new RuntimeException("CLI execution failure: $command - $runFrom.absolutePath")
    }

    def putExpectation(String runFrom, String command) {
        if(!expectations.containsKey(runFrom)) {
            expectations.put(runFrom, [command])
        } else {
            expectations.get(runFrom).add(command)
        }
    }

    def putExpectationWithAction(String runFrom, String command, Closure action) {
        if(!expectations.containsKey(runFrom)) {
            expectations.put(runFrom, [command])
        } else {
            expectations.get(runFrom).add(command)
        }

        actionsAfterExpectation.put(command, action)
    }

}
