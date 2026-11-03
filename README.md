# Agendamento de tarefas em Java

Muitos sistemas precisam fazer coisas sozinhos, sem que ninguém clique em um botão: gerar um
relatório todo dia às 6h, verificar pedidos pendentes a cada 5 minutos, apagar arquivos temporários
de tempos em tempos, reenviar notificações que não foram confirmadas.

Neste tutorial você vai aprender a agendar tarefas desse tipo em Java puro, sem nenhum framework,
usando o `ScheduledExecutorService`, que já vem no JDK. No final, você vai montar com as próprias
mãos uma tarefa que roda todo dia no mesmo horário.

## Rode o exemplo primeiro

1. Clone o repositório.
2. Abra o projeto no IntelliJ ou na IDE que você preferir.
3. Execute a classe `Exemplo1Schedule` e acompanhe o console.

Cada passo do tutorial tem a sua própria classe com um método `main`, de `Exemplo1Schedule` a
`Exemplo6HorarioFixo`. Assim você roda um exemplo de cada vez e o console mostra só o que interessa
naquele passo. Ao longo do texto, cada passo diz qual classe rodar.

> [!IMPORTANT]
> Do `Exemplo2FixedRate` em diante, as tarefas se repetem para sempre e o programa **não termina
> sozinho**. Quando já tiver visto o suficiente no console, pare o programa à mão:
>
> - **IntelliJ:** clique no botão **Stop** (o quadrado vermelho da janela Run) ou use `Ctrl+F2`
>   (`Cmd+F2` no Mac).
> - **Terminal:** pressione `Ctrl+C`.
>
> No passo 5 você vai ver como encerrar o agendador pelo código.

## O que é agendar uma tarefa?

Agendar uma tarefa é pedir para um trecho de código rodar sozinho no futuro: uma única vez, depois
de um tempo, ou várias vezes, em intervalos regulares. O resto da aplicação não precisa "lembrar"
de chamar esse código.

Pense no alarme do celular. Você pode criar um alarme que toca uma vez só, daqui a 10 minutos, ou
um alarme que se repete. Depois de configurado, você não precisa fazer mais nada: ele toca na hora
certa. O `ScheduledExecutorService` é o alarme do seu programa.

> [!NOTE]
> Pesquisando na internet, você vai encontrar exemplos com uma classe chamada `Timer`. Ela é uma
> versão mais antiga, em que um erro em uma única tarefa pode parar todo o agendamento. Prefira o
> `ScheduledExecutorService`, que é o que usamos aqui.

## Passo a passo

### 1. Crie o agendador

Uma tarefa agendada precisa de alguém para executá-la quando chegar a hora. Esse "alguém" é uma
thread, e o agendador mantém um grupo delas pronto para trabalhar, chamado de pool de threads.

Você não cria o agendador com `new`. Quem cria é a classe `Executors`, que tem métodos prontos
para isso:

```java
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
```

O número `3` é a quantidade de threads do pool, ou seja, quantas tarefas podem rodar ao mesmo
tempo. Se chegar a hora de mais tarefas do que há threads livres, as que sobrarem esperam na fila
até uma thread terminar o que está fazendo.

### 2. Agende as tarefas

A tarefa em si é um `Runnable`: um objeto com um método `run()` que contém o código a executar. No
projeto, a classe `TarefaAgendada` implementa `Runnable` e recebe um nome, para você saber no
console qual tarefa rodou.

O agendador tem três métodos de agendamento. Todos recebem a tarefa, os tempos e um `TimeUnit`,
que diz em que unidade estão esses tempos (`SECONDS`, `MINUTES`, `HOURS`...).

#### 2.1. `schedule()`: uma única vez, depois de um tempo

É o alarme que toca uma vez só.

```java
scheduler.schedule(new TarefaAgendada("Envio de e-mail de boas-vindas"), 2, TimeUnit.SECONDS);
```

A tarefa roda uma vez, 2 segundos depois do agendamento, e acabou. Serve para ações que precisam
ser adiadas, como enviar um e-mail alguns segundos depois de um cadastro.

▶ Rode a classe `Exemplo1Schedule`. O e-mail aparece 2 segundos depois e o programa termina sozinho
(no passo 5 você vai entender por quê).

#### 2.2. `scheduleAtFixedRate()`: com intervalo fixo entre inícios

```java
scheduler.scheduleAtFixedRate(
        new TarefaAgendada("Verificação de pedidos pendentes"),
        1, 3, TimeUnit.SECONDS
);
```

