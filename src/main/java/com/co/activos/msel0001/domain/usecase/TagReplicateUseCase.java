package com.co.activos.msel0001.domain.usecase;

import com.co.activos.msel0001.domain.model.reclutador.candidateTag.CandidateTag;
import com.co.activos.msel0001.domain.model.reclutador.candidateTag.gateway.CandidateTagGateway;
import com.co.activos.msel0001.domain.model.reclutador.tag.Tag;
import com.co.activos.msel0001.domain.model.reclutador.tag.gateway.TagGateway;
import com.co.activos.msel0001.domain.model.reclutador.tagAssignment.TagAssignment;
import com.co.activos.msel0001.domain.model.reclutador.tagAssignment.gateway.TagAssignmentGateway;
import com.co.activos.msel0001.domain.model.reclutador.user.User;
import com.co.activos.msel0001.domain.model.strategy.ReplicateType;
import com.co.activos.msel0001.domain.model.strategy.StrategyReplication;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TagReplicateUseCase implements StrategyReplication {

    private static final String ACTION_REMOVE = "REMOVE";
    private static final int EXPIRATION_MONTHS = 12;

    private final Gson gson;
    private final CandidateTagGateway candidateTagGateway;
    private final TagGateway tagGateway;
    private final TagAssignmentGateway tagAssignmentGateway;

    public TagReplicateUseCase(Gson gson, CandidateTagGateway candidateTagGateway, TagGateway tagGateway,
            TagAssignmentGateway tagAssignmentGateway) {
        this.gson = gson;
        this.candidateTagGateway = candidateTagGateway;
        this.tagGateway = tagGateway;
        this.tagAssignmentGateway = tagAssignmentGateway;
    }

    @Override
    public void replicate(User information) {
        Tag tagEvent = buildTag(information);
        CandidateTag candidateTag = candidateTagGateway.findById(tagEvent.getTagId());

        if (candidateTag == null || !Boolean.TRUE.equals(candidateTag.getActive())) {
            throw new IllegalArgumentException("Tag invalido o inactivo: " + tagEvent.getTagId());
        }

        String userId = information.getId();

        if (ACTION_REMOVE.equalsIgnoreCase(tagEvent.getAction())) {
            removeTag(userId, tagEvent);
        } else {
            addTag(userId, tagEvent);
        }
    }

    @Override
    public ReplicateType getReplicateType() {
        return ReplicateType.TAG;
    }

    @Override
    public boolean requiresDocumentValidation() {
        return true;
    }

    private void addTag(String userId, Tag tagEvent) {
        List<String> currentTags = new ArrayList<>(
                Optional.ofNullable(tagGateway.getUserTags(userId)).orElse(new ArrayList<>()));

        if (!currentTags.contains(tagEvent.getTagId())) {
            currentTags.add(tagEvent.getTagId());
            tagGateway.updateUserTags(userId, currentTags);
        }

        scheduleExpirationIfApplicable(userId, tagEvent);
    }

    private void removeTag(String userId, Tag tagEvent) {
        List<String> currentTags = new ArrayList<>(
                Optional.ofNullable(tagGateway.getUserTags(userId)).orElse(new ArrayList<>()));

        if (currentTags.remove(tagEvent.getTagId())) {
            tagGateway.updateUserTags(userId, currentTags);
        }

        // Idempotente: si no habia un TagAssignment pendiente (tag sin vencimiento,
        // o ya vencido y borrado por TTL) esto es un no-op.
        tagAssignmentGateway.delete(userId, tagEvent.getTagId());
    }

    private void scheduleExpirationIfApplicable(String userId, Tag tagEvent) {
        if (tagEvent.getFechaRetiro() == null || tagEvent.getFechaRetiro().isEmpty()) {
            return;
        }

        LocalDate expiresOn = LocalDate.parse(tagEvent.getFechaRetiro()).plusMonths(EXPIRATION_MONTHS);

        tagAssignmentGateway.save(TagAssignment.builder()
                .userId(userId)
                .tagId(tagEvent.getTagId())
                .expiresAt(expiresOn.atStartOfDay(ZoneOffset.UTC).toInstant())
                .build());
    }

    private Tag buildTag(User information) {
        try {
            return gson.fromJson(information.getInformationToReplicate(), Tag.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON format for Tag: " + information.getInformationToReplicate(), e);
        }
    }
}
