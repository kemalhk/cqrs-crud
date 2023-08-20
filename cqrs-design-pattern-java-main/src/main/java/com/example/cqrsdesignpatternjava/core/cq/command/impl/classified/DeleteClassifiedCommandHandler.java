package com.example.cqrsdesignpatternjava.core.cq.command.impl.classified;

import com.example.cqrsdesignpatternjava.core.cq.command.CommandHandler;
import com.example.cqrsdesignpatternjava.core.cq.command.result.CommandResult;
import com.example.cqrsdesignpatternjava.core.rabbitmq.publisher.RabbitMqPublisher;
import com.example.cqrsdesignpatternjava.entity.Classified;
import com.example.cqrsdesignpatternjava.repository.ClassifiedRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
@AllArgsConstructor

public class DeleteClassifiedCommandHandler implements CommandHandler<DeleteClassifiedCommand> {
    private final ClassifiedRepository classifiedRepository;
    private final RabbitMqPublisher rabbitMqPublisher;

    private final String CLASSIFIED_DELETED_QUEUE = "CLASSIFIED_DELETED_QUEUE";

    @Override
    public CommandResult handle(DeleteClassifiedCommand command) {
        Classified classified = classifiedRepository.findById(command.getClassifiedId())
                .orElse(null);

        if (classified != null) {
            classifiedRepository.delete(classified);

            // Send a deleted event.
            try {
                rabbitMqPublisher.publish(CLASSIFIED_DELETED_QUEUE, String.valueOf(classified.getId()));
            } catch (IOException | TimeoutException e) {
                // Handle the exception if publishing fails.
                return new CommandResult(false, "Failed to publish deletion event.");
            }

            return new CommandResult(true, "Classified deleted successfully.");
        } else {
            return new CommandResult(false, "Classified not found.");
        }
    }
}
