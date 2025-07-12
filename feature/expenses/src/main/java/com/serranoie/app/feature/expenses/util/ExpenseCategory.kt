package com.serranoie.app.feature.expenses.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.rounded.CompareArrows
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Percent
import androidx.compose.ui.graphics.vector.ImageVector

enum class ExpenseCategory(val displayName: String) {
    FOOD("Food"),
    TRANSPORT("Transport"),
    ACCOMMODATION("Accommodation"),
    ACTIVITIES("Activities"),
    SOUVENIRS("Souvenirs"),
    SHOPPING("Shopping"),
    FUN("Fun"),
    HEALTH("Health"),
    MISC("Other");

    companion object {
        fun fromCategoryName(categoryName: String): ExpenseCategory {
            return entries.find {
                it.name.equals(categoryName, ignoreCase = true) ||
                        it.displayName.equals(categoryName, ignoreCase = true)
            } ?: MISC
        }
    }
}

enum class ExpenseSplitType(val displayName: String) {
    EQUAL("Equal"),
    PERCENTAGE("Percentage"),
    MANUAL("Manual");

    companion object {
        fun fromSplitTypeName(splitTypeName: String): ExpenseSplitType {
            return when (splitTypeName.uppercase()) {
                "EQUAL" -> EQUAL
                "PERCENTAGE" -> PERCENTAGE
                "CUSTOM", "MANUAL" -> MANUAL
                else -> EQUAL
            }
        }
    }
}

fun ExpenseCategory.icon(): ImageVector {
    return when (this) {
        ExpenseCategory.FOOD -> Icons.Default.Restaurant
        ExpenseCategory.TRANSPORT -> Icons.Default.DirectionsCar
        ExpenseCategory.ACCOMMODATION -> Icons.Default.Hotel
        ExpenseCategory.ACTIVITIES -> Icons.Default.Event
        ExpenseCategory.SOUVENIRS -> Icons.Default.CardGiftcard
        ExpenseCategory.SHOPPING -> Icons.Default.ShoppingCart
        ExpenseCategory.FUN -> Icons.Default.Celebration
        ExpenseCategory.HEALTH -> Icons.Default.MedicalServices
        ExpenseCategory.MISC -> Icons.Default.HelpOutline
    }
}

fun ExpenseSplitType.icon(): ImageVector {
    return when (this) {
        ExpenseSplitType.EQUAL -> Icons.Rounded.CompareArrows
        ExpenseSplitType.PERCENTAGE -> Icons.Rounded.Percent
        ExpenseSplitType.MANUAL -> Icons.Rounded.Edit
    }
}
