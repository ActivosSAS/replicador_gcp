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

    private static final String DELETE_TRUE = "true";
    private static final int EXPIRATION_MONTHS = 12;

    private final Gson gson;
    private final CandidateTagGateway candidateTagGateway;
    private final TagGateway tagGateway;
    private final TagAssignmentGateway tagAssignmentGateway;
    private final TagRemovalService tagRemovalService;

    public TagReplicateUseCase(Gson gson, CandidateTagGateway candidateTagGateway, TagGateway tagGateway,
            TagAssignmentGateway tagAssignmentGateway, TagRemovalService tagRemovalService) {
        this.gson = gson;
        this.candidateTagGateway = candidateTagGateway;
        this.tagGateway = tagGateway;
        this.tagAssignmentGateway = tagAssignmentGateway;
        this.tagRemovalService = tagRemovalService;
    }

    @Override
    public void replicate(User information) {
        Tag tagEvent = buildTag(information);
        CandidateTag candidateTag = candidateTagGateway.findById(tagEvent.getTag());

        if (candidateTag == null || !Boolean.TRUE.equals(candidateTag.getActive())) {
            throw new IllegalArgumentException("Tag invalido o inactivo: " + tagEvent.getTag());
        }

        String userId = information.getId();

        if (DELETE_TRUE.equalsIgnoreCase(tagEvent.getDeleteIndicator())) {
            tagRemovalService.remove(userId, tagEvent.getTag());
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

        if (!currentTags.contains(tagEvent.getTag())) {
            currentTags.add(tagEvent.getTag());
            tagGateway.updateUserTags(userId, currentTags);
        }

        scheduleExpirationIfApplicable(userId, tagEvent);
    }

    private void scheduleExpirationIfApplicable(String userId, Tag tagEvent) {
        if (tagEvent.getFechaRetiro() == null || tagEvent.getFechaRetiro().isEmpty()) {
            return;
        }

        LocalDate expiresOn = LocalDate.parse(tagEvent.getFechaRetiro()).plusMonths(EXPIRATION_MONTHS);

        tagAssignmentGateway.save(TagAssignment.builder()
                .userId(userId)
                .tagId(tagEvent.getTag())
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
