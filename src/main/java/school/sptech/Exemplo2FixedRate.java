package school.sptech;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Exemplo2FixedRate {
    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

        // Passo 2.2 - scheduleAtFixedRate(): intervalo contado do início de uma execução ao início da próxima
        scheduler.scheduleAtFixedRate(
                new TarefaAgendada("Verificação de pedidos pendentes"),
                1, 3, TimeUnit.SECONDS
        );

        // A tarefa repete para sempre e o programa não termina sozinho.
        // Quando quiser parar, use o botão Stop da IDE ou Ctrl+C no terminal.
    }
}
