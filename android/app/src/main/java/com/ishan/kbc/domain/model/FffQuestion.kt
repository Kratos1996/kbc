package com.ishan.kbc.domain.model

data class FffQuestion(
    val id: String,
    val text: String,
    val items: List<FffItem>,
    val correctOrder: List<Int>,
    val timeLimitSec: Int = 30,
)

data class FffItem(
    val id: Int,
    val label: String,
    val text: String,
)

data class FffResult(
    val correct: Boolean,
    val correctOrder: List<Int>,
    val userOrder: List<Int>,
    val timeTaken: Int,
)

fun sampleFffQuestions(): List<FffQuestion> = listOf(
    FffQuestion(
        id = "fff_1",
        text = "Arrange these historical events from earliest to latest:",
        items = listOf(
            FffItem(1, "A", "The French Revolution"),
            FffItem(2, "B", "Moon Landing (Apollo 11)"),
            FffItem(3, "C", "Magna Carta Signing"),
            FffItem(4, "D", "Industrial Revolution Begins"),
        ),
        correctOrder = listOf(3, 1, 4, 2),
        timeLimitSec = 30,
    ),
    FffQuestion(
        id = "fff_2",
        text = "Arrange these Indian cities from North to South:",
        items = listOf(
            FffItem(1, "A", "Chennai"),
            FffItem(2, "B", "Delhi"),
            FffItem(3, "C", "Kolkata"),
            FffItem(4, "D", "Mumbai"),
        ),
        correctOrder = listOf(2, 3, 4, 1),
        timeLimitSec = 30,
    ),
    FffQuestion(
        id = "fff_3",
        text = "Arrange these by increasing size (smallest first):",
        items = listOf(
            FffItem(1, "A", "Earth"),
            FffItem(2, "B", "Sun"),
            FffItem(3, "C", "Moon"),
            FffItem(4, "D", "Solar System"),
        ),
        correctOrder = listOf(3, 1, 2, 4),
        timeLimitSec = 30,
    ),
    FffQuestion(
        id = "fff_4",
        text = "Arrange these by year of establishment (earliest first):",
        items = listOf(
            FffItem(1, "A", "Google"),
            FffItem(2, "B", "Microsoft"),
            FffItem(3, "C", "Apple"),
            FffItem(4, "D", "Amazon"),
        ),
        correctOrder = listOf(3, 2, 4, 1),
        timeLimitSec = 25,
    ),
)
