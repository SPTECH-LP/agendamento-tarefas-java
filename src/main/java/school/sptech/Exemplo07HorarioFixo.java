package school.sptech;

import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Exemplo07HorarioFixo {
    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // Passo 7: tarefa em horário fixo que se reagenda a cada execução.
        // Num sistema real seria LocalTime.of(8, 0); aqui usamos daqui a 5 segundos
        // para você ver a execução e o reagendamento sem esperar até amanhã.
        LocalTime horario = LocalTime.now().plusSeconds(5).withNano(0);
        new TarefaDiaria(scheduler, horario).agendarProxima();

        // A tarefa se reagenda para sempre e o programa não termina sozinho.
        // Quando quiser parar, use o botão Stop da IDE ou Ctrl+C no terminal.
    }
}
