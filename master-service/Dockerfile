# Используем легковесный образ с Java 17
FROM eclipse-temurin:17-jre-alpine

# Создаем папку для приложения
WORKDIR /app

# Копируем собранный jar-файл внутрь контейнера
# Убедись, что путь совпадает с тем, куда Gradle собирает твой jar
COPY build/libs/*-SNAPSHOT.jar app.jar

# Команда для запуска
ENTRYPOINT ["java", "-jar", "app.jar"]