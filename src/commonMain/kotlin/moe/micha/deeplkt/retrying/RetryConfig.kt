package moe.micha.deeplkt.retrying

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * @property retryCount The number of the current retry (i.e. starts at 1 and counts up).
 *
 * You can use [retryCount] and [failureReason] to make decisions about whether to retry the request or not.
 * Requests are always retried with exponential backoff, doubling the delay between requests each time.
 * Call [retry] to have the request be retried and [stopRequest] to have it be dropped.
 * You can set limits with [capRetriesAt] and [capDelayAt], and lift them with [allowInfiniteRetries] and [allowInfiniteDelay].
 */
data class RetryConfig(
    val retryCount: Int,
    val failureReason: FailureReason,
) {
    internal var shouldRetry = true
        private set

    internal var retryCap: Int? = 3
        private set

    internal var delayCap: Duration? = 5.seconds
        private set

    /**
     * Indicates that the request should be retried.
     *
     * When specifying [retry] and [stopRequest], you can override one with the other. Only the last call is respected.
     */
    fun retry() {
        shouldRetry = true
    }

    /**
     * Indicates that the request should be dropped and not retried any further.
     *
     * When specifying [retry] and [stopRequest], you can override one with the other. Only the last call is respected.
     */
    fun stopRequest() {
        shouldRetry = false
    }

    /**
     * Limits the amount of retries. 0 (or negative numbers) mean no retries will be made.
     */
    fun capRetriesAt(amount: Int) {
        retryCap = amount
    }

    /**
     * Limits the delay between retries. [Duration.ZERO] means all retries will happen right after one another, with no delay.
     */
    fun capDelayAt(amount: Duration) {
        delayCap = amount
    }

    /**
     * Disables the retry limit.
     *
     * Implementation detail: Doesn't actually disable it, only sets it to [Int.MAX_VALUE].
     */
    fun allowInfiniteRetries() {
        retryCap = null
    }

    /**
     * Disables the delay limit.
     *
     * Implementation detail: Doesn't actually disable it, only sets it to [Duration.INFINITE]
     * (which corresponds to [Long.MAX_VALUE] milliseconds).
     */
    fun allowInfiniteDelay() {
        delayCap = null
    }
}
