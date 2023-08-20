package com.example.cqrsdesignpatternjava.core.rabbitmq.updater;

import com.example.cqrsdesignpatternjava.core.elastic.service.ElasticsearchService;
import com.example.cqrsdesignpatternjava.core.rabbitmq.receiver.RabbitMqReceiver;
import com.example.cqrsdesignpatternjava.entity.Classified;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Delivery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Lazy(value = false)
public class ElasticsearchUpdater {

    private final ElasticsearchService elasticsearchService;
    private final ObjectMapper objectMapper;
    private final String CLASSIFIED_INSERTED_QUEUE = "CLASSIFIED_INSERTED_QUEUE";
    private final String CLASSIFIED_DELETED_QUEUE = "CLASSIFIED_DELETED_QUEUE";


    @Autowired
    public ElasticsearchUpdater(RabbitMqReceiver rabbitMqReceiver, ElasticsearchService elasticsearchService, ObjectMapper objectMapper) throws Exception {
        this.elasticsearchService = elasticsearchService;
        this.objectMapper = objectMapper;

        rabbitMqReceiver.receive(CLASSIFIED_INSERTED_QUEUE, this::updateElasticsearchForNewClassified);
        rabbitMqReceiver.receive(CLASSIFIED_DELETED_QUEUE, this::updateElasticsearchForDeletedClassified); // Yeni metod
    }

    private void updateElasticsearchForNewClassified(String consumerTag, Delivery message) throws IOException {
        Classified classified = objectMapper.readValue(message.getBody(), Classified.class);

        this.elasticsearchService.<Classified>insertDocument("classifieds", classified.getId().toString(), classified);
    }

    private void updateElasticsearchForDeletedClassified(String consumerTag, Delivery message) throws IOException {
        Long deletedClassifiedId = Long.parseLong(new String(message.getBody()));

        this.elasticsearchService.deleteDocument("classifieds", deletedClassifiedId.toString());
    }
}
