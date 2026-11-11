package school.sptech.passo01;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Exemplo01AEncerramentoManual {

    public static void main(String[] args) {
        // Passo 1.1: cria o agendador, com uma única thread para executar as tarefas
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // Passo 1.2: agenda uma tarefa para rodar uma vez, daqui a 2 segundos
        scheduler.schedule(() -> {
            String mensagem = "Enviando e-mail de boas-vindas (thread: %s)"
                  .formatted(Thread.currentThread().getName());
            System.out.println(mensagem);
        }, 2, TimeUnit.SECONDS);

        System.out.println("Fim do main.");

        // Passo 1.4: descomente a linha abaixo para o programa terminar sozinho
        // scheduler.shutdown();
    }
}
