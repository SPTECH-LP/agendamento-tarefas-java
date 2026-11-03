package school.sptech;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Exemplo4ComparandoRateEDelay {
    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

        // Passo 2.4: as duas tarefas demoram 1 segundo e têm intervalo de 3 segundos.
        // Compare os horários de início no console com o diagrama do README.
        scheduler.scheduleAtFixedRate(
                new TarefaDemorada("FixedRate ", 1000),
                0, 3, TimeUnit.SECONDS
        );
        scheduler.scheduleWithFixedDelay(
                new TarefaDemorada("FixedDelay", 1000),
                0, 3, TimeUnit.SECONDS
        );

        // As tarefas repetem para sempre e o programa não termina sozinho.
        // Quando quiser parar, use o botão Stop da IDE ou Ctrl+C no terminal.
    }
}
