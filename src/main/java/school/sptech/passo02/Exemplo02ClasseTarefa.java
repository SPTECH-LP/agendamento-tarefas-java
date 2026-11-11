package school.sptech.passo02;

import school.sptech.TarefaAgendada;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Exemplo02ClasseTarefa {

    public static void main(String[] args) {
        try (ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()) {
            scheduler.schedule(
                  new TarefaAgendada("Envio de e-mail de boas-vindas (feito com classe)"),
                  2,
                  TimeUnit.SECONDS
            );
        }
        System.out.println("Agendador encerrado.");
    }
}
