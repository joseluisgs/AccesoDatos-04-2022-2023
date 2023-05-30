package models

data class User(
    val id: Long = NEW_USER_ID,
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val website: String
) {
    companion object {
        const val NEW_USER_ID = -1L
    }

}