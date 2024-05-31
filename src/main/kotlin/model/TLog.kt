package model;

import com.fasterxml.jackson.annotation.JsonFormat
import java.sql.Timestamp

data class TLog(
    var sensorId: Int,
    var type: String,
    var value: Double,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    var time: Timestamp
)
