package model

import com.fasterxml.jackson.annotation.JsonFormat
import java.sql.Timestamp

data class GetTLogsResponseNode(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    var time: Timestamp,
    var value: Double
)
