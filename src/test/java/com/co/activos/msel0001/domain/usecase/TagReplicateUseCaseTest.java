package com.co.activos.msel0001.domain.usecase;

import com.co.activos.msel0001.domain.model.reclutador.candidateTag.CandidateTag;
import com.co.activos.msel0001.domain.model.reclutador.candidateTag.gateway.CandidateTagGateway;
import com.co.activos.msel0001.domain.model.reclutador.tagAssignment.TagAssignment;
import com.co.activos.msel0001.domain.model.reclutador.tagAssignment.gateway.TagAssignmentGateway;
import com.co.activos.msel0001.domain.model.reclutador.tag.gateway.TagGateway;
import com.co.activos.msel0001.domain.model.reclutador.user.User;
import com.co.activos.msel0001.domain.model.strategy.ReplicateType;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagReplicateUseCaseTest {

    @InjectMocks
    private TagReplicateUseCase useCase;

    @Mock
    private CandidateTagGateway candidateTagGateway;

    @Mock
    private TagGateway tagGateway;

    @Mock
    private TagAssignmentGateway tagAssignmentGateway;

    private Gson gson;
    private User user;

    private static final String addJson = "{\n" +
            "    \"tagId\": \"extrabajador\",\n" +
            "    \"action\": \"ADD\"\n" +
            "}";

    private static final String removeJson = "{\n" +
            "    \"tagId\": \"extrabajador\",\n" +
            "    \"action\": \"REMOVE\"\n" +
            "}";

    private static final String addWithFechaRetiroJson = "{\n" +
            "    \"tagId\": \"extrabajador\",\n" +
            "    \"action\": \"ADD\",\n" +
            "    \"fechaRetiro\": \"2025-08-30\"\n" +
            "}";

    @BeforeEach
    void setUp() {
        gson = new Gson();
        useCase = new TagReplicateUseCase(gson, candidateTagGateway, tagGateway, tagAssignmentGateway);
        user = User.builder()
                .id("user1")
                .informationToReplicate(addJson)
                .build();
    }

    @Test
    void adds_tag_when_valid_and_not_present() {
        when(candidateTagGateway.findById("extrabajador"))
                .thenReturn(CandidateTag.builder().id("extrabajador").name("Extrabajador").active(true).build());
        when(tagGateway.getUserTags("user1")).thenReturn(Collections.emptyList());

        useCase.replicate(user);

        verify(tagGateway, times(1)).updateUserTags(eq("user1"), eq(List.of("extrabajador")));
        verify(tagAssignmentGateway, never()).save(any());
    }

    @Test
    void does_not_duplicate_tag_when_already_present() {
        when(candidateTagGateway.findById("extrabajador"))
                .thenReturn(CandidateTag.builder().id("extrabajador").name("Extrabajador").active(true).build());
        when(tagGateway.getUserTags("user1")).thenReturn(List.of("extrabajador"));

        useCase.replicate(user);

        verify(tagGateway, never()).updateUserTags(anyString(), anyList());
    }

    @Test
    void removes_tag_when_action_is_remove() {
        User removeUser = User.builder().id("user1").informationToReplicate(removeJson).build();

        when(candidateTagGateway.findById("extrabajador"))
                .thenReturn(CandidateTag.builder().id("extrabajador").name("Extrabajador").active(true).build());
        when(tagGateway.getUserTags("user1")).thenReturn(List.of("extrabajador", "referido"));

        useCase.replicate(removeUser);

        verify(tagGateway, times(1)).updateUserTags(eq("user1"), eq(List.of("referido")));
        verify(tagAssignmentGateway, times(1)).delete("user1", "extrabajador");
    }

    @Test
    void rejects_tag_not_found_in_catalog() {
        when(candidateTagGateway.findById("extrabajador")).thenReturn(null);

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> useCase.replicate(user));

        assertEquals("Tag invalido o inactivo: extrabajador", exception.getMessage());
        verify(tagGateway, never()).updateUserTags(anyString(), anyList());
    }

    @Test
    void rejects_inactive_tag() {
        when(candidateTagGateway.findById("extrabajador"))
                .thenReturn(CandidateTag.builder().id("extrabajador").name("Extrabajador").active(false).build());

        Assertions.assertThrows(IllegalArgumentException.class, () -> useCase.replicate(user));

        verify(tagGateway, never()).updateUserTags(anyString(), anyList());
    }

    @Test
    void invalid_json() {
        User invalidInfo = User.builder().id("user1").informationToReplicate("{invalid_json}").build();

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> useCase.replicate(invalidInfo));

        assertEquals("Invalid JSON format for Tag: {invalid_json}", exception.getMessage());
    }

    @Test
    void getReplicateType() {
        assertEquals(ReplicateType.TAG, useCase.getReplicateType());
    }

    @Test
    void requiresDocumentValidation_is_true() {
        Assertions.assertTrue(useCase.requiresDocumentValidation());
    }

    @Test
    void creates_expiring_assignment_when_fecha_retiro_present() {
        User userWithFecha = User.builder().id("user1").informationToReplicate(addWithFechaRetiroJson).build();

        when(candidateTagGateway.findById("extrabajador"))
                .thenReturn(CandidateTag.builder().id("extrabajador").name("Extrabajador").active(true).build());
        when(tagGateway.getUserTags("user1")).thenReturn(Collections.emptyList());

        useCase.replicate(userWithFecha);

        ArgumentCaptor<TagAssignment> captor = ArgumentCaptor.forClass(TagAssignment.class);
        verify(tagAssignmentGateway, times(1)).save(captor.capture());

        TagAssignment saved = captor.getValue();
        assertEquals("user1", saved.getUserId());
        assertEquals("extrabajador", saved.getTagId());
        assertEquals(LocalDate.parse("2026-08-30").atStartOfDay(ZoneOffset.UTC).toInstant(), saved.getExpiresAt());
    }

    @Test
    void refreshes_assignment_on_resend_even_when_tag_already_present() {
        User userWithFecha = User.builder().id("user1").informationToReplicate(addWithFechaRetiroJson).build();

        when(candidateTagGateway.findById("extrabajador"))
                .thenReturn(CandidateTag.builder().id("extrabajador").name("Extrabajador").active(true).build());
        when(tagGateway.getUserTags("user1")).thenReturn(List.of("extrabajador"));

        useCase.replicate(userWithFecha);

        verify(tagGateway, never()).updateUserTags(anyString(), anyList());
        verify(tagAssignmentGateway, times(1)).save(any());
    }
}
