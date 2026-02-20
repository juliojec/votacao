package br.com.cooperativismo.votacao.messaging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VotacaoEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private VotacaoEventPublisher eventPublisher;

    @Test
    void publicarResultado_DeveEnviarEventoParaRabbit() {
        UUID sessaoId = UUID.randomUUID();
        UUID pautaId = UUID.randomUUID();
        VotacaoResultadoEvent evento = new VotacaoResultadoEvent(
                sessaoId,
                pautaId,
                "Pauta Assembléia",
                10,
                5,
                "APROVADA"
        );

        eventPublisher.publicarResultado(evento);

        ArgumentCaptor<VotacaoResultadoEvent> captor = ArgumentCaptor.forClass(VotacaoResultadoEvent.class);
        verify(rabbitTemplate, times(1))
                .convertAndSend(eq(VotacaoEventPublisher.EXCHANGE),
                        eq(VotacaoEventPublisher.ROUTING_KEY),
                        captor.capture());

        VotacaoResultadoEvent enviado = captor.getValue();
        assertEquals(evento.sessaoId(), enviado.sessaoId());
        assertEquals(evento.pautaId(), enviado.pautaId());
        assertEquals(evento.pautaTitulo(), enviado.pautaTitulo());
        assertEquals(evento.votosSim(), enviado.votosSim());
        assertEquals(evento.votosNao(), enviado.votosNao());
        assertEquals(evento.resultado(), enviado.resultado());
    }
}
