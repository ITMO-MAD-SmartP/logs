package model;

import java.sql.Timestamp

data class TLog(
    var sensorId: Int,
    var type: String,
    var value: Double,
    var time: Timestamp
)
