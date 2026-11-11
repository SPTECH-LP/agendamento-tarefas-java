package school.sptech.passo04;

import school.sptech.TarefaAgendada;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Exemplo04FixedDelay {

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // scheduleWithFixedDelay(): intervalo contado do fim de uma execução ao início da próxima
        scheduler.scheduleWithFixedDelay(
              new TarefaAgendada("Geração de relatório de vendas"),
              1,
              4,
              TimeUnit.SECONDS
        );
    }
}
