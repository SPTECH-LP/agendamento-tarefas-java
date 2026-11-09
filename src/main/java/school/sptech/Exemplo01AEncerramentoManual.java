package school.sptech;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Exemplo01AEncerramentoManual {
    public static void main(String[] args) {
        // Passo 1.1: cria o agendador, com uma única thread para executar as tarefas
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // Passo 1.2: agenda uma tarefa escrita com lambda para rodar uma vez, daqui a 2 segundos
        scheduler.schedule(() -> {
            System.out.println("Enviando e-mail de boas-vindas (thread: "
                    + Thread.currentThread().getName() + ")");
        }, 2, TimeUnit.SECONDS);

        System.out.println("E-mail agendado para daqui a 2 segundos (thread: "
                + Thread.currentThread().getName() + ")");
        System.out.println("Fim do main.");

        // Passo 1.4: descomente o bloco abaixo para o programa terminar sozinho
        // scheduler.shutdown();
        // try {
        //     if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
        //         scheduler.shutdownNow();
        //     }
        // } catch (InterruptedException e) {
        //     scheduler.shutdownNow();
        //     Thread.currentThread().interrupt();
        // }
    }
}
