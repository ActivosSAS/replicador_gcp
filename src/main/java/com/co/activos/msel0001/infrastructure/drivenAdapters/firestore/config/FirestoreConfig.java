package com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.config;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cliente Firestore de bajo nivel, usado solo donde se necesita una
 * actualizacion parcial de campo (ej. Users.tags) sin sobreescribir el
 * resto del documento, algo que Spring Data Firestore no garantiza con
 * save(). Usa Application Default Credentials, igual que el resto de la
 * app en Cloud Run.
 */
@Configuration
public class FirestoreConfig {

    @Value("${spring.cloud.gcp.project-id}")
    private String projectId;

    @Bean
    public Firestore firestore() {
        return FirestoreOptions.getDefaultInstance()
                .toBuilder()
                .setProjectId(projectId)
                .build()
                .getService();
    }
}
