//
//package com.co.activos.msel0001.domain.usecase;
//import com.co.activos.msel0001.domain.model.reclutador.agreement.Agreement;
//import com.co.activos.msel0001.domain.model.reclutador.agreement.gateway.AgreementGateway;
//import com.co.activos.msel0001.domain.model.reclutador.user.User;
//import com.google.gson.Gson;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//
//import java.util.UUID;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AgreementReplicateUseCaseTest {
//
//    private Gson gson;
//    @Mock
//    private AgreementGateway agreementGateway;
//
//
//    @InjectMocks
//    private AgreementReplicateUseCase useCase;
//
//    private User user;
//
//    private    Agreement existingAgreement;
//
//
//    private static String json = "{\n" +
//            "    \"id\": \"id1\",\n" +
//            "    \"agreementId\": \"a3\",\n" +
//            "    \"status\": \"ACTIVE\",\n" +
//            "    \"userId\": \"user3\"\n" +
//            "}";
//
//    @BeforeEach
//    void setUp() {
//        gson = new Gson();
//        useCase = new AgreementReplicateUseCase(gson, agreementGateway);
//        user = User.builder()
//                .id(UUID.randomUUID().toString())
//                .informationToReplicate(json)
//                .build();
//
//        existingAgreement= Agreement.builder()
//                .id("id1")
//                .agreementId("a3")
//                .status("ACTIVE")
//                .userId(user.getId())
//                .build();
//    }
//
//
//    @Test
//    @DisplayName("replicate should save new agreement when userId is new")
//    void replicate_shouldSaveNewAgreement_whenUserIdIsNew() {
//
//        when(agreementGateway.findByUserId(user.getId())).thenReturn(existingAgreement);
//
//        useCase.replicate(user);
//
//        verify(agreementGateway, never()).saveAgreement(any());
//    }
//
//
//    @Test
//    @DisplayName("replicate should update existing agreement when userId exists")
//    void replicate_shouldUpdateExistingAgreement_whenUserIdExists() {
//        User info = mock(User.class);
//        when(info.getId()).thenReturn("user1");
//        when(info.getInformationToReplicate()).thenReturn(json);
//
//        Agreement existingAgreement = Agreement.builder()
//                .id("id1")
//                .agreementId("a1")
//                .status("INACTIVE")
//                .userId("user1")
//                .build();
//
//        when(agreementGateway.findByUserId("user1")).thenReturn(existingAgreement);
//
//        useCase.replicate(info);
//
//        Agreement expectedUpdatedAgreement = Agreement.builder()
//                .id("id1")
//                .agreementId("a3")
//                .status("ACTIVE")
//                .userId("user1")
//                .build();
//
//        verify(agreementGateway).saveAgreement(expectedUpdatedAgreement);
//    }
//
//
//
//
//}