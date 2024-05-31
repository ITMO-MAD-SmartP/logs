package model

data class TLogsResponse(
    var requestId: Int,
    var list: List<GetTLogsResponseNode>
)