| Parâmetro  | O que significa                                                     |
|------------|---------------------------------------------------------------------|
| `1`        | Quanto esperar antes da primeira execução.                          |
| `3`        | Intervalo entre o **início** de uma execução e o início da próxima. |
| `TimeUnit` | Unidade dos dois números acima.                                     |

Pense num ônibus que sai do terminal a cada 15 minutos. O horário de saída é fixo, não importa
quanto tempo a viagem anterior levou. Aqui é igual: a tarefa começa a cada 3 segundos, contados de
um início para o outro.

▶ Rode a classe `Exemplo2FixedRate` e confira no console: um horário a cada 3 segundos.

> [!NOTE]
> E se a tarefa demorar mais do que o intervalo? O agendador não roda duas cópias da mesma tarefa
> ao mesmo tempo. A próxima execução espera a anterior terminar e começa logo em seguida, sem pausa.

#### 2.3. `scheduleWithFixedDelay()`: com intervalo fixo entre fim e início

```java
scheduler.scheduleWithFixedDelay(
        new TarefaAgendada("Geração de relatório de vendas"),
        1, 4, TimeUnit.SECONDS
);
```

Os parâmetros são os mesmos, mas o intervalo agora é contado do **fim** de uma execução até o
**início** da próxima. Pense numa série de exercícios na academia: você descansa 1 minuto depois
de terminar cada série, não importa quanto ela demorou.

Use esse método quando a tarefa precisa de uma folga garantida entre uma execução e outra. Um
exemplo é um processo pesado que consulta o banco: se ele demorar, a próxima execução não começa
colada na anterior.

▶ Rode a classe `Exemplo3FixedDelay`. A tarefa dela termina na hora, então o intervalo fica em 4
segundos, igual ao do `scheduleAtFixedRate`. A diferença só aparece quando a tarefa demora, que é o
que o próximo exemplo mostra.

#### 2.4. Comparando os dois

Imagine uma tarefa que demora 1 segundo, agendada com intervalo de 3 segundos nos dois métodos:

```
segundos                0   1   2   3   4   5   6   7   8   9
scheduleAtFixedRate     [===]       [===]       [===]       [===]
scheduleWithFixedDelay  [===]           [===]           [===]
```

Com `scheduleAtFixedRate`, as execuções começam nos segundos 0, 3, 6 e 9. Com
`scheduleWithFixedDelay`, cada execução termina, espera 3 segundos e só então a próxima começa:
segundos 0, 4 e 8.

▶ Rode a classe `Exemplo4ComparandoRateEDelay`. Ela agenda as duas versões com a classe
`TarefaDemorada`, que usa um `Thread.sleep` para simular um trabalho de 1 segundo. Compare os
horários de "início" no console com o diagrama acima.

### 3. Trate as exceções dentro da tarefa

Este é o erro mais traiçoeiro do agendamento. Se uma tarefa repetida lançar uma exceção que
ninguém trata, o agendador cancela todas as próximas execuções dela. E faz isso em silêncio: nada
aparece no console. As outras tarefas continuam rodando normalmente; só a que falhou nunca mais
roda, e você só descobre quando alguém reclama que o relatório parou de chegar.

A solução é tratar a exceção dentro da própria tarefa, com `try/catch`.

No código abaixo, em vez de criar uma classe como a `TarefaAgendada`, escrevemos a tarefa direto
na chamada, com uma lambda. O `() -> { ... }` é um jeito curto de criar um `Runnable`: o código
entre as chaves é o que estaria dentro do método `run()`.

```java
scheduler.scheduleAtFixedRate(() -> {
    try {
        // lógica da tarefa
    } catch (RuntimeException e) {
        System.err.println("Erro tratado dentro da tarefa: " + e.getMessage());
    }
}, 1, 5, TimeUnit.SECONDS);
```

Assim, se uma execução falhar, o erro aparece no console e a próxima execução acontece normalmente.

▶ Rode a classe `Exemplo5ExcecaoNaTarefa`. Ela agenda duas tarefas que sempre falham, uma sem
`try/catch` e outra com. A primeira aparece no console uma única vez e depois some, sem nenhuma
mensagem de erro. A segunda mostra o erro e continua rodando a cada 3 segundos.

> [!WARNING]
> Coloque o `try/catch` em toda tarefa repetida. Sem ele, uma falha isolada (um banco fora do ar
> por um minuto, por exemplo) desliga a tarefa para sempre.

### 4. Agende em um horário fixo com `java.time` ("todo dia às 8h")

Até aqui, todos os tempos eram relativos: "daqui a 2 segundos", "a cada 3 segundos". Mas muitas
tarefas reais são marcadas no relógio: "todo dia às 8h". O `ScheduledExecutorService` não tem um
método para isso, então você vai montar essa lógica com as próprias mãos.

