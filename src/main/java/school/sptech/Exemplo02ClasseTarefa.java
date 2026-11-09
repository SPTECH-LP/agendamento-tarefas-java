package school.sptech;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Exemplo02ClasseTarefa {
    public static void main(String[] args) {
        // O try-with-resources encerra o agendador no fim do bloco (passo 1.5)
        try (ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()) {

            // Passo 2: as duas formas de escrever a mesma tarefa
            // Com lambda: o código da tarefa fica direto na chamada
            scheduler.schedule(() -> {
                System.out.println("Envio de e-mail de boas-vindas (feito com lambda)");
            }, 1, TimeUnit.SECONDS);

            // Com classe: o código fica no método run() de TarefaAgendada
            scheduler.schedule(
                    new TarefaAgendada("Envio de e-mail de boas-vindas (feito com classe)"),
                    2,
                    TimeUnit.SECONDS
            );

            System.out.println("Duas tarefas agendadas.");
        } // aqui o close() chama shutdown() e espera as duas tarefas terminarem

        System.out.println("Agendador encerrado.");
    }
}
