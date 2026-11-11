# Agendamento de tarefas em Java

Imagine que você precise gerar um relatório todos os dias às 6h, verificar pedidos pendentes a cada
5 minutos ou apagar arquivos temporários de tempos em tempos.

Em vez de executar essas tarefas manualmente, podemos programar nossa aplicação para cuidar disso
sozinha!

Neste tutorial, vamos aprender a agendar tarefas em Java puro, sem utilizar frameworks. Para isso,
usaremos o `ScheduledExecutorService`, que já faz parte do JDK.

Vamos começar com uma tarefa simples, executada uma única vez, e adicionar novos recursos aos
poucos. Ao longo do caminho, você vai aprender a repetir tarefas, definir horários específicos e
tratar erros.

## Antes de começar

1. Clone o repositório.
2. Abra o projeto no IntelliJ ou na IDE de sua preferência.

Cada exemplo possui sua própria classe com um método `main`. Assim, você pode executar cada etapa
separadamente e observar o comportamento no console.

Os exemplos estão organizados em ordem numérica, de `Exemplo01AEncerramentoManual` até
`Exemplo07HorarioFixo`. 

> [!IMPORTANT]
> A partir do passo 3, as tarefas começam a se repetir e o programa não termina sozinho. Isso é
> esperado: o agendador continua funcionando enquanto a aplicação estiver ligada.
>
> Quando terminar de observar um exemplo, interrompa a execução:
>
> - **IntelliJ:** clique no botão **Stop**.
> - **Terminal:** pressione `Ctrl+C`.

## O que é agendar uma tarefa?

Agendar uma tarefa significa programar um trecho de código para ser executado automaticamente em
determinado momento. Ela pode rodar uma única vez, depois de algum tempo, ou se repetir em
intervalos regulares.

Pense no alarme do celular: você pode configurá-lo para tocar daqui a 10 minutos ou para despertar
todos os dias no mesmo horário. Depois de configurado, não precisa fazer mais nada. O
`ScheduledExecutorService` funciona de maneira parecida dentro de um programa Java.

## Passo a passo

### 1. Sua primeira tarefa agendada

#### 1.1. Crie o agendador

Para executar uma tarefa no momento programado, precisamos de um agendador. Ele organiza as tarefas
e utiliza uma ou mais **threads** para executá-las.

Uma thread é uma linha de execução. Em outras palavras, permite que uma parte do programa trabalhe
enquanto outras partes continuam executando.

A classe `Executors` oferece métodos prontos para criar um agendador. Vamos começar com o mais
simples, que utiliza uma única thread:

```java
ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
```