#### Por que não usar `scheduleAtFixedRate` com 24 horas?

Parece a solução óbvia: calcular quanto falta até as 8h e repetir a cada 24 horas. O problema é
que pequenos atrasos se acumulam dia após dia, e uma mudança de horário (como o horário de verão)
desloca a tarefa uma hora para sempre. Depois de um tempo, a tarefa "das 8h" passa a rodar às
8h03, ou às 9h.

A saída é recalcular o horário a cada execução. A ideia tem três partes:

1. Calcular quanto tempo falta até a próxima 8h.
2. Agendar uma única execução com `schedule()`, usando esse tempo.
3. Dentro da tarefa, depois de fazer o trabalho, repetir o cálculo e agendar a próxima.

É como um despertador que você precisa reprogramar toda manhã: ao acordar, você já deixa marcado o
de amanhã.

#### 4.1. Calcule quanto falta até as 8h

```java
LocalDateTime agora = LocalDateTime.now();
LocalDateTime proximaExecucao = agora.with(LocalTime.of(8, 0));

if (!proximaExecucao.isAfter(agora)) {
    proximaExecucao = proximaExecucao.plusDays(1);
}

Duration espera = Duration.between(agora, proximaExecucao);
```

Linha a linha:

- `agora.with(LocalTime.of(8, 0))` pega a data de hoje e troca a hora para 8h00.
- Se esse horário já passou (o programa rodou às 10h, por exemplo), a próxima execução é amanhã,
  então somamos um dia com `plusDays(1)`.
- `Duration.between(agora, proximaExecucao)` calcula o intervalo entre os dois momentos. Se agora
  são 22h, o resultado é 10 horas.

#### 4.2. Agende uma única execução

O `schedule()` não recebe um `Duration`, e sim um número e um `TimeUnit`. Por isso convertemos a
espera para milissegundos:

```java
scheduler.schedule(tarefa, espera.toMillis(), TimeUnit.MILLISECONDS);
```

#### 4.3. Faça a tarefa agendar a próxima

Juntando tudo numa classe, a tarefa se reagenda sozinha no final de cada execução. O horário é
recebido no construtor, para a mesma classe servir para qualquer horário:

```java
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
```

E para começar o ciclo, basta agendar a primeira execução:

```java
new TarefaDiaria(scheduler, LocalTime.of(8, 0)).agendarProxima();
```

Repare em três detalhes:

- **`schedule(this, ...)`**: a tarefa agenda a si mesma. Cada execução deixa a próxima marcada.
- **`finally`**: o reagendamento fica no `finally` para acontecer mesmo se a tarefa der erro. Se
  ele ficasse dentro do `try`, uma única falha quebraria a corrente e a tarefa nunca mais rodaria,
  o mesmo problema do passo 3.
- **`isShutdown()`**: depois que o agendador é encerrado (passo 5), ele recusa novos agendamentos
  e lança uma `RejectedExecutionException`. Essa verificação evita o erro.

▶ Rode a classe `Exemplo6HorarioFixo`. Para você não precisar esperar até amanhã, ela passa para a
`TarefaDiaria` um horário 5 segundos à frente do relógio, em vez de `LocalTime.of(8, 0)`. No
console você vê a primeira execução e, logo depois, a próxima marcada para o mesmo horário de
amanhã.

> [!NOTE]
> Usamos `LocalDateTime` para deixar o exemplo simples. Ele não conhece fuso horário, então não
> percebe mudanças como o horário de verão. Em um sistema real, use `ZonedDateTime.now()` no lugar:
> os métodos `with`, `isAfter` e `plusDays` funcionam do mesmo jeito.

O que você acabou de construir é a ideia por trás do **cron**, o agendador clássico de sistemas
Unix, em que você escreve regras como "todo dia às 8h" ou "toda segunda às 9h". Frameworks como o
Spring (com `@Scheduled(cron = "...")`) e o Quartz fazem esse cálculo para você, mas por baixo a
lógica é a mesma: descobrir quando é a próxima vez e esperar até lá.

### 5. Encerre o agendador

As threads do pool não param sozinhas. Se o programa chegar ao fim do `main` sem encerrar o
agendador, elas continuam vivas esperando a próxima tarefa, e o programa nunca termina. Por isso,
quando não precisar mais do agendador, chame `shutdown()`:

```java
scheduler.shutdown();
try {
    if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
        scheduler.shutdownNow();
    }
} catch (InterruptedException e) {
    scheduler.shutdownNow();
    Thread.currentThread().interrupt();
}
```

