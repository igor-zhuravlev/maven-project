package com.epam.learn.ss.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "storages",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_storages_bucket_path",
            columnNames = {"bucket", "path"}
        )
    }
)
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_type", nullable = false, unique = true)
    private StorageType storageType;

    @Column(nullable = false)
    private String bucket;

    @Column(nullable = false)
    private String path;

}
