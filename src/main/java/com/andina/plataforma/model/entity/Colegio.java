package com.andina.plataforma.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "colegio")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Colegio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_colegio")
    @EqualsAndHashCode.Include
    private Integer idColegio;

    @Column(name = "nombre", length = 200, nullable = false)
    private String nombre;

    @Column(name = "codigo_modular", length = 7, unique = true)
    private String codigoModular;

    @Column(name = "departamento", length = 100, nullable = false)
    private String departamento;

    @Column(name = "provincia", length = 100, nullable = false)
    private String provincia;

    @Column(name = "distrito", length = 100, nullable = false)
    private String distrito;

    @Column(name = "comunidad", length = 150)
    private String comunidad;

    @Column(name = "tipo_zona", length = 30)
    private String tipoZona;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}