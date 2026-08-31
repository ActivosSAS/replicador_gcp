package com.co.activos.msel0001.domain.model.reclutador.tag;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Tag {
    // Id del catalogo CandidateTags (ej. "extrabajador").
    private final String tag;
    // "true"/"false", igual que en el evento de bloqueos (itBlocks/deleteIndicator).
    // true = quitar el tag, false u omitido = agregarlo.
    private final String deleteIndicator;
    // Fecha de retiro (yyyy-MM-dd). Solo aplica a tags con vencimiento automatico
    // (ej. "extrabajador"): dispara la creacion de un TagAssignment con TTL a
    // 12 meses. Si viene null, el tag se agrega sin fecha de expiracion.
    private final String fechaRetiro;
}
