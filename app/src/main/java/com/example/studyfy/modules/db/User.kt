data class User(
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val biography: String = "",
    val gradeLevel: String = "",
    val profileImageUrl: String = "", // 🔥 Eklenen alan
    val followers: List<String> = emptyList(),
    val following: List<String> = emptyList()
)
