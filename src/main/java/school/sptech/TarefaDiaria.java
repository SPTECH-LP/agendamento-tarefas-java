package school.sptech;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TarefaDiaria implements Runnable {

    private final ScheduledExecutorService scheduler;
    private final LocalTime horario;

    public TarefaDiaria(ScheduledExecutorService scheduler, LocalTime horario) {
        this.scheduler = scheduler;
        this.horario = horario;
    }

    public void agendarProxima() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime proximaExecucao = agora.with(horario);

        if (!proximaExecucao.isAfter(agora)) {
            proximaExecucao = proximaExecucao.plusDays(1);
        }

        Duration espera = Duration.between(agora, proximaExecucao);
        System.out.println("Próxima execução: " + proximaExecucao);
        scheduler.schedule(this, espera.toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        try {
            System.out.println("Gerando o relatório diário...");
            // lógica da tarefa
        } catch (RuntimeException e) {
            System.err.println("Erro no relatório diário: " + e.getMessage());
        } finally {
            if (!scheduler.isShutdown()) {
                agendarProxima();
            }
        }
    }
}
