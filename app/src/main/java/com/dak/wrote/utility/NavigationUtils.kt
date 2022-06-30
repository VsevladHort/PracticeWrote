package com.dak.wrote.utility

fun String.toNav() = replace('/', '\\')
fun String.fromNav() = replace('\\', '/')
