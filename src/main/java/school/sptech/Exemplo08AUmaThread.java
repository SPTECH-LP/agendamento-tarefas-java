package school.sptech;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Exemplo08AUmaThread {
    public static void main(String[] args) {
        // Passo 8: agendador com 1 thread: as duas tarefas dividem a mesma thread
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // Tarefa rápida: deveria rodar a cada 1 segundo
        scheduler.scheduleAtFixedRate(
                new TarefaAgendada("Verificação de pedidos"),
                0,
                1,
                TimeUnit.SECONDS
        );

        // Tarefa lenta: demora 4 segundos e começa 2 segundos depois
        scheduler.schedule(
                new TarefaDemorada("Relatório pesado", 4000),
                2,
                TimeUnit.SECONDS
        );

        // A verificação repete para sempre e o programa não termina sozinho.
        // Quando quiser parar, use o botão Stop da IDE ou Ctrl+C no terminal.
    }
}
