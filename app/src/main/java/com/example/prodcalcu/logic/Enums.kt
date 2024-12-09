package com.example.prodcalcu.logic

enum class CampType(val dailyCalories: Int, val displayName: String) {
    PERMANENT_CAMP(2500, "Лагерь постоянный"),
    RADIAL_EXCURSIONS(3000, "Радиальные вылазки"),
    MOBILE_CAMP(3500, "Подвижный лагерь")
}

enum class Climate(val calorieAdjustmentPercentage: Int, val displayName: String) {
    SUMMER(0, "Лето"),
    DEMISEASON(7, "Демисезон"),
    WINTER(14, "Зима")
}

enum class MealType(val calorieShare: Int, val displayName: String) {
    BREAKFAST(27, "Завтрак"),
    LUNCH(40, "Обед"),
    DINNER(33, "Ужин")
}

