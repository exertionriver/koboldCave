package org.river.exertion.ai.noumena

enum class NoumenonType {

    OTHER { override fun tag() = "other" },
        BEING { override fun tag() = "being" },
            HUMANOID { override fun tag() = "humanoid" },
                LOW_RACE { override fun tag() = "low-race" },
                    KOBOLD { override fun tag() = "kobold" },
            ANIMAL { override fun tag() = "animal" },
                BEAST { override fun tag() = "beast" },
                BIRD { override fun tag() = "bird" },
                AQUATIC { override fun tag() = "aquatic" },
            PLANT { override fun tag() = "plant" },
            FUNGUS { override fun tag() = "fungus" },
                MUSHROOM { override fun tag() = "mushroom" },
                    WHITE_CAP { override fun tag() = "white-cap" },
                    BUTTER_BROWN { override fun tag() = "butter-brown" },
                LICHEN { override fun tag() = "lichen" },
                     LIGHT_SCENT { override fun tag() = "light-scent" },
            INSECT { override fun tag() = "insect" },
                BLACK_BEETLE { override fun tag() = "black-beetle" },
        NON_BEING { override fun tag() = "non-being" },
            AUTOMATON { override fun tag() = "automaton" },
                SKELETON { override fun tag() = "skeleton" },
            UNDEAD { override fun tag() = "undead" },
                GHOUL { override fun tag() = "ghoul" },

    ELEMENT { override fun tag() = "element" },
        WATER { override fun tag() = "water" },

    INDIVIDUAL { override fun tag() = "individual" },

    GROUP { override fun tag() = "group" },
        RED_HAND { override fun tag() = "red hand" },

    NONE { override fun tag() = "none" }

    ;
    abstract fun tag() : String
}