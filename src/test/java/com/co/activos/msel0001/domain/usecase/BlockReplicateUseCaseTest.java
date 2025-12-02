package com.co.activos.msel0001.domain.usecase;

import com.co.activos.msel0001.domain.model.reclutador.block.Block;
import com.co.activos.msel0001.domain.model.reclutador.block.gateway.BlockGateway;
import com.co.activos.msel0001.domain.model.reclutador.user.User;
import com.co.activos.msel0001.domain.model.strategy.ReplicateType;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BlockReplicateUseCaseTest {


    @InjectMocks
    private BlockReplicateUseCase useCase;

    @Mock
    private BlockGateway blockGateway;

    private Gson gson;
    private Block block;
    private User user;


    private static String json = "{\n" +
            "    \"id\": \"id1\",\n" +
            "    \"userId\": \"user1\",\n" +
            "    \"itBlocks\": \"true\",\n" +
            "    \"cause\": \"some cause\",\n" +
            "    \"lock_type\": \"type1\"\n" +
            "}";

    @BeforeEach
    void setUp() {
        gson = new Gson();
        useCase = new BlockReplicateUseCase(gson, blockGateway);
        user = user
                .builder()
                .id("user1")
                .informationToReplicate(json)
                .build();

        block = Block.builder()
                .id("id1")
                .userId("user1")
                .itBlocks(true)
                .cause("some cause")
                .lock_type("type2")
                .build();
    }

    @Test
    void replicate() {
     when(blockGateway.getBlock("user1")).thenReturn(block);

     useCase.replicate(user);
     assertEquals(ReplicateType.BLOCK, useCase.getReplicateType());

     verify(blockGateway,times(1)).saveBlock(any());

    }

    @Test
    void no_save_equal() {
        Block existingBlock = Block.builder()
                .id("id1")
                .userId("user1")
                .itBlocks(true)
                .cause("some cause")
                .lock_type("type1")
                .build();

        when(blockGateway.getBlock("user1")).thenReturn(existingBlock);

        useCase.replicate(user);

        verify(blockGateway, never()).saveBlock(any());
    }

    @Test
    void not_existing_block() {
        when(blockGateway.getBlock("user1")).thenReturn(null);

        useCase.replicate(user);

        verify(blockGateway, times(1)).saveBlock(any());
        verify(blockGateway, times(1)).getBlock(any());
    }

    @Test
    void invalid_json() {
        User invalidInfo = User.builder()
                .id("user1")
                .informationToReplicate("{invalid_json}")
                .build();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> useCase.replicate(invalidInfo));

        var exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> useCase.replicate(invalidInfo));
        assertEquals("Invalid JSON format for Block: {invalid_json}",
                exception.getMessage());
    }

    @Test
    void getReplicateType() {
        ReplicateType replicateType = useCase.getReplicateType();
        assertEquals(ReplicateType.BLOCK, replicateType);
    }
}