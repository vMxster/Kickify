package it.unibo.kickify.data.models

enum class Language(val langCode: String) {
    ENGLISH("en"),
    ESPANOL("es"),
    ITALIANO("it"),
    LATINUM("la");

    companion object {
        fun getLangFromCode(langCode: String) : Language? {
            return Language.entries.firstOrNull { lang -> lang.langCode == langCode }
        }

        fun getLanguageStringFromCode(langCode: String): String {
            return when(langCode){
                "en" -> "English"
                "es" -> "Español"
                "it" -> "Italiano"
                "la" -> "Latinum"
                else -> ""
            }
        }

        fun getCodeFromLanguageString(lang: String): String{
            return when(lang){
                "English" -> "en"
                "Español" -> "es"
                "Italiano" -> "it"
                "Latinum" -> "la"
                else -> ""
            }
        }

        fun getLanguagesStringList(): List<String>{
            val res = mutableListOf<String>()
            Language.entries.forEach { lang ->
                res.add( getLanguageStringFromCode(lang.langCode) )
            }
            return res
        }
    }
}