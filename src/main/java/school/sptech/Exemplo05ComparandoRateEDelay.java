package school.sptech;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Exemplo05ComparandoRateEDelay {
    public static void main(String[] args) {
        // Passo 5: cada tarefa tem o seu próprio agendador de uma thread,
        // para uma não atrasar a outra (o passo 8 mostra o que acontece se dividirem a thread)
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

        // As tarefas repetem para sempre e o programa não termina sozinho.
        // Quando quiser parar, use o botão Stop da IDE ou Ctrl+C no terminal.
    }
}
