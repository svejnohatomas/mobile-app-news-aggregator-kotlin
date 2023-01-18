package io.github.svejnohatomas.swansea.newsaggregator.interfaces

/**
 * An interface for refresh confirmation functionality.
 *
 * @author Tomas Svejnoha (987451@swansea.ac.uk)
 * @version 1.0.0
 * */
interface IConfirmationObserver {
    /**
     * Confirms the refresh.
     * */
    fun onConfirm()
}