package school.sptech;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Exemplo03FixedRate {
    public static void main(String[] args) {
        // Sem try-with-resources: o agendador fica ligado enquanto o programa estiver rodando
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // Passo 3 - scheduleAtFixedRate(): intervalo contado do início de uma execução ao início da próxima
        scheduler.scheduleAtFixedRate(
                new TarefaAgendada("Verificação de pedidos pendentes"),
                1,
                3,
                TimeUnit.SECONDS
        );

        // A tarefa repete para sempre e o programa não termina sozinho.
        // Quando quiser parar, use o botão Stop da IDE ou Ctrl+C no terminal.
    }
}
