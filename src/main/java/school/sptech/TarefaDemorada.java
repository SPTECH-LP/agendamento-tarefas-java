package school.sptech;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TarefaDemorada implements Runnable {

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final String nome;
    private final long duracaoMillis;

    public TarefaDemorada(String nome, long duracaoMillis) {
        this.nome = nome;
        this.duracaoMillis = duracaoMillis;
    }

    @Override
    public void run() {
        imprimir("início");
        try {
            // Simula um trabalho que demora, como uma consulta pesada ao banco
            Thread.sleep(duracaoMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        imprimir("fim");
    }

    private void imprimir(String etapa) {
        String mensagem = "[%s] %s: %s (%s)".formatted(
              LocalTime.now().format(FORMATO_HORA),
              nome,
              etapa,
              Thread.currentThread().getName()
        );
        System.out.println(mensagem);
    }
}