O código encerra o agendador com educação e, se ele não terminar a tempo, à força:

| Método                                    | O que faz                                                                                                         |
|-------------------------------------------|-------------------------------------------------------------------------------------------------------------------|
| `shutdown()`                              | Para de aceitar novas tarefas e cancela as repetições futuras. Tarefas que estão rodando terminam normalmente, e as de execução única que já estavam agendadas ainda rodam. |
| `awaitTermination(5, TimeUnit.SECONDS)`   | Espera até tudo terminar, por no máximo 5 segundos. Devolve `false` se o tempo acabar antes.                      |
| `shutdownNow()`                           | Interrompe as tarefas em execução e descarta as que ainda não começaram.                                          |

O `Exemplo1Schedule` termina com esse código: o `shutdown()` deixa o e-mail já agendado ser
enviado, o `awaitTermination` espera por ele e o programa termina em 2 segundos. Os outros exemplos
não encerram o agendador, para você acompanhar as repetições pelo tempo que quiser e parar à mão.

A `TarefaDiaria` do passo 4 mostra por que as duas etapas são necessárias. Ela sempre deixa uma
execução única marcada para o dia seguinte, e o `shutdown()` deixa essas execuções rodarem. Com só
o `shutdown()`, o programa ficaria parado esperando até o mesmo horário de amanhã. O
`awaitTermination` espera 5 segundos, percebe que a tarefa não terminou e o `shutdownNow()` cancela
o agendamento.

O `catch` de `InterruptedException` cobre o caso de a própria thread que está esperando ser
interrompida. Nesse caso encerramos tudo à força e devolvemos o aviso de interrupção com
`Thread.currentThread().interrupt()`, para que o resto do programa saiba o que aconteceu.

## Experimente

Mexer no código é o jeito mais rápido de fixar o que você viu. Rode o projeto depois de cada
mudança e observe o console:

1. No `Exemplo4ComparandoRateEDelay`, troque `newScheduledThreadPool(2)` por
   `newScheduledThreadPool(1)`. As duas tarefas passam a disputar uma única thread e começam a se
   atrasar.
2. Ainda no `Exemplo4ComparandoRateEDelay`, aumente a duração da tarefa do `scheduleAtFixedRate`
   de `1000` para `5000` (mais que o intervalo de 3 segundos) e veja as execuções começarem
   coladas umas nas outras.
3. No `Exemplo5ExcecaoNaTarefa`, mova o `throw` da tarefa com `try/catch` para depois do bloco
   `catch`. Ela passa a se comportar como a outra: roda uma vez e some.
4. No `Exemplo6HorarioFixo`, troque o horário por `LocalTime.of(8, 0)` e repare na data da próxima
   execução que aparece no console.
5. No `Exemplo1Schedule`, comente a chamada a `shutdown()` e todo o bloco `try` logo abaixo dela.
   O e-mail continua sendo enviado, mas o programa não termina mais sozinho.
6. Copie o código de encerramento do passo 5 para o final do `Exemplo2FixedRate`. A tarefa nem
   chega a rodar, porque o `shutdown()` cancela as repetições antes da primeira execução. Para ver
   algumas execuções antes do encerramento, faça o `main` esperar antes de encerrar, colocando
   `TimeUnit.SECONDS.sleep(10);` antes do `shutdown()` (e `throws InterruptedException` na
   assinatura do `main`).

## Limitações

O `ScheduledExecutorService` resolve bem o agendamento dentro de uma aplicação, mas vale conhecer
o que ele não faz:

- **Tudo fica na memória.** Se a aplicação reiniciar, os agendamentos somem e precisam ser criados
  de novo na inicialização. Também não há histórico do que rodou ou falhou.
- **Não sabe que existem outras cópias da aplicação.** Se o mesmo sistema rodar em dois servidores,
  a tarefa "das 8h" roda duas vezes, uma em cada servidor.
- **Não entende expressões cron.** Regras como "dias úteis às 8h" ou "primeiro dia do mês" precisam
  ser calculadas à mão, como no passo 4.

Quando essas limitações começarem a pesar, procure ferramentas feitas para isso, como o
`@Scheduled` do Spring ou o Quartz, que guarda os agendamentos em banco de dados.

## Documentação oficial

Se ficar alguma dúvida, a documentação oficial é o melhor lugar para continuar:

- `ScheduledExecutorService`:
  [https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ScheduledExecutorService.html](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ScheduledExecutorService.html)
- `Duration` (`java.time`):
  [https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/Duration.html](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/Duration.html)