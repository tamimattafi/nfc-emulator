package com.attafitamim.nfc.view.activities.global

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

open class ActivityLifeCycleHandler<T: Activity> : Application.ActivityLifecycleCallbacks {

    open var currentState: ActivityState = ActivityState.UNKNOWN
    protected set(newState) {
        field = newState
        this.notifyStateListeners(newState)
    }

    protected open var activityWeakReference = WeakReference<T>(null)
    open var currentReference
        get() = activityWeakReference.get()
        protected set(newReference) {
            if (newReference == null) activityWeakReference.clear()
            else activityWeakReference = WeakReference(newReference)
        }

    protected open val stateListeners = ArrayList<(newState: ActivityState) -> Unit>()
    protected open val specificStateListeners = HashMap<ActivityState, ArrayList<() -> Unit>>()

    override fun onActivityPaused(activity: Activity) {
        this.tryChangeActivityState(activity, ActivityState.PAUSED)
    }

    override fun onActivityStarted(activity: Activity) {
        this.tryChangeActivityState(activity, ActivityState.STARTED)
    }

    override fun onActivityDestroyed(activity: Activity) {
        this.tryChangeActivityState(activity, ActivityState.DESTROYED)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        this.tryChangeActivityState(activity, ActivityState.SAVE_INSTANCE_STATE)
    }

    override fun onActivityStopped(activity: Activity) {
        this.tryChangeActivityState(activity, ActivityState.STOPPED)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        this.tryChangeActivityState(activity, ActivityState.CREATED)
    }

    override fun onActivityResumed(activity: Activity) {
        this.tryChangeActivityState(activity, ActivityState.RESUMED)
    }

    open fun addStateChangeListener(listener: (newState: ActivityState) -> Unit) {
        this.stateListeners.add(listener)
    }

    open fun removeStateChangeListener(listener: (newState: ActivityState) -> Unit) {
        this.stateListeners.remove(listener)
    }

    open fun addSpecificStateChangeListener(state: ActivityState, listener: () -> Unit) {
        this.initSpecificListenersArray(state)
        specificStateListeners[state]!!.add(listener)
    }

    open fun removeSpecificStateChangeListener(state: ActivityState, listener: () -> Unit) {
        this.initSpecificListenersArray(state)
        specificStateListeners[state]!!.remove(listener)
    }

    open fun releaseListeners() {
        this.stateListeners.clear()
        this.specificStateListeners.clear()
    }

    @Suppress("UNCHECKED_CAST")
    protected open fun tryChangeActivityState(activity: Activity, state: ActivityState) {
        val myActivity = activity as? T ?: return
        this.currentReference = myActivity
        this.currentState = state
    }

    protected open fun notifyStateListeners(newState: ActivityState) {
        this.stateListeners.forEach { listener -> listener.invoke(newState) }

        val specificStateListeners = this.specificStateListeners[newState]
        specificStateListeners?.forEach { listener -> listener.invoke() }
    }

    protected open fun initSpecificListenersArray(state: ActivityState) {
        if (this.specificStateListeners[state] == null) {
            this.specificStateListeners[state] = ArrayList()
        }
    }

    enum class ActivityState {
        PAUSED,
        STARTED,
        DESTROYED,
        SAVE_INSTANCE_STATE,
        STOPPED,
        CREATED,
        RESUMED,
        UNKNOWN
    }
}