package com.aldemir.mesanews.data.api.model

data class ResponseNewHighlights (
    var data: List<Data>
) {
    data class Data(
        var title: String,
        var description: String,
        var content: String,
        var author: String,
        var published_at: String,
        var highlight: Boolean,
        var url: String,
        var image_url: String
    )
}