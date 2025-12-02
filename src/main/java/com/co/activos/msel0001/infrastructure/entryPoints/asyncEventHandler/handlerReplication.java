package com.co.activos.msel0001.infrastructure.entryPoints.asyncEventHandler;

import com.co.activos.msel0001.domain.exceptions.ReplicationException;
import com.co.activos.msel0001.domain.usecase.StrategyReplicationUseCase;
import com.co.activos.msel0001.helpers.constants.Errors;
import com.co.activos.msel0001.helpers.event.XMLParser;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class handlerReplication {

    private static final Logger logger = LoggerFactory.getLogger(handlerReplication.class);
    private final StrategyReplicationUseCase replicationUseCase;

    @JmsListener(destination = "sq_replication", containerFactory = "jmsListenerContainerFactory")
    public void onMessage(String message) {
        long startTime = System.currentTimeMillis();
        logger.info("Received message: {} ", message);
        logger.info("Converting message");
        String idEvent = Optional.ofNullable(XMLParser.getIdEventoAsMap(message))
                .orElseThrow(() -> new ReplicationException(Errors.NO_FOUND_EVENT));
        logger.info("Message converted");
        replicationUseCase.replicate(idEvent);
        logger.info("Message processed");
        long endTime = System.currentTimeMillis();
        double durationInSeconds = (endTime - startTime) / 1000.0;
        logger.info("Duration: {} seconds", durationInSeconds);
    }
}
