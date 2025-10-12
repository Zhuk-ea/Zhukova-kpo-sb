package moscowzoo.services;

import moscowzoo.domains.Animal;
import org.springframework.stereotype.Component;

/**
 * Ветеринарная клиника для проверки здоровья животных.
 * Принимает решение о принятии животного в зоопарк.
 */
@Component
public class VeterinaryClinic {

    /**
     * Проверяет здоровье животного "в первый раз" и принимает решение о принятии в зоопарк.
     *
     * @param animal животное для проверки
     * @return true, если животное здорово и может быть принято
     */
    public boolean checkHealth(Animal animal) {
        // Предполагаем, что больно каждое пятое животное
        boolean isHealthy = Math.random() > 0.2;
        animal.setHealthy(isHealthy);
        return isHealthy;
    }

    /**
     * Узнаёт здоровье животного, которое считается уже проверенным.
     *
     * @param animal животное для проверки
     * @return true, если животное здорово
     */
    public boolean isHealthy(Animal animal) {
        return animal.isHealthy();
    }
}