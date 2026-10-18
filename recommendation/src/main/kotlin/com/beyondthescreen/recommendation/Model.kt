package com.beyondthescreen.recommendation

enum class Motif {
    SHIP,
    REGENCY,
    WORMHOLE,
    CYMBAL,
    HOTEL,
    SUSHI,
    DRAGON,
    BUNNY,
    ROBOT,
    MACHINE,
}

data class Attribution(
    val credit: String,
    val license: String,
    val licenseUrl: String,
    val sourceUrl: String,
)

data class Title(
    val id: String,
    val name: String,
    val year: Int,
    val kind: String,
    val runtimeLabel: String?,
    val logline: String,
    val synopsis: String,
    val themes: List<String>,
    val playbackUrl: String?,
    val attribution: Attribution?,
    val motif: Motif,
) {
    val playable: Boolean get() = !playbackUrl.isNullOrBlank()
}

enum class Source {
    BEDROCK,
    CURATED,
}

data class Suggestion(
    val name: String,
    val why: String,
    val searchQuery: String,
) {
    val url: String get() = SearchLinks.urlFor(searchQuery)
}

data class Category(
    val id: String,
    val label: String,
    val suggestions: List<Suggestion>,
)

data class RecommendationSet(
    val titleId: String,
    val source: Source,
    val categories: List<Category>,
    val note: String? = null,
    val detail: String? = null,
)
