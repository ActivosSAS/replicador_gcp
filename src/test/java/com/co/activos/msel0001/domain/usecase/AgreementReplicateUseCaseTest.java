package com.co.activos.msel0001.domain.usecase;

import com.co.activos.msel0001.domain.model.reclutador.agreement.Agreement;
import com.co.activos.msel0001.domain.model.reclutador.agreement.gateway.AgreementGateway;
import com.co.activos.msel0001.domain.model.reclutador.user.User;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgreementReplicateUseCaseTest {

    @InjectMocks
    private AgreementReplicateUseCase useCase;

    @Mock
    private AgreementGateway agreementGateway;

    @Mock
    private TagRemovalService tagRemovalService;

    private Gson gson;

    private static final String activeJson = "{\n" +
            "    \"id\": \"id1\",\n" +
            "    \"agreementId\": \"a1\",\n" +
            "    \"status\": \"ACTIVO\"\n" +
            "}";

    private static final String activeShortJson = "{\n" +
            "    \"id\": \"id1\",\n" +
            "    \"agreementId\": \"a1\",\n" +
            "    \"status\": \"ACT\"\n" +
            "}";

    private static final String inactiveJson = "{\n" +
            "    \"id\": \"id1\",\n" +
            "    \"agreementId\": \"a1\",\n" +
            "    \"status\": \"INACTIVO\"\n" +
            "}";

    @BeforeEach
    void setUp() {
        gson = new Gson();
        useCase = new AgreementReplicateUseCase(gson, agreementGateway, tagRemovalService);
    }

    @Test
    @DisplayName("guarda el contrato nuevo cuando no existia")
    void saves_new_agreement_when_none_existed() {
        User info = User.builder().id("user1").informationToReplicate(inactiveJson).build();
        when(agreementGateway.findByUserIdAndAgreementId("user1", "a1")).thenReturn(null);

        useCase.replicate(info);

        verify(agreementGateway, times(1)).saveAgreement(any());
    }

    @Test
    @DisplayName("no vuelve a guardar ni toca el tag si el estado no cambio")
    void does_nothing_when_status_unchanged() {
        User info = User.builder().id("user1").informationToReplicate(activeJson).build();
        Agreement existing = Agreement.builder().id("id1").agreementId("a1").status("ACTIVO").userId("user1").build();
        when(agreementGateway.findByUserIdAndAgreementId("user1", "a1")).thenReturn(existing);

        useCase.replicate(info);

        verify(agreementGateway, never()).saveAgreement(any());
        verifyNoInteractions(tagRemovalService);
    }

    @Test
    @DisplayName("contrato activo (ACTIVO) dispara la remocion del tag Extrabajador")
    void removes_extrabajador_tag_when_status_becomes_active() {
        User info = User.builder().id("user1").informationToReplicate(activeJson).build();
        Agreement existing = Agreement.builder().id("id1").agreementId("a1").status("INACTIVO").userId("user1").build();
        when(agreementGateway.findByUserIdAndAgreementId("user1", "a1")).thenReturn(existing);

        useCase.replicate(info);

        verify(agreementGateway, times(1)).saveAgreement(any());
        verify(tagRemovalService, times(1)).remove("user1", "extrabajador");
    }

    @Test
    @DisplayName("tambien reconoce el codigo corto ACT como activo")
    void removes_extrabajador_tag_for_short_active_code() {
        User info = User.builder().id("user1").informationToReplicate(activeShortJson).build();
        Agreement existing = Agreement.builder().id("id1").agreementId("a1").status("PRE").userId("user1").build();
        when(agreementGateway.findByUserIdAndAgreementId("user1", "a1")).thenReturn(existing);

        useCase.replicate(info);

        verify(tagRemovalService, times(1)).remove("user1", "extrabajador");
    }

    @Test
    @DisplayName("contrato que pasa a inactivo no toca el tag")
    void does_not_touch_tag_when_status_becomes_inactive() {
        User info = User.builder().id("user1").informationToReplicate(inactiveJson).build();
        Agreement existing = Agreement.builder().id("id1").agreementId("a1").status("ACTIVO").userId("user1").build();
        when(agreementGateway.findByUserIdAndAgreementId("user1", "a1")).thenReturn(existing);

        useCase.replicate(info);

        verify(agreementGateway, times(1)).saveAgreement(any());
        verifyNoInteractions(tagRemovalService);
    }
}
