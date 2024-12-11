import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.prodcalcu.R
import com.example.prodcalcu.logic.CampType
import com.example.prodcalcu.logic.Climate
import com.example.prodcalcu.logic.MealType
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    navController: NavHostController,
    calcName: String,
    dayNumber: Int,
    personNumber: Int,
    activity: CampType,
    season: Climate,
) {
    val context = LocalContext.current
    val days = remember { mutableStateListOf<List<Ingredient>>() }
    val fs = Firebase.firestore
    val ingredientsCollection = fs.collection("meals")
    val productsCollection = fs.collection("products")


    LaunchedEffect(Unit) {
        // Получаем все ингредиенты из Firestore
        val ingredientsList = ingredientsCollection.get().await().toObjects(Ingredient::class.java)

        // Группируем ингредиенты по типу и названию блюда
        val breakfastIngredients = ingredientsList.filter { it.type == MealType.BREAKFAST.displayName }
        val lunchIngredients = ingredientsList.filter { it.type == MealType.LUNCH.displayName }
        val dinnerIngredients = ingredientsList.filter { it.type == MealType.DINNER.displayName }

        // Получаем уникальные названия для каждого типа ингредиентов
        val uniqueBreakfastNames = breakfastIngredients.map { it.meal }.distinct()
        val uniqueLunchNames = lunchIngredients.map { it.meal }.distinct()
        val uniqueDinnerNames = dinnerIngredients.map { it.meal }.distinct()

        // Генерация рациона на `dayNumber` дней
        for (day in 1..dayNumber) {
            val dailyMeals = mutableListOf<Ingredient>()

            // Определяем индекс для каждого типа блюда на основе дня и количества блюд
            if (uniqueBreakfastNames.isNotEmpty()) {
                val breakfastIndex = (day - 1) % uniqueBreakfastNames.size
                dailyMeals.addAll(breakfastIngredients.filter { it.meal == uniqueBreakfastNames[breakfastIndex] })
            }
            if (uniqueLunchNames.isNotEmpty()) {
                val lunchIndex = (day - 1) % uniqueLunchNames.size
                dailyMeals.addAll(lunchIngredients.filter { it.meal == uniqueLunchNames[lunchIndex] })
            }
            if (uniqueDinnerNames.isNotEmpty()) {
                val dinnerIndex = (day - 1) % uniqueDinnerNames.size
                dailyMeals.addAll(dinnerIngredients.filter { it.meal == uniqueDinnerNames[dinnerIndex] })
            }

            days.add(dailyMeals)
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(calcName) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            itemsIndexed(days) { dayIndex, mealPlans ->
                Text(
                    text = "День ${dayIndex + 1}",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )

                // Группируем ингредиенты по названию блюда
                val groupedIngredients = mealPlans.groupBy { it.meal }
                val ingredientsList = remember { mutableStateListOf<List<Ingredient>>() }

                // Отображаем карточки для каждого уникального блюда
                groupedIngredients.forEach { (mealName, ingredients) ->
                    MealCard(mealName, ingredients, productsCollection, personNumber, activity, season)
                    ingredientsList.add(ingredients)
                }
                NutritionCard(mealPlans, productsCollection, personNumber, activity, season)
                if (dayIndex != days.size-1) HorizontalDivider(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp).padding(horizontal = 16.dp),
                    color = colorResource(R.color.black),
                    thickness = 1.dp // Устанавливает толщину линии
                )
            }
        }
    }
}

@Composable
fun MealCard(
    mealName: String,
    ingredients: List<Ingredient>,
    products: CollectionReference,
    personNumber: Int,
    activity: CampType,
    season: Climate,
) {
    val productDetails = remember { mutableStateListOf<Pair<String, Double>>() } // Список пар (название продукта, вес)
    val totalProteins = remember { mutableStateOf(0.0) }
    val totalFats = remember { mutableStateOf(0.0) }
    val totalCarbohydrates = remember { mutableStateOf(0.0) }
    val totalCalories = remember { mutableStateOf(0.0) }
    val totalMass = remember { mutableStateOf(0.0) }

    LaunchedEffect(ingredients) {
        productDetails.clear() // Очищаем список перед загрузкой
        totalProteins.value = 0.0
        totalFats.value = 0.0
        totalCarbohydrates.value = 0.0
        totalCalories.value = 0.0
        totalMass.value = 0.0

        for (ingredient in ingredients) {
            totalMass.value += ingredient.weightPerPortion
        }

        for (ingredient in ingredients) {
            val productDocument = products.document(ingredient.productId.toString()).get().await()
            val product = productDocument.toObject(Product::class.java)

            if (product != null) {
                val ingredientKof = calculateKof(
                    prodCalories = product.calories,
                    mType = ingredient.type,
                    climate = season.calorieAdjustmentPercentage,
                    cType = activity.dailyCalories*(ingredient.weightPerPortion/totalMass.value)
                )

                // Добавляем продукт в список с учетом коэффициента
                productDetails.add(Pair(product.name, ingredient.weightPerPortion * ingredientKof * personNumber))

                // Суммируем значения с учетом коэффициента
                totalProteins.value += (product.proteins  * ingredientKof)
                totalFats.value += (product.fats * ingredientKof)
                totalCarbohydrates.value += (product.carbohydrates * ingredientKof)
                totalCalories.value += (product.calories * ingredientKof)

            }
        }
    }

    Card(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardColors(
            containerColor = colorResource(R.color.white),
            contentColor = colorResource(R.color.black),
            disabledContainerColor = colorResource(R.color.transparent),
            disabledContentColor = colorResource(R.color.transparent),
        )) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(mealName, style = MaterialTheme.typography.headlineSmall)

            // Отображаем информацию о продуктах и их массах
            Column {
                productDetails.forEach { (productName, weight) ->
                    Text("$productName: ${"%.1f".format(weight)} гр")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Выводим БЖУ и калории в виде чипов
            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                Chip(true, text = "${"%.1f".format(totalCalories.value)} ккал")
                Chip(false, text = "${"%.1f".format(totalProteins.value)} Б", modifier = Modifier.padding(end = 4.dp))
                Chip(false, text = "${"%.1f".format(totalFats.value)} Ж", modifier = Modifier.padding(end = 4.dp))
                Chip(false, text = "${"%.1f".format(totalCarbohydrates.value)} У", modifier = Modifier.padding(end = 4.dp))
            }
        }
    }
}



