package com.aziza.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform