package com.example.pledge.ui

import android.content.Context
import com.example.pledge.R
import java.util.Locale

class LanguageManager(private val context: Context) {

    fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        // Перезапускаем активити для применения изменений
        if (context is android.app.Activity) {
            context.recreate()
        }
    }

    fun showLanguageMenu(anchor: android.view.View) {
        val popupMenu = androidx.appcompat.widget.PopupMenu(context, anchor)
        popupMenu.inflate(R.menu.language_menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.lang_ru -> {
                    setLocale("ru")
                    true
                }

                R.id.lang_en -> {
                    setLocale("en")
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }
}