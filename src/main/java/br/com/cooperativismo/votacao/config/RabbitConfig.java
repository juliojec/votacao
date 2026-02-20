package br.com.cooperativismo.votacao.config;

import br.com.cooperativismo.votacao.messaging.VotacaoEventPublisher;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange votacaoExchange() {
        return new TopicExchange(VotacaoEventPublisher.EXCHANGE);
    }

    @Bean
    public Queue resultadoQueue() {
        return QueueBuilder.durable(VotacaoEventPublisher.ROUTING_KEY).build();
    }

    @Bean
    public Binding resultadoBinding(Queue resultadoQueue, TopicExchange votacaoExchange) {
        return BindingBuilder.bind(resultadoQueue).to(votacaoExchange).with(VotacaoEventPublisher.ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}