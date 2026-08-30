package com.co.activos.msel0001.domain.usecase;

import com.co.activos.msel0001.domain.model.reclutador.candidateTag.CandidateTag;
import com.co.activos.msel0001.domain.model.reclutador.candidateTag.gateway.CandidateTagGateway;
import com.co.activos.msel0001.domain.model.reclutador.tag.Tag;
import com.co.activos.msel0001.domain.model.reclutador.tag.gateway.TagGateway;
import com.co.activos.msel0001.domain.model.reclutador.user.User;
import com.co.activos.msel0001.domain.model.strategy.ReplicateType;
import com.co.activos.msel0001.domain.model.strategy.StrategyReplication;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TagReplicateUseCase implements StrategyReplication {

    private static final String ACTION_REMOVE = "REMOVE";

    private final Gson gson;
    private final CandidateTagGateway candidateTagGateway;
    private final TagGateway tagGateway;

    public TagReplicateUseCase(Gson gson, CandidateTagGateway candidateTagGateway, TagGateway tagGateway) {
        this.gson = gson;
        this.candidateTagGateway = candidateTagGateway;
        this.tagGateway = tagGateway;
    }

    @Override
    public void replicate(User information) {
        Tag tagEvent = buildTag(information);
        CandidateTag candidateTag = candidateTagGateway.findById(tagEvent.getTagId());

        if (candidateTag == null || !Boolean.TRUE.equals(candidateTag.getActive())) {
            throw new IllegalArgumentException("Tag invalido o inactivo: " + tagEvent.getTagId());
        }

        String userId = information.getId();
        List<String> currentTags = new ArrayList<>(
                Optional.ofNullable(tagGateway.getUserTags(userId)).orElse(new ArrayList<>()));

        if (ACTION_REMOVE.equalsIgnoreCase(tagEvent.getAction())) {
            currentTags.remove(tagEvent.getTagId());
        } else if (!currentTags.contains(tagEvent.getTagId())) {
            currentTags.add(tagEvent.getTagId());
        } else {
            return;
        }

        tagGateway.updateUserTags(userId, currentTags);
    }

    @Override
    public ReplicateType getReplicateType() {
        return ReplicateType.TAG;
    }

    @Override
    public boolean requiresDocumentValidation() {
        return true;
    }

    private Tag buildTag(User information) {
        try {
            return gson.fromJson(information.getInformationToReplicate(), Tag.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON format for Tag: " + information.getInformationToReplicate(), e);
        }
    }
}
