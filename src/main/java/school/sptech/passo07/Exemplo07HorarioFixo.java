package school.sptech.passo07;

import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Exemplo07HorarioFixo {

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // Em produção seria LocalTime.of(8, 0). Aqui, daqui a 5 segundos para não esperar até amanhã.
        LocalTime horario = LocalTime.now().plusSeconds(5).withNano(0);
        new TarefaDiaria(scheduler, horario).agendarProxima();
    }
}
