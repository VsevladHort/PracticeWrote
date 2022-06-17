package com.dak.wrote.backend.abstractions.entities

interface Book : TreeEntity, UniqueEntity {
    var title: String
}