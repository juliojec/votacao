package br.com.cooperativismo.votacao.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class VotacaoEventPublisher {

    public static final String EXCHANGE = "votacao.exchange";
    public static final String ROUTING_KEY = "votacao.resultado";

    private final RabbitTemplate rabbitTemplate;

    public void publicarResultado(VotacaoResultadoEvent event) {
        log.info("Publicando resultado da sessão {} — SIM: {}, NÃO: {}",
                event.sessaoId(), event.votosSim(), event.votosNao());

        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, event);
    }
}