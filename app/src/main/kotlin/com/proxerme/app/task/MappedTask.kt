package com.proxerme.app.task

import com.proxerme.app.task.framework.Task

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
class MappedTask<I, O>(private val task: Task<I>, private val mapFunction: (I) -> O) : Task<O> {

    override val isWorking: Boolean
        get() = task.isWorking

    override fun execute(successCallback: (O) -> Unit, exceptionCallback: (Exception) -> Unit) {
        task.execute({
            successCallback.invoke(mapFunction.invoke(it))
        }, {
            exceptionCallback.invoke(it)
        })
    }

    override fun cancel() {
        task.cancel()
    }

    override fun reset() {
        task.reset()
    }

    override fun destroy() {
        task.destroy()
    }
}