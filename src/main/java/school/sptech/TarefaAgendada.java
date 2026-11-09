package school.sptech;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TarefaAgendada implements Runnable {

    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    private final String nome;

    public TarefaAgendada(String nome) {
        this.nome = nome;
    }

    @Override
    public void run() {
        System.out.printf("[%s] %s (%s)%n",
                LocalTime.now().format(FORMATO_HORA),
                nome,
                Thread.currentThread().getName());
    }
}
