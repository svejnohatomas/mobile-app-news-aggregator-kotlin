package io.github.svejnohatomas.swansea.newsaggregator.interfaces

/**
 * An interface for refresh functionality observers.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 * */
interface IRefreshObserver {
    /**
     * A refresh notification method.
     *
     * @param userTriggered Indicates whether the refresh was triggered by the user or the application.
     * */
    fun refresh(userTriggered: Boolean)
}