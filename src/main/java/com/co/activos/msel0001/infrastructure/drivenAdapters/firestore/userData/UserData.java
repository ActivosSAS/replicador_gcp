package com.co.activos.msel0001.infrastructure.drivenAdapters.firestore.userData;

import com.google.cloud.Timestamp;
import com.google.cloud.spring.data.firestore.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Document(collectionName = "Users")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserData implements Serializable {
    private String id;
    private String documentType;
    private String email;
    private String userType;
    private String role;
    private String idNumber;
    private String lastName;
    private String ip;
    private boolean agreements;
    private Timestamp updateAt;
    private Timestamp createdAt;
    private String password;
    private boolean completeDocuments;
    private boolean activate;
    private String name;
    private String confirmToken;
}