Uma thread será suficiente para todos os exemplos do passo a passo. Na seção
[Extra](#extra), vamos observar o que acontece quando precisamos executar tarefas
diferentes ao mesmo tempo.

#### 1.2. Agende a tarefa

A tarefa que queremos executar pode ser representada por um `Runnable`. Essa interface possui um
método chamado `run()`, que contém o código da tarefa.

Para tarefas pequenas, podemos escrever esse código diretamente usando uma **lambda**:

```java
scheduler.schedule(() -> {
    String mensagem = "Enviando e-mail de boas-vindas (thread: %s)"
            .formatted(Thread.currentThread().getName());
    System.out.println(mensagem);
}, 2, TimeUnit.SECONDS);
```

O trecho `() -> { ... }` representa a tarefa. O código entre as chaves é o que seria escrito dentro
do método `run()`.

E os outros argumentos?

| Argumento          | Significado                                                                    |
|--------------------|--------------------------------------------------------------------------------|
| `2`                | Quanto tempo esperar antes de executar a tarefa.                               |
| `TimeUnit.SECONDS` | Unidade de tempo utilizada. Também existem opções como `MINUTES` e `HOURS`.    |

O método `schedule()` agenda uma execução única. Neste exemplo, o envio do e-mail acontece uma vez,
dois segundos depois do agendamento.

Uma observação importante: chamar `schedule()` não faz o programa esperar. O método apenas registra
a tarefa e devolve o controle imediatamente. Por isso, o `main` continua executando enquanto o
agendador aguarda o momento certo.

**Execute** a classe `Exemplo01AEncerramentoManual`. O console deve mostrar algo parecido com isto:

```text
Fim do main.
Enviando e-mail de boas-vindas (thread: pool-1-thread-1)
```

Repare na ordem das mensagens: o `main` termina antes de o e-mail ser enviado. Mesmo assim, o
programa continua aberto.

#### 1.3. Por que o programa não terminou?

Em Java, o programa só termina quando todas as threads acabam, e não apenas o `main`. A thread
criada pelo agendador continua ativa, aguardando novas tarefas, até que alguém avise que ela não é
mais necessária.

#### 1.4. Encerre o agendador

Para avisar que não queremos mais utilizar o agendador, chamamos `shutdown()`.

No final da classe `Exemplo01AEncerramentoManual`, você encontrará uma linha comentada.
Descomente-a:

```java
scheduler.shutdown();
```

Agora execute a classe novamente. O e-mail será enviado e, em seguida, o programa terminará.

O `shutdown()` impede novos agendamentos, mas deixa terminar as tarefas únicas que já estavam
programadas. Por isso, o e-mail ainda é enviado.

#### 1.5. Uma alternativa: `try-with-resources`

Também podemos utilizar o agendador com `try-with-resources`:

```java
try (ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()) {
    scheduler.schedule(() -> {
        String mensagem = "Enviando e-mail de boas-vindas (thread: %s)"
                .formatted(Thread.currentThread().getName());
        System.out.println(mensagem);
    }, 2, TimeUnit.SECONDS);
}
System.out.println("Fim do main.");
```

Ao sair do bloco `try`, o Java chama `close()` automaticamente. Esse método faz o `shutdown()` e
espera as tarefas terminarem.

**Execute** a classe `Exemplo01BTryWithResources`. Repare na ordem: agora, a mensagem
`Fim do main.` aparece depois do envio do e-mail, porque o `main` fica aguardando o agendador
terminar ao sair do bloco `try`.

Use essa abordagem quando o trabalho do agendador tem começo e fim: por exemplo, uma tarefa única,
um conjunto pequeno de tarefas ou um processamento em lote.

> [!WARNING]
> Não utilize `try-with-resources` quando o agendador precisa permanecer ativo durante toda a
> execução da aplicação. Ao sair do bloco, o `close()` encerra o agendador e as tarefas periódicas
> deixam de continuar.
>
> Nos próximos exemplos, vamos trabalhar com tarefas que precisam se repetir. Por isso, o agendador
> ficará ativo até o programa ser interrompido.

---

### 2. A mesma tarefa escrita como classe

A lambda é uma forma prática de escrever tarefas pequenas. Mas ela não é a única opção: também
podemos criar uma classe que implementa `Runnable`.

No projeto, a classe `TarefaAgendada` faz isso. Ela recebe um nome pelo construtor e utiliza o
método `run()` para imprimir o horário, o nome da tarefa e a thread responsável pela execução.

```java
public class TarefaAgendada implements Runnable {

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final String nome;

    public TarefaAgendada(String nome) {
        this.nome = nome;
    }

    @Override
    public void run() {
        String mensagem = "[%s] %s (%s)".formatted(
                LocalTime.now().format(FORMATO_HORA),
                nome,
                Thread.currentThread().getName()
        );
        System.out.println(mensagem);
    }
}
```

Para o agendador, não faz diferença se a tarefa foi criada com lambda ou com uma classe. Nos dois
casos, ele recebe um `Runnable`. Basta passar um objeto da classe no lugar da lambda:

```java
scheduler.schedule(
        new TarefaAgendada("Envio de e-mail de boas-vindas (feito com classe)"),
        2,
        TimeUnit.SECONDS
);
```

Qual forma escolher?

| Forma      | Quando preferir                                                                                          |
|------------|----------------------------------------------------------------------------------------------------------|
| **Lambda** | Quando a tarefa é pequena e utilizada em um único lugar.                                                 |
| **Classe** | Quando a tarefa tem mais responsabilidades, precisa guardar informações ou será reutilizada em diferentes agendamentos. |

Nos próximos passos, você verá as duas abordagens.

**Execute** a classe `Exemplo02ClasseTarefa`. A tarefa será executada depois de 2 segundos e o
programa terminará sozinho, porque o agendador foi criado com `try-with-resources`.

---

### 3. Repetindo tarefas com `scheduleAtFixedRate()`

Até aqui, cada tarefa foi executada uma única vez. E se quisermos repetir uma tarefa
automaticamente?

Uma das opções é utilizar `scheduleAtFixedRate()`:

```java
scheduler.scheduleAtFixedRate(
        new TarefaAgendada("Verificação de pedidos pendentes"),
        1,
        3,
        TimeUnit.SECONDS
);
```

Os parâmetros indicam:

| Parâmetro          | Significado                                                         |
|--------------------|---------------------------------------------------------------------|
| `1`                | Tempo de espera antes da primeira execução.                         |
| `3`                | Intervalo entre o **início** de uma execução e o início da próxima. |
| `TimeUnit.SECONDS` | Unidade de tempo dos dois valores.                                  |

Imagine um ônibus que sai do terminal a cada 15 minutos. O horário de saída segue uma programação
fixa. É essa a ideia do `scheduleAtFixedRate()`: as execuções são planejadas com base em um
intervalo contado entre seus horários de início.

Diferentemente dos exemplos anteriores, aqui precisamos deixar o agendador ativo. Se o encerrarmos
logo depois de agendar a tarefa, as próximas execuções não continuarão.

**Execute** a classe `Exemplo03FixedRate` e observe o console. A tarefa será executada
repetidamente, com intervalo de 3 segundos entre os inícios, enquanto o programa estiver ligado.

> [!NOTE]
> Uma mesma tarefa periódica não executa duas vezes ao mesmo tempo. Se uma execução demorar mais
> que o intervalo programado, a próxima começará assim que a anterior terminar. Ela poderá ficar
> atrasada em relação ao horário planejado.

---

### 4. Repetindo tarefas com `scheduleWithFixedDelay()`

Existe outra maneira de repetir uma tarefa: aguardar um intervalo depois que cada execução
terminar.

Para isso, utilizamos `scheduleWithFixedDelay()`:

```java
scheduler.scheduleWithFixedDelay(
        new TarefaAgendada("Geração de relatório de vendas"),
        1,
        4,
        TimeUnit.SECONDS
);
```

Os parâmetros são os mesmos do método anterior. A diferença está na forma de contar o intervalo:

| Método                     | Como conta o intervalo                                           |
|----------------------------|------------------------------------------------------------------|
| `scheduleAtFixedRate()`    | Do **início** de uma execução até o início da próxima.           |
| `scheduleWithFixedDelay()` | Do **fim** de uma execução até o início da próxima.              |

Pense em uma série de exercícios na academia: você termina uma série e descansa por um minuto antes
de começar a próxima. Não importa quanto tempo levou para concluir a série; o descanso começa depois
que ela termina.

Esse método é útil quando queremos garantir uma pausa entre as execuções, independentemente da
duração da tarefa.

**Execute** a classe `Exemplo04FixedDelay`. Como a tarefa do exemplo termina rapidamente, os
horários podem parecer iguais aos de um `scheduleAtFixedRate()` com intervalo de 4 segundos. A
diferença fica mais evidente quando a tarefa demora, como veremos a seguir.

---

### 5. Comparando os dois métodos

Vamos imaginar uma tarefa que demora 1 segundo para terminar e que foi programada com intervalo de
3 segundos nos dois métodos.

```text
Segundos                0   1   2   3   4   5   6   7   8   9
scheduleAtFixedRate     [===]       [===]       [===]       [===]
scheduleWithFixedDelay  [===]           [===]           [===]
```

Com `scheduleAtFixedRate()`, as execuções começam nos segundos 0, 3, 6 e 9.

Com `scheduleWithFixedDelay()`, cada execução termina, aguarda 3 segundos e só então começa
novamente. Por isso, os inícios acontecem nos segundos 0, 4 e 8.

O exemplo `Exemplo05ComparandoRateEDelay` utiliza a classe `TarefaDemorada`, que usa
`Thread.sleep()` para simular uma tarefa que demora 1 segundo.

**Execute** a classe `Exemplo05ComparandoRateEDelay` e compare os horários de início no console com
o diagrama.

---

### 6. Trate as exceções dentro da tarefa

Existe um detalhe importante quando trabalhamos com tarefas periódicas: se uma execução lançar uma
exceção e ela não for tratada, as próximas execuções daquela tarefa serão canceladas.

Isso pode ser difícil de perceber. Imagine que uma tarefa consulta o banco de dados a cada minuto.
Se o banco ficar indisponível por alguns instantes e a tarefa lançar uma exceção, ela poderá parar
de ser executada. Quando o banco voltar, a tarefa não será retomada automaticamente.

Para evitar esse comportamento, trate as exceções dentro da própria tarefa:

```java
scheduler.scheduleAtFixedRate(() -> {
    try {
        // lógica da tarefa
    } catch (RuntimeException e) {
        System.err.println("Erro tratado dentro da tarefa: " + e.getMessage());
    }
}, 1, 3, TimeUnit.SECONDS);
```

Assim, se uma execução falhar, o erro será apresentado no console e a tarefa poderá ser executada
novamente no próximo intervalo.

**Execute** a classe `Exemplo06Excecao`. Ela agenda duas tarefas que falham: uma sem `try/catch` e
outra com tratamento de exceção.

Observe a diferença:

- A primeira executa uma vez e não volta a ser executada.
- A segunda apresenta a mensagem de erro e continua tentando executar a tarefa.

> [!WARNING]
> Em tarefas periódicas, pense em como os erros serão tratados. Uma exceção não tratada pode
> interromper definitivamente as próximas execuções daquela tarefa.

---

### 7. Agendando uma tarefa em um horário fixo

Até agora, trabalhamos com tempos relativos: executar daqui a 2 segundos ou repetir a cada 3
segundos.

Mas e se quisermos executar uma tarefa todos os dias às 8h?

O `ScheduledExecutorService` não possui um método específico para agendar uma execução diária em um
horário do relógio. Porém, podemos montar esse comportamento utilizando `schedule()` e as classes
de data e hora do Java.

#### Por que não repetir a cada 24 horas?

Uma alternativa que pode parecer simples é calcular quanto falta até as 8h e depois utilizar
`scheduleAtFixedRate()` com um intervalo de 24 horas.

O problema é que um intervalo de 24 horas não significa necessariamente executar todos os dias às
8h. Atrasos e mudanças de horário podem fazer com que a execução se afaste do horário desejado.

Uma solução mais adequada é calcular novamente o próximo horário depois de cada execução.

A lógica tem três etapas:

1. Calcular quanto falta até a próxima ocorrência do horário desejado.
2. Agendar uma execução única com `schedule()`.
3. Ao terminar a tarefa, calcular e agendar a próxima ocorrência.

É como programar o despertador novamente a cada dia: quando o alarme toca, você já deixa o próximo
marcado.

#### 7.1. Calcule quanto falta até as 8h

Vamos começar calculando a próxima execução:

```java
LocalDateTime agora = LocalDateTime.now();
LocalDateTime proximaExecucao = agora.with(LocalTime.of(8, 0));

if (!proximaExecucao.isAfter(agora)) {
    proximaExecucao = proximaExecucao.plusDays(1);
}

Duration espera = Duration.between(agora, proximaExecucao);
```

Entenda o que acontece:

| Trecho                           | O que faz                                                              |
|----------------------------------|------------------------------------------------------------------------|
| `agora.with(LocalTime.of(8, 0))` | Mantém a data atual e define o horário como 8h.                        |
| `plusDays(1)`                    | Se esse horário já passou, agenda a execução para o dia seguinte.      |
| `Duration.between()`             | Calcula o tempo entre o momento atual e a próxima execução.            |

Por exemplo, se agora são 22h, a próxima execução será às 8h do dia seguinte. Portanto, a espera
será de 10 horas.

#### 7.2. Agende uma única execução

O método `schedule()` recebe um número e uma unidade de tempo, não um objeto `Duration`.

Por isso, podemos converter o tempo calculado para milissegundos utilizando `toMillis()`:

```java
scheduler.schedule(tarefa, espera.toMillis(), TimeUnit.MILLISECONDS);
```

#### 7.3. Faça a tarefa agendar a próxima execução

Agora vamos juntar as etapas em uma classe.

Nesse caso, uma classe é mais conveniente do que uma lambda, pois a tarefa precisa guardar o
agendador e o horário desejado. Além disso, ela precisa conseguir agendar uma nova execução de si
mesma.

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

Para iniciar o ciclo, basta criar a tarefa e agendar a primeira execução:

```java
new TarefaDiaria(scheduler, LocalTime.of(8, 0)).agendarProxima();
```

**Execute** a classe `Exemplo07HorarioFixo`. Para não precisar esperar até o dia seguinte, o
exemplo informa à `TarefaDiaria` um horário alguns segundos à frente do momento atual.

No console, você verá a primeira execução e, em seguida, o horário calculado para a próxima
ocorrência, no dia seguinte.

#### 7.4. Alguns detalhes importantes

| Detalhe                            | Por que importa                                                                                                                                                                                                   |
|------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `schedule(this, ...)`              | A própria tarefa agenda sua próxima execução. Assim, cada execução deixa a seguinte programada.                                                                                                                  |
| Reagendamento no `finally`         | Dessa forma, ele acontece mesmo quando a lógica da tarefa lança uma exceção que foi tratada.                                                                                                                     |
| `isShutdown()`                     | Verifica se o agendador já iniciou seu encerramento. Isso evita tentar agendar uma nova execução depois que ele deixou de aceitar tarefas.                                                                       |
| Fuso horário                       | Usamos `LocalDateTime` para manter o exemplo simples. Em aplicações que precisam respeitar um fuso horário específico, prefira trabalhar com `ZonedDateTime` e definir explicitamente a região, como `ZoneId.of("America/Sao_Paulo")`. |

---

## Experimente!

Agora que você conhece os principais recursos do `ScheduledExecutorService`, que tal alterar alguns
exemplos e observar o que acontece?

Faça os testes abaixo. Depois de cada mudança, execute o projeto e acompanhe o console.

1. No `Exemplo03FixedRate`, crie o agendador com `try-with-resources` e coloque o
   `scheduleAtFixedRate()` dentro do bloco. Observe o que acontece quando o bloco termina e o
   agendador é encerrado.
2. No `Exemplo05ComparandoRateEDelay`, agende as duas tarefas no `schedulerRate` e remova o
   `schedulerDelay`. Observe como uma tarefa interfere nos horários da outra.
3. Ainda no `Exemplo05ComparandoRateEDelay`, aumente a duração da tarefa com
   `scheduleAtFixedRate()` de `1000` para `5000` milissegundos. Compare os horários com o diagrama
   do passo 5.
4. No `Exemplo06Excecao`, mova o `throw` da tarefa que possui `try/catch` para depois do bloco
   `catch`. Observe o que acontece com as próximas execuções.
5. No `Exemplo07HorarioFixo`, troque o horário utilizado por `LocalTime.of(8, 0)` e confira a data e
   o horário da próxima execução no console.

## Limitações

O `ScheduledExecutorService` é útil para agendar tarefas dentro de uma aplicação, mas existem
algumas limitações que precisamos conhecer:

| Limitação                              | O que significa                                                                                                                                                         |
|----------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Os agendamentos ficam na memória**   | Se a aplicação for encerrada ou reiniciada, os agendamentos não são preservados. Eles precisam ser criados novamente quando a aplicação iniciar.                       |
| **Não coordena diferentes instâncias** | Se a mesma aplicação estiver rodando em dois servidores, cada instância terá seu próprio agendador. Uma tarefa programada para as 8h poderá ser executada nos dois servidores. |
| **Não oferece expressões cron**        | Regras como "dias úteis às 8h" ou "no primeiro dia do mês" precisam ser implementadas manualmente.                                                                     |

Quando essas limitações se tornarem um problema, vale conhecer ferramentas mais completas, como o
`@Scheduled` do Spring ou o Quartz. Elas oferecem recursos adicionais para trabalhar com
agendamentos.

## Extra

### Quando uma thread não é suficiente?

No passo a passo, todos os agendadores utilizaram uma única thread. Isso funcionou porque as
tarefas eram rápidas ou porque não havia outras tarefas concorrendo pela mesma thread.

Mas uma thread só executa uma tarefa por vez. Se uma tarefa demorar, as outras tarefas do mesmo
agendador precisarão esperar.

Os exemplos `08A` e `08B` mostram esse comportamento com duas tarefas:

- Uma verificação de pedidos, rápida, que deveria ser executada a cada 1 segundo.
- Um relatório mais pesado, que começa depois de 2 segundos e demora 4 segundos.

O código de agendamento é o mesmo nos dois exemplos:

```java
scheduler.scheduleAtFixedRate(
        new TarefaAgendada("Verificação de pedidos"),
        0,
        1,
        TimeUnit.SECONDS
);

scheduler.schedule(
        new TarefaDemorada("Relatório pesado", 4000),
        2,
        TimeUnit.SECONDS
);
```

A diferença entre os exemplos está na quantidade de threads disponíveis no agendador.

#### Utilizando uma thread

**Execute** a classe `Exemplo08AUmaThread`. O console deve apresentar algo parecido com isto:

```text
[18:06:38] Verificação de pedidos (pool-1-thread-1)
[18:06:39] Verificação de pedidos (pool-1-thread-1)
[18:06:39] Relatório pesado: início (pool-1-thread-1)
[18:06:43] Relatório pesado: fim (pool-1-thread-1)
[18:06:43] Verificação de pedidos (pool-1-thread-1)
[18:06:43] Verificação de pedidos (pool-1-thread-1)
[18:06:43] Verificação de pedidos (pool-1-thread-1)
[18:06:43] Verificação de pedidos (pool-1-thread-1)
[18:06:44] Verificação de pedidos (pool-1-thread-1)
```

Enquanto o relatório ocupa a única thread disponível, a verificação de pedidos não consegue
executar.

Quando o relatório termina, as execuções periódicas que ficaram atrasadas podem acontecer em
sequência, até que o agendador alcance novamente o ritmo programado.

Em um sistema real, isso pode significar verificações importantes ficando atrasadas porque outra
tarefa está ocupando a única thread disponível.

#### Utilizando duas threads

Para permitir que mais de uma tarefa seja executada ao mesmo tempo, podemos criar um agendador com
um pool de threads.

Em vez de `newSingleThreadScheduledExecutor()`, utilizamos `newScheduledThreadPool()`, informando a
quantidade de threads:

```java
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
```

Esse conjunto de threads disponíveis é chamado de **pool de threads**. Nesse exemplo, duas tarefas
podem executar ao mesmo tempo. Se todas as threads estiverem ocupadas, as tarefas seguintes
precisarão esperar.

**Execute** a classe `Exemplo08BDuasThreads`:

```text
[18:06:47] Verificação de pedidos (pool-1-thread-1)
[18:06:48] Verificação de pedidos (pool-1-thread-1)
[18:06:48] Relatório pesado: início (pool-1-thread-2)
[18:06:49] Verificação de pedidos (pool-1-thread-1)
[18:06:50] Verificação de pedidos (pool-1-thread-1)
[18:06:51] Verificação de pedidos (pool-1-thread-1)
[18:06:52] Verificação de pedidos (pool-1-thread-1)
[18:06:52] Relatório pesado: fim (pool-1-thread-2)
[18:06:53] Verificação de pedidos (pool-1-thread-1)
```

Agora, o relatório é executado na `pool-1-thread-2`, enquanto a verificação de pedidos continua na
`pool-1-thread-1`.

Como há duas threads disponíveis, uma tarefa não precisa esperar a outra terminar.

#### Quantas threads utilizar?

Não existe um número ideal para todos os cenários. A quantidade depende da duração das tarefas, do
tipo de trabalho e de quantas execuções podem acontecer ao mesmo tempo.

Como ponto de partida:

| Quantidade                | Quando usar                                                                                                         |
|---------------------------|---------------------------------------------------------------------------------------------------------------------|
| **Uma thread**            | É suficiente quando as tarefas são rápidas ou quando não há problema em uma esperar pela outra.                    |
| **Mais de uma thread**    | Pode ser útil quando tarefas demoradas não devem bloquear outras tarefas.                                           |
| **Cuidado com o excesso** | Cada thread consome recursos. Além disso, tarefas executadas ao mesmo tempo podem acessar os mesmos dados, exigindo atenção à concorrência. |

> [!TIP]
> No `Exemplo08AUmaThread`, aumente a duração do relatório de `4000` para `7000` milissegundos e
> observe quantas verificações ficam atrasadas enquanto o relatório está em execução.

## Documentação oficial

Se quiser consultar os detalhes de cada recurso, estas são as documentações oficiais:

- [`ScheduledExecutorService`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ScheduledExecutorService.html)
- [`Executors`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Executors.html)
- [`Duration`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/Duration.html)
