package com.attafitamim.nfc.view.cards.details.model

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

class TaskDetailsViewModel(
    private val getTask: GetTask,
    private val taskId: Int
): ViewModel(), ContainerHost<CardDetailsState, Unit> {

    override val container by lazy {
        val initialState = CardDetailsState()
        container<CardDetailsState, Unit>(initialState)
    }

    init {
        this.loadTask()
    }

    private fun loadTask() = intent {
        val task = getTask(taskId)

        reduce {
            state.copy(task = task)
        }
    }
}