package moe.micha.deeplkt.retrying

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

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

    fun retry() {
        shouldRetry = true
    }

    fun stopRequest() {
        shouldRetry = false
    }

    fun capRetriesAt(amount: Int) {
        retryCap = amount
    }

    fun capDelayAt(amount: Duration) {
        delayCap = amount
    }

    fun allowInfiniteRetries() {
        retryCap = null
    }

    fun allowInfiniteDelay() {
        delayCap = null
    }
}
