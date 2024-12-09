import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
                actions = {
                    IconButton(onClick = {
                        exportToExcel(context, days)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Export")
                    }
                }
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

                // Отображаем карточки для каждого уникального блюда
                groupedIngredients.forEach { (mealName, ingredients) ->
                    MealCard(mealName, ingredients, productsCollection)
                }
            }
        }
    }
}

@Composable
fun MealCard(mealName: String, ingredients: List<Ingredient>, products: CollectionReference) {
    val productDetails = remember { mutableStateListOf<Pair<String, Int>>() } // Список пар (название продукта, вес)

    LaunchedEffect(ingredients) {
        productDetails.clear() // Очищаем список перед загрузкой

        // Получаем информацию о каждом продукте для ингредиентов
        for (ingredient in ingredients) {
            val productDocument = products.document(ingredient.productId.toString()).get().await()
            val product = productDocument.toObject(Product::class.java)

            if (product != null) {
                productDetails.add(Pair(product.name, ingredient.weightPerPortion))
            }
        }
    }

    Card(modifier = Modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(mealName, style = MaterialTheme.typography.headlineMedium)

            // Отображаем информацию о продуктах и граммовке
            Column {
                productDetails.forEach { (productName, weight) ->
                    Text("$productName: $weight гр")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

data class Ingredient(
    val type: String = "", // Тип приема пищи (Завтрак/Обед/Ужин)
    val meal: String = "", // Название блюда
    val productId: Int = 0, // Идентификатор продукта
    val weightPerPortion: Int = 0 // Вес ингредиента в граммах в порции блюда
)

data class Product(
    val name: String = "",
    val proteins: Int = 0,
    val fats: Int = 0,
    val carbohydrates: Int = 0,
    val calories: Int = 0,
)

fun exportToExcel(context: Context, days: List<List<Ingredient>>) {
    // Логика экспорта в Excel (не реализована в этом примере)
}