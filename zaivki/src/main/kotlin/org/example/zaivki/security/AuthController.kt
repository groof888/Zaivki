package org.example.zaivki.security

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(private val jwtService: JwtService) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): String {
        return jwtService.generateToken(request.username)
    }
}

data class LoginRequest(val username: String)