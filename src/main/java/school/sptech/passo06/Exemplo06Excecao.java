package school.sptech.passo06;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Exemplo06Excecao {

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // Passo 6: sem try/catch, a primeira exceção cancela todas as próximas execuções
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("[sem try/catch] consultando o banco...");
            throw new RuntimeException("banco de dados fora do ar");
        }, 1, 3, TimeUnit.SECONDS);

        // Com try/catch, o erro aparece no console e a tarefa continua rodando
        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("[com try/catch] consultando o banco...");
                throw new RuntimeException("banco de dados fora do ar");
            } catch (RuntimeException e) {
                System.err.println("[com try/catch] erro tratado: " + e.getMessage());
            }
        }, 1, 3, TimeUnit.SECONDS);
    }
}
