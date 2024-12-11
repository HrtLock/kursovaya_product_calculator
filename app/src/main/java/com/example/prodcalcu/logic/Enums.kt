package com.example.prodcalcu.logic

enum class CampType(val dailyCalories: Int, val displayName: String) {
    PERMANENT_CAMP(2000, "Лагерь"),
    RADIAL_EXCURSIONS(2500, "Радиальные вылазки"),
    MOBILE_CAMP(3500, "Переходы")
}

enum class Climate(val calorieAdjustmentPercentage: Int, val displayName: String) {
    SUMMER(0, "Лето"),
    DEMISEASON(3, "Демисезон"),
    WINTER(5, "Зима")
}

enum class MealType(val calorieShare: Int, val displayName: String) {
    BREAKFAST(27, "Завтрак"),
    LUNCH(40, "Обед"),
    DINNER(33, "Ужин")
}

