package school.sptech.passo01;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Exemplo01BTryWithResources {

    public static void main(String[] args) {
        // Passo 1.5: o try-with-resources chama close() no fim do bloco,
        // que faz o shutdown() e espera as tarefas terminarem
        try (ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()) {
            scheduler.schedule(() -> {
                String mensagem = "Enviando e-mail de boas-vindas (thread: %s)"
                      .formatted(Thread.currentThread().getName());
                System.out.println(mensagem);
            }, 2, TimeUnit.SECONDS);
        }
        System.out.println("Fim do main.");
    }
}
