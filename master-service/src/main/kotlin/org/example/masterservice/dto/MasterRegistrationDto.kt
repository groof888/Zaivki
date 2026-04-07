package org.example.masterservice.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class MasterRegistrationDto(
    @JsonProperty("userId")
    val userId: Long? = null,

    @JsonProperty("name")
    val name: String? = null,

    @JsonProperty("specialization")
    val specialization: String? = null,

    @JsonProperty("experienceYears")
    val experienceYears: Int? = null
)