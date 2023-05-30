package errors

sealed class InternetError(val message: String) {
    class NoInternet(message: String) : InternetError(message)
    class MalformedUrl(message: String) : InternetError(message)
}