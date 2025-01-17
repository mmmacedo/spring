package com.spring.core.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Audit implements Serializable {

    @Serial
    private static final long serialVersionUID = 5834170622327672876L;

    @Column(name = "created_on", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdOn = LocalDateTime.now();

    @Column(name = "created_by", updatable = false)
    private UUID createdBy;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "deleted_on", updatable = false)
    private LocalDateTime deletedOn;

    @Column(name = "deleted_by", updatable = false)
    private UUID deletedBy;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false, updatable = false)
    private boolean isDeleted = false;
}
