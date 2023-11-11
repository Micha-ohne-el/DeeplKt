package moe.micha.deeplkt.retrying

enum class FailureReason {
    TooManyRequests,
    QuotaExceeded,
    ServerError,
    OtherError;
}