@Composable
fun NutritionCard(
    ingredients: List<Ingredient>,
    products: CollectionReference,
    personNumber: Int,
    activity: CampType,
    season: Climate,
) {
    val productDetails = remember { mutableStateMapOf<String, Double>() } // Используем Map для хранения (название продукта, вес)

    // Переменные для хранения итоговых значений макронутриентов
    val totalProteins = remember { mutableStateOf(0.0) }
    val totalFats = remember { mutableStateOf(0.0) }
    val totalCarbohydrates = remember { mutableStateOf(0.0) }
    val totalCalories = remember { mutableStateOf(0.0) }

    // Группируем ингредиенты по названию блюда
    val groupedIngredients = ingredients.groupBy { it.meal }

    groupedIngredients.forEach { (mealName, ingredients) ->
        LaunchedEffect(ingredients) {
            var totalMass = 0.0
            productDetails.clear()
            totalProteins.value = 0.0
            totalFats.value = 0.0
            totalCarbohydrates.value = 0.0
            totalCalories.value = 0.0

            for (ingredient in ingredients) {
                // Суммируем массу всех ингредиентов
                totalMass += ingredient.weightPerPortion
            }

            for (ingredient in ingredients) {
                val productDocument = products.document(ingredient.productId.toString()).get().await()
                val product = productDocument.toObject(Product::class.java)

                if (product != null) {
                    // Расчет коэффициента с учетом общего веса
                    val ingredientKof = calculateKof(
                        prodCalories = product.calories,
                        mType = ingredient.type,
                        climate = season.calorieAdjustmentPercentage,
                        cType = activity.dailyCalories * (ingredient.weightPerPortion / totalMass)
                    )

                    // Рассчитываем вес для продукта с учетом коэффициента
                    val weightForProduct = ingredient.weightPerPortion * ingredientKof * personNumber

                    // Суммируем граммовки одинаковых продуктов
                    productDetails[product.name] = productDetails.getOrDefault(product.name, 0.0) + weightForProduct

                    // Суммируем значения макронутриентов с учетом коэффициента
                    totalProteins.value += (product.proteins * ingredientKof * personNumber)
                    totalFats.value += (product.fats * ingredientKof * personNumber)
                    totalCarbohydrates.value += (product.carbohydrates * ingredientKof * personNumber)
                    totalCalories.value += (product.calories * ingredientKof * personNumber)
                }
            }
        }
    }

    Card(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardColors(
            containerColor = colorResource(R.color.white),
            contentColor = colorResource(R.color.black),
            disabledContainerColor = colorResource(R.color.transparent),
            disabledContentColor = colorResource(R.color.transparent),
        )) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Использованные продукты:", style = MaterialTheme.typography.headlineSmall)

            // Отображаем список продуктов и их граммовок без повторов
            productDetails.forEach { (productName, weight) ->
                Text(
                    text = "$productName: ${"%.1f".format(weight)} гр",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Выводим БЖУ и калории в виде чипов
            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                Chip(true, text = "${"%.1f".format(totalCalories.value)} ккал")
                Chip(false, text = "${"%.1f".format(totalProteins.value)} Б", modifier = Modifier.padding(end = 4.dp))
                Chip(false, text = "${"%.1f".format(totalFats.value)} Ж", modifier = Modifier.padding(end = 4.dp))
                Chip(false, text = "${"%.1f".format(totalCarbohydrates.value)} У", modifier = Modifier.padding(end = 4.dp))
            }
        }
    }
}





@Composable
fun Chip(isCcal: Boolean, text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = if (isCcal) colorResource(R.color.primaryDark) else colorResource(R.color.primaryLight),
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = colorResource(R.color.white),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

data class Ingredient(
    val type: String = "", // Тип приема пищи (Завтрак/Обед/Ужин)
    val meal: String = "", // Название блюда
    val productId: Int = 0, // Идентификатор продукта
    val weightPerPortion: Double = 0.0 // Вес ингредиента в граммах в порции блюда
)

data class Product(
    val name: String = "",
    val proteins: Double = 0.0,
    val fats: Double = 0.0,
    val carbohydrates: Double = 0.0,
    val calories: Double = 0.0,
)


// Класс для хранения дневной пищевой ценности
data class DailyNutrition(
    val proteins: Double = 0.0,
    val fats: Double = 0.0,
    val carbohydrates: Double = 0.0,
    val calories: Double = 0.0
)



fun calculateKof(
    prodCalories: Double = 0.0,
    mType: String,
    climate: Int = 0,
    cType: Double= 1.0
): Double {
    // Находим MealType по displayName
    val mealTypeVal = MealType.values().find { it.displayName == mType }

    // Вычисляем калории
//    val calories = prodCalories / 100 * prodMass
    // Проверяем cType на ноль перед делением
    if (cType == 0.0) {
        throw IllegalArgumentException("cType не может быть равен нулю.")
    }
    // Вычисляем прием калорий
    val priem = cType / 100 * (mealTypeVal?.calorieShare ?: 1) + cType / 100 * climate

    // Вычисляем коэффициент
    val kof = priem/prodCalories
//    println(kof)
    return kof
}
