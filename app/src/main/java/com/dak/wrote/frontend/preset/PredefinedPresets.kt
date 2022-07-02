package com.dak.wrote.frontend.preset

import com.dak.wrote.frontend.editor.*

val characterPreset =
    BasicPreset(
        "Character", setOf(), setOf("character"),
        SerializablePageLayout(
            listOf(
                SerializableParagraphLayout(
                    "Summary",
                    listOf()
                ),
                SerializableParagraphLayout(
                    "History",
                    listOf()
                ),
                SerializableParagraphLayout(
                    "Relations",
                    listOf()
                ),
                SerializableParagraphLayout(
                    "Talents",
                    listOf()
                )
            )
        )
    )

val nationPreset = BasicPreset(
    "Nation", setOf(),
    setOf("nation"), SerializablePageLayout(
        listOf(
            SerializableParagraphLayout(
                "Summary",
                listOf()
            ),
            SerializableParagraphLayout(
                "History", listOf()
            ),
            SerializableParagraphLayout(
                "Rulers", listOf()
            ),
            SerializableParagraphLayout(
                "Items", listOf(
                    SerializableItemListLayout(listOf())
                )
            ),
        )
    )
)

val emptyPreset = BasicPreset("Empty", setOf(), setOf(), SerializablePageLayout(emptyList()))

val predefinedPresets = listOf(
    characterPreset,
    nationPreset,
    emptyPreset
)