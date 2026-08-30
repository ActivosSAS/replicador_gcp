package com.co.activos.msel0001.domain.model.reclutador.tag;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Tag {
    private final String tagId;
    private final String action;
    // Fecha de retiro (yyyy-MM-dd). Solo aplica a tags con vencimiento automatico
    // (ej. "extrabajador"): dispara la creacion de un TagAssignment con TTL a
    // 12 meses. Si viene null, el tag se agrega sin fecha de expiracion.
    private final String fechaRetiro;
}
