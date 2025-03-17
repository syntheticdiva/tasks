package com.example.tasks.config;

import com.example.tasks.exception.ConfigFileNotFoundException;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.File;

/**
 * Конфигурационный класс для проверки необходимых условий при старте приложения.
 * <p>
 * Реализует проверку наличия обязательного конфигурационного файла {@code config.txt}
 * в корневой директории приложения. Выбрасывает исключение при отсутствии файла,
 * предотвращая дальнейшую работу приложения.
 * </p>
 */
@Configuration
public class AppConfig implements EnvironmentAware {
    /**
     * Проверяет наличие конфигурационного файла при инициализации окружения.
     *
     * @param environment объект окружения Spring
     * @throws ConfigFileNotFoundException если файл {@code config.txt} не найден
     *         в корневой директории приложения (например, {@code ./config.txt})
     * Выполняет следующие проверки:
     * <ol>
     *   <li>Создает объект File для пути {@code ./config.txt}</li>
     *   <li>Проверяет существование файла в файловой системе</li>
     *   <li>Генерирует исключение с описанием проблемы при отсутствии файла</li>
     * </ol>
     */
    @Override
    public void setEnvironment(Environment environment) {
        File configFile = new File("./config.txt");
        if (!configFile.exists()) {
            throw new ConfigFileNotFoundException("Configuration file config.txt not found in root directory");
        }
    }
}