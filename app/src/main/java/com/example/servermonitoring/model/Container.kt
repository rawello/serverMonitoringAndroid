package com.example.servermonitoring.model

data class Container(
    val id: String,
    val names: List<String>,
    val image: String,
    val status: String,
    val state: String
) {
    companion object {
        fun parseFromList(containerStrings: List<String>): List<Container> {
            return containerStrings.mapNotNull { parseSingleContainer(it) }
        }

        private fun parseSingleContainer(containerString: String): Container? {
            try {
                // ID
                val idStart = containerString.indexOf("ID: ") + 4
                val idEnd = containerString.indexOf(",", idStart)
                val id = containerString.substring(idStart, idEnd).trim()

                // Names
                val namesStart = containerString.indexOf("Names: [") + 8
                val namesEnd = containerString.indexOf("]", namesStart)
                val namesContent = containerString.substring(namesStart, namesEnd)
                val names = namesContent.split(",").map {
                    it.trim().removeSurrounding("\"")
                }

                // Status
                val statusStart = containerString.indexOf("Status: ") + 8
                val statusEnd = containerString.indexOf(",", statusStart)
                val status = containerString.substring(statusStart, statusEnd).trim()

                // Image
                val imageStart = containerString.indexOf("Image: ") + 7
                val imageEnd = containerString.indexOf(",", imageStart)
                val image = containerString.substring(imageStart, imageEnd).trim()

                // State
                val stateStart = containerString.indexOf("State: ") + 7
                val state = containerString.substring(stateStart).trim()

                return Container(id, names, image, status, state)
            } catch (e: Exception) {
                // В случае ошибки парсинга пропускаем этот контейнер
                return null
            }
        }
    }
}