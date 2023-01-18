package io.github.svejnohatomas.swansea.newsaggregator.interfaces

/**
 * An interface for refresh functionality.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 * */
interface IRefresh {
    /**
     * A list of observers.
     * */
    val observers: MutableList<IRefreshObserver>

    /**
     * Adds an observer to a list of observers.
     *
     * @param observer An observer to add.
     * */
    fun attach(observer: IRefreshObserver) {
        observers.add(observer)
    }

    /**
     * Removes an observer from a list of observers.
     *
     * @param observer An observer to remove.
     * */
    fun detach(observer: IRefreshObserver) {
        observers.remove(observer)
    }

    /**
     * Notify all observers.
     *
     * @param userTriggered Indicates whether the refresh was triggered by the user or the application.
     * */
    fun notifyObservers(userTriggered: Boolean) {
        observers.forEach { it.refresh(userTriggered) }
    }
}