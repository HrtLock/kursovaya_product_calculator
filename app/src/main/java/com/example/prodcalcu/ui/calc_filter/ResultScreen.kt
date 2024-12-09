import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
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
    val days = remember { mutableStateListOf<List<Meal>>() }

    val fs = Firebase.firestore
    val meals = fs.collection("meals")
    val products = fs.collection("products")

    LaunchedEffect(Unit) {
        // Генерация рациона на `dayNumber` дней
        val mealsList = meals.get().await().toObjects(Meal::class.java)

        // Создаем переменные для хранения блюд по типам
        val breakfastMeals = mutableListOf<Meal>()
        val lunchMeals = mutableListOf<Meal>()
        val dinnerMeals = mutableListOf<Meal>()

        // Фильтруем блюда по типу и добавляем в соответствующие списки
        for (meal in mealsList) {
            when (meal.type) {
                MealType.BREAKFAST.displayName -> breakfastMeals.add(meal)
                MealType.LUNCH.displayName -> lunchMeals.add(meal)
                MealType.DINNER.displayName -> dinnerMeals.add(meal)
            }
        }

        // Получаем уникальные названия для каждого типа блюд
        val uniqueBreakfastNames = breakfastMeals.map { it.meal }.distinct() // Уникальные названия для завтрака
        val uniqueLunchNames = lunchMeals.map { it.meal }.distinct()         // Уникальные названия для обеда
        val uniqueDinnerNames = dinnerMeals.map { it.meal }.distinct()       // Уникальные названия для ужина

        val mealPlan = mutableListOf<List<String>>()

        for (day in 1..dayNumber) {
            val dailyMeals = mutableListOf<String>()

            // Определяем индекс для каждого типа блюда на основе дня и количества блюд
            if (uniqueBreakfastNames.isNotEmpty()) {
                val breakfastIndex = (day - 1) % uniqueBreakfastNames.size
                dailyMeals.add(uniqueBreakfastNames[breakfastIndex])
            }
            if (uniqueLunchNames.isNotEmpty()) {
                val lunchIndex = (day - 1) % uniqueLunchNames.size
                dailyMeals.add(uniqueLunchNames[lunchIndex])
            }
            if (uniqueDinnerNames.isNotEmpty()) {
                val dinnerIndex = (day - 1) % uniqueDinnerNames.size
                dailyMeals.add(uniqueDinnerNames[dinnerIndex])
            }

            mealPlan.add(dailyMeals)
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
                actions = {
                    IconButton(onClick = {
                        exportToExcel(context, days, products)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Export")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            itemsIndexed(days) { dayIndex, dailyMeals ->
                Text(
                    text = "День ${dayIndex + 1}",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )
                dailyMeals.forEach { meal ->
                    MealCard(meal, products)
                }
                DailySummaryCard(dailyMeals, products)
            }
        }
    }
}

@Composable
fun MealCard(meal: Meal, products: CollectionReference) {
    val ingredients = remember { mutableStateListOf<Product>() }

    LaunchedEffect(meal) {
        // Загрузка ингредиентов для данного блюда
//        val ingredientsList = meal.productIds.map { productId ->
//            products.document(productId).get().await().toObject(Product::class.java)
//        }
//        ingredients.addAll(ingredientsList.filterNotNull())
    }

    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(meal.meal, style = MaterialTheme.typography.headlineMedium)
            Row {
                Column {
                    ingredients.forEach {
                        Text("${it.name}: ${meal.weightPerPortion} гр")
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
//                Image(
//                    painter = rememberImagePainter(meal.imageUrl),
//                    contentDescription = null,
//                    modifier = Modifier.size(64.dp)
//                )
            }
            Spacer(modifier = Modifier.height(8.dp))
//            Text("БЖУК: ${meal.proteins}/${meal.fats}/${meal.carbohydrates}, ${meal.calories} ккал")
        }
    }
}

@Composable
fun DailySummaryCard(meals: List<Meal>, products: CollectionReference) {
    val dailySummary = remember { mutableStateOf(Summary()) }

    LaunchedEffect(meals) {
        // Расчет общей БЖУК и ингредиентов на день
        val summary = Summary()
        meals.forEach { meal ->
//            val ingredientsList = meal.productIds.map { productId ->
//                products.document(productId).get().await().toObject(Product::class.java)
//            }
//            ingredientsList.filterNotNull().forEach { product ->
//                summary.proteins += product.proteins
//                summary.fats += product.fats
//                summary.carbohydrates += product.carbohydrates
//                summary.calories += product.calories
//                summary.ingredients[product.name] = (summary.ingredients[product.name] ?: 0) + meal.weightPerPortion
//            }
        }
        dailySummary.value = summary
    }

    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Дневной расклад", style = MaterialTheme.typography.headlineMedium)
            dailySummary.value.ingredients.forEach { (name, weight) ->
                Text("$name: $weight гр")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("БЖУК: ${dailySummary.value.proteins}/${dailySummary.value.fats}/${dailySummary.value.carbohydrates}, ${dailySummary.value.calories} ккал")
        }
    }
}

fun getNextMeal(groupedMeals: Map<String, List<Meal>>, day: Int, type: String): Meal {
    val mealsForType = groupedMeals[type] ?: emptyList()
    return mealsForType[(day - 1) % mealsForType.size]
}

fun exportToExcel(context: Context, days: List<List<Meal>>, products: CollectionReference) {
    // Логика экспорта в Excel
}

data class Meal(
    val meal: String = "",
    val type: String = "",
    val productId: Int = 0,
    val weightPerPortion: Int = 0,
)

data class Product(
    val name: String = "",
    val proteins: Int = 0,
    val fats: Int = 0,
    val carbohydrates: Int = 0,
    val calories: Int = 0
)

data class Summary(
    var proteins: Int = 0,
    var fats: Int = 0,
    var carbohydrates: Int = 0,
    var calories: Int = 0,
    val ingredients: MutableMap<String, Int> = mutableMapOf()
)
