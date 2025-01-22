package com.spring.core.entities;

import com.spring.domains.user.User;
import jakarta.persistence.Embedded;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@MappedSuperclass
public abstract class Auditable<U> {

    @Embedded
    protected Audit audit = new Audit();

    @PrePersist
    protected void onCreate() {
        audit.setCreatedOn(LocalDateTime.now());
        audit.setCreatedBy(getCurrentUserId());
    }

    @PreUpdate
    protected void onUpdate() {
        audit.setUpdatedOn(LocalDateTime.now());
        audit.setUpdatedBy(getCurrentUserId());
    }

    protected void updateDeletedFields() {
        audit.setDeletedOn(LocalDateTime.now());
        audit.setDeletedBy(getCurrentUserId());
    }

    public void delete() {
        audit.setDeleted(true);
        audit.setDeletedOn(LocalDateTime.now());
        audit.setDeletedBy(getCurrentUserId());
    }

    private UUID getCurrentUserId() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(authentication -> (User) authentication.getPrincipal())
                .map(User::getId)
                .orElseThrow(() -> new IllegalStateException("No authenticated user found"));
    }
}
