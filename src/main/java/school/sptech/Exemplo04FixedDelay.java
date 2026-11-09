package school.sptech;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Exemplo04FixedDelay {
    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // Passo 4 - scheduleWithFixedDelay(): intervalo contado do fim de uma execução ao início da próxima
        scheduler.scheduleWithFixedDelay(
                new TarefaAgendada("Geração de relatório de vendas"),
                1,
                4,
                TimeUnit.SECONDS
        );

        // A tarefa repete para sempre e o programa não termina sozinho.
        // Quando quiser parar, use o botão Stop da IDE ou Ctrl+C no terminal.
    }
}
