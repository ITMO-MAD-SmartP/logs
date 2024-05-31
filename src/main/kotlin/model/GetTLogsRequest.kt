package model

import com.fasterxml.jackson.annotation.JsonFormat
import java.sql.Timestamp

data class GetTLogsRequest(
    var requestId: Int,
    var sensorId: Int,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    var startTime: Timestamp?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    var endEndTime: Timestamp?
)
