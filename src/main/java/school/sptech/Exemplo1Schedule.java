package school.sptech;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Exemplo1Schedule {
    public static void main(String[] args) {
        // Passo 1: cria o agendador com um pool de 3 threads
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

        // Passo 2.1 - schedule(): executa uma única vez, após o tempo informado
        scheduler.schedule(new TarefaAgendada("Envio de e-mail de boas-vindas"), 2, TimeUnit.SECONDS);
        System.out.println("E-mail agendado para daqui a 2 segundos.");

        // Passo 5: o shutdown() não cancela execuções únicas que já estavam agendadas,
        // então o e-mail ainda é enviado e o awaitTermination espera por ele
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("Agendador encerrado.");
    }
}
