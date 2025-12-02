package com.co.activos.msel0001.domain.usecase;

import com.co.activos.msel0001.domain.exceptions.ReplicationException;
import com.co.activos.msel0001.domain.model.activos.detailReplication.DetailReplication;
import com.co.activos.msel0001.domain.model.activos.detailReplication.gateway.DetailReplicationGateway;
import com.co.activos.msel0001.domain.model.activos.detailReplication.util.State;
import com.co.activos.msel0001.domain.model.reclutador.user.User;
import com.co.activos.msel0001.domain.model.reclutador.user.gateway.UserGateway;
import com.co.activos.msel0001.domain.model.strategy.ReplicateType;
import com.co.activos.msel0001.domain.model.strategy.StrategyReplication;
import com.co.activos.msel0001.helpers.constants.Errors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.co.activos.msel0001.helpers.constants.Errors.NO_STRATEGY_FOUND;


@Service
public class StrategyReplicationUseCase {

    private static final Logger logger = LoggerFactory.getLogger(StrategyReplicationUseCase.class);

    private final UserGateway userGateway;
    private final DetailReplicationGateway detailReplicationGateway;
    private final Map<ReplicateType, StrategyReplication> replicateTypeHashMap;

    public StrategyReplicationUseCase(UserGateway userGateway,
                                      DetailReplicationGateway detailReplicationGateway,
                                      List<StrategyReplication> strategyReplications) {
        this.userGateway = userGateway;
        this.detailReplicationGateway = detailReplicationGateway;
        this.replicateTypeHashMap = strategyReplications.stream()
                .collect(Collectors.toMap(
                        StrategyReplication::getReplicateType,
                        Function.identity()
                ));
    }

    public void replicate(String event) {
        AtomicReference<DetailReplication> detailRef = new AtomicReference<>();

        try {
            // 1. Buscar DetailReplication
            DetailReplication detail = Optional.ofNullable(detailReplicationGateway.findByIdEvent(event))
                    .orElseThrow(() -> new ReplicationException(Errors.NO_DETAIL_REPLICATION_FOUND + event));
            detailRef.set(detail);

            // 2. Validar ID Config
            if (detail.getIdConfig() == null) {
                throw new ReplicationException(Errors.NO_ID_CONFIG);
            }

            // 3. Obtener tipo de replicación y estrategia
            ReplicateType replicateType = Arrays.stream(ReplicateType.values())
                    .filter(type -> detail.getIdConfig().equalsIgnoreCase(type.getValue()))
                    .findFirst()
                    .orElseThrow(() -> new ReplicationException(NO_STRATEGY_FOUND + detail.getIdConfig()));

            StrategyReplication strategy = replicateTypeHashMap.get(replicateType);
            if (strategy == null) {
                throw new ReplicationException(NO_STRATEGY_FOUND + detail.getIdConfig());
            }

            // 4. Preparar información del usuario según sea necesario por la estrategia
            User info = prepareUserInformation(detail, strategy);

            // 5. Ejecutar validación y replicación
            logger.info("Iniciando replicación para evento: {}, estrategia: {}", event, replicateType);
            strategyValidate(strategy, info, replicateType);

            // 6. Actualizar estado exitoso
            detailReplicationGateway.updateStatus(detail.toBuilder()
                    .state(State.PROCESSED)
                    .description("Replicación exitosa")
                    .build());

            logger.info("Replicación exitosa - Evento: {}, Estrategia: {}", event, replicateType);
            
        } catch (ReplicationException e) {
            handleReplicationError(detailRef.get(), e);
            logger.error("Error en el proceso de replicación", e);
        } catch (Exception e) {
            handleReplicationError(detailRef.get(), new ReplicationException("Error inesperado durante la replicación: " + e.getMessage(), e));
           logger.error("Error en el proceso de replicación", e);
        }
    }

    private User prepareUserInformation(DetailReplication detail, StrategyReplication strategy) {
        // Si la estrategia no requiere validación de documento, retornar un User básico
        if (!strategy.requiresDocumentValidation()) {
            return User.builder()
                    .informationToReplicate(detail.getInformationToReplicate())
                    .build();
        }

        // Validar que exista la información de documento requerida
        if (detail.getDocumentType() == null || detail.getDocumentNumber() == null) {
            throw new ReplicationException(Errors.NO_BASIC_INFORMATION_FOUND +
                    detail.getDocumentType() + Errors.AND_DOCUMENT_NUMBER + detail.getDocumentNumber());
        }

        // Obtener y retornar la información del usuario
        return Optional.ofNullable(userGateway.getUser(detail.getDocumentType(), detail.getDocumentNumber()))
                .map(user -> user.toBuilder()
                        .informationToReplicate(detail.getInformationToReplicate())
                        .build())
                .orElseThrow(() -> new ReplicationException(Errors.NO_BASIC_INFORMATION_FOUND +
                        detail.getDocumentType() + Errors.AND_DOCUMENT_NUMBER + detail.getDocumentNumber()));
    }

    private void handleReplicationError(DetailReplication detail, Exception e) {
        if (detail != null) {
            try {
                detailReplicationGateway.updateStatus(detail.toBuilder()
                        .state(State.ERROR)
                        .description(e.getMessage())
                        .build());
                logger.error("Error en el proceso de replicación para el detalle: {}. Error: {}", detail, e.getMessage(), e);
            } catch (Exception ex) {
                logger.error("Error actualizando el estado de la replicación", ex);
            }
        } else {
            logger.error("Error en el proceso de replicación (sin detalle disponible)", e);
        }
    }

    private static void strategyValidate(StrategyReplication strategy, User info, ReplicateType replicateType) {
        try {
            strategy.replicate(info);
        } catch (Exception e) {
            throw new ReplicationException("Error replicating with strategy [" +
                    replicateType.name() + "]: " + e.getMessage());
        }
    }

    private void updateStatusIfDetailPresent(DetailReplication detail, String description) {
        if (detail != null) {
            detailReplicationGateway.updateStatus(detail.toBuilder()
                    .state(State.ERROR)
                    .description(description)
                    .build());
        }
    }
}


