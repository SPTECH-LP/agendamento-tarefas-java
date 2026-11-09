package school.sptech;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Exemplo01BTryWithResources {
    public static void main(String[] args) {
        // Passo 1.5: o try-with-resources chama close() no fim do bloco,
        // que faz o shutdown() e espera as tarefas terminarem
        try (ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()) {
            scheduler.schedule(() -> {
                System.out.println("Enviando e-mail de boas-vindas (thread: "
                        + Thread.currentThread().getName() + ")");
            }, 2, TimeUnit.SECONDS);

            System.out.println("E-mail agendado para daqui a 2 segundos (thread: "
                    + Thread.currentThread().getName() + ")");
        } // aqui o close() encerra o agendador

        System.out.println("Fim do main.");
    }
}
