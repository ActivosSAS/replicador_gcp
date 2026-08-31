package com.co.activos.msel0001.domain.usecase;

import com.co.activos.msel0001.domain.model.reclutador.tag.gateway.TagGateway;
import com.co.activos.msel0001.domain.model.reclutador.tagAssignment.gateway.TagAssignmentGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagRemovalServiceTest {

    @InjectMocks
    private TagRemovalService service;

    @Mock
    private TagGateway tagGateway;

    @Mock
    private TagAssignmentGateway tagAssignmentGateway;

    @BeforeEach
    void setUp() {
        service = new TagRemovalService(tagGateway, tagAssignmentGateway);
    }

    @Test
    void removes_tag_and_deletes_assignment_when_present() {
        when(tagGateway.getUserTags("user1")).thenReturn(List.of("extrabajador", "referido"));

        service.remove("user1", "extrabajador");

        verify(tagGateway, times(1)).updateUserTags(eq("user1"), eq(List.of("referido")));
        verify(tagAssignmentGateway, times(1)).delete("user1", "extrabajador");
    }

    @Test
    void is_noop_on_users_tags_when_tag_not_present() {
        when(tagGateway.getUserTags("user1")).thenReturn(List.of("referido"));

        service.remove("user1", "extrabajador");

        verify(tagGateway, never()).updateUserTags(anyString(), anyList());
        verify(tagAssignmentGateway, times(1)).delete("user1", "extrabajador");
    }
}
