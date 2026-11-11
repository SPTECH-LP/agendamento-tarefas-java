package school.sptech.passo05;

import school.sptech.TarefaDemorada;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Exemplo05ComparandoRateEDelay {

    public static void main(String[] args) {
        ScheduledExecutorService schedulerRate =
              Executors.newSingleThreadScheduledExecutor();

        ScheduledExecutorService schedulerDelay =
              Executors.newSingleThreadScheduledExecutor();

        // As duas tarefas demoram 1 segundo e têm intervalo de 3 segundos.
        // Compare os horários de início no console com o diagrama do README.
        schedulerRate.scheduleAtFixedRate(
              new TarefaDemorada("FixedRate ", 1000),
              0, 3, TimeUnit.SECONDS
        );
        schedulerDelay.scheduleWithFixedDelay(
              new TarefaDemorada("FixedDelay", 1000),
              0, 3, TimeUnit.SECONDS
        );
    }
}
