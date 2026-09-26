package com.andina.plataforma.service;

import com.andina.plataforma.dto.MaterialRequestDTO;
import com.andina.plataforma.dto.MaterialResponseDTO;
import com.andina.plataforma.exception.CursoNoEncontradoException;
import com.andina.plataforma.exception.MaterialNoEncontradoException;
import com.andina.plataforma.exception.PersonaNoEncontradoException;
import com.andina.plataforma.mapper.MaterialMapper;
import com.andina.plataforma.model.entity.Curso;
import com.andina.plataforma.model.entity.Material;
import com.andina.plataforma.model.entity.Persona;
import com.andina.plataforma.model.entity.TipoPersona;
import com.andina.plataforma.repository.CursoRepository;
import com.andina.plataforma.repository.MaterialRepository;
import com.andina.plataforma.repository.PersonaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaterialServiceImplTest {

    @Mock
    private MaterialRepository materialRepository;

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private MaterialMapper materialMapper;

    @InjectMocks
    private MaterialServiceImpl materialService;

    private MaterialRequestDTO requestDTO;
    private MaterialResponseDTO responseDTO;
    private Material materialEntity;
    private Persona personaEntity;
    private Curso cursoEntity;
    private TipoPersona tipoPersona;

    @BeforeEach
    void setUp() {
        tipoPersona = TipoPersona.builder()
                .idTipoPersona(1)
                .detalle("DOCENTE")
                .build();

        personaEntity = Persona.builder()
                .idPersona(1)
                .nombres("Juan")
                .apellidos("Pérez")
                .tipoPersona(tipoPersona)
                .build();

        cursoEntity = Curso.builder()
                .idCurso(1)
                .nombre("Matemáticas")
                .build();

        materialEntity = Material.builder()
                .idMaterial(1)
                .persona(personaEntity)
                .titulo("Guía de Matemáticas")
                .descripcion("Material de apoyo")
                .tipo("GUÍA")
                .urlArchivo("https://example.com/material.pdf")
                .fechaPublicacion(LocalDate.of(2026, 3, 1))
                .cursos(new HashSet<>(Set.of(cursoEntity)))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        requestDTO = MaterialRequestDTO.builder()
                .idPersona(1)
                .titulo("Guía de Matemáticas")
                .descripcion("Material de apoyo")
                .tipo("GUÍA")
                .urlArchivo("https://example.com/material.pdf")
                .fechaPublicacion(LocalDate.of(2026, 3, 1))
                .idsCursos(List.of(1))
                .build();

        responseDTO = MaterialResponseDTO.builder()
                .idMaterial(1)
                .idPersona(1)
                .nombrePersona("Juan Pérez")
                .titulo("Guía de Matemáticas")
                .descripcion("Material de apoyo")
                .tipo("GUÍA")
                .urlArchivo("https://example.com/material.pdf")
                .fechaPublicacion(LocalDate.of(2026, 3, 1))
                .cursos(List.of(
                        com.andina.plataforma.dto.CursoResumenDTO.builder()
                                .idCurso(1)
                                .nombre("Matemáticas")
                                .build()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void crear_deberiaGuardarYRetornarResponseDTO_cuandoExitosoConCursos() {
        when(personaRepository.findById(1)).thenReturn(Optional.of(personaEntity));
        when(cursoRepository.findAllById(List.of(1))).thenReturn(List.of(cursoEntity));
        when(materialMapper.toEntity(any())).thenReturn(Material.builder().build());
        when(materialRepository.save(any())).thenAnswer(invocation -> {
            Material m = invocation.getArgument(0);
            m.setIdMaterial(1);
            return m;
        });
        when(materialMapper.toResponseDTO(any())).thenReturn(responseDTO);

        MaterialResponseDTO resultado = materialService.crear(requestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdMaterial()).isEqualTo(1);
        verify(personaRepository).findById(1);
        verify(cursoRepository).findAllById(List.of(1));
        verify(materialRepository).save(any());
        verify(materialMapper).toResponseDTO(any());
    }

    @Test
    void crear_deberiaGuardarYRetornarResponseDTO_cuandoExitosoSinCursos() {
        MaterialRequestDTO dtoSinCursos = MaterialRequestDTO.builder()
                .idPersona(1)
                .titulo("Material sin cursos")
                .descripcion("Descripción")
                .build();

        when(personaRepository.findById(1)).thenReturn(Optional.of(personaEntity));
        when(materialMapper.toEntity(any())).thenReturn(Material.builder().build());
        when(materialRepository.save(any())).thenAnswer(invocation -> {
            Material m = invocation.getArgument(0);
            m.setIdMaterial(1);
            return m;
        });
        when(materialMapper.toResponseDTO(any())).thenReturn(responseDTO);

        MaterialResponseDTO resultado = materialService.crear(dtoSinCursos);

        assertThat(resultado).isNotNull();
        verify(personaRepository).findById(1);
        verify(cursoRepository, never()).findAllById(any());
        verify(materialRepository).save(any());
    }

    @Test
    void crear_deberiaLanzarExcepcion_cuandoPersonaNoExiste() {
        when(personaRepository.findById(99)).thenReturn(Optional.empty());

        MaterialRequestDTO dto = MaterialRequestDTO.builder()
                .idPersona(99)
                .titulo("Test")
                .build();

        assertThatThrownBy(() -> materialService.crear(dto))
                .isInstanceOf(PersonaNoEncontradoException.class)
                .hasMessageContaining("99");

        verify(personaRepository).findById(99);
        verify(materialRepository, never()).save(any());
    }

    @Test
    void crear_deberiaLanzarExcepcion_cuandoAlgunCursoNoExiste() {
        when(personaRepository.findById(1)).thenReturn(Optional.of(personaEntity));
        when(cursoRepository.findAllById(List.of(1, 2, 3))).thenReturn(List.of(
                Curso.builder().idCurso(1).build(),
                Curso.builder().idCurso(3).build()
        ));

        MaterialRequestDTO dto = MaterialRequestDTO.builder()
                .idPersona(1)
                .titulo("Test")
                .idsCursos(List.of(1, 2, 3))
                .build();

        assertThatThrownBy(() -> materialService.crear(dto))
                .isInstanceOf(CursoNoEncontradoException.class)
                .hasMessageContaining("2");

        verify(cursoRepository).findAllById(List.of(1, 2, 3));
        verify(materialRepository, never()).save(any());
    }

    @Test
    void obtenerPorId_deberiaRetornarResponseDTO_cuandoExiste() {
        when(materialRepository.findById(1)).thenReturn(Optional.of(materialEntity));
        when(materialMapper.toResponseDTO(any())).thenReturn(responseDTO);

        MaterialResponseDTO resultado = materialService.obtenerPorId(1);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdMaterial()).isEqualTo(1);
        verify(materialRepository).findById(1);
    }

    @Test
    void obtenerPorId_deberiaLanzarExcepcion_cuandoNoExiste() {
        when(materialRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> materialService.obtenerPorId(99))
                .isInstanceOf(MaterialNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void obtenerTodos_deberiaRetornarLista_cuandoHayResultados() {
        when(materialRepository.findAll()).thenReturn(List.of(
                Material.builder().idMaterial(1).build(),
                Material.builder().idMaterial(2).build()
        ));
        when(materialMapper.toResponseDTO(any()))
                .thenReturn(
                        MaterialResponseDTO.builder().idMaterial(1).titulo("Test 1").build(),
                        MaterialResponseDTO.builder().idMaterial(2).titulo("Test 2").build()
                );

        List<MaterialResponseDTO> resultado = materialService.obtenerTodos();

        assertThat(resultado).hasSize(2);
        verify(materialRepository).findAll();
        verify(materialMapper, times(2)).toResponseDTO(any());
    }

    @Test
    void obtenerTodos_deberiaRetornarListaVacia_cuandoNoHayResultados() {
        when(materialRepository.findAll()).thenReturn(Collections.emptyList());

        List<MaterialResponseDTO> resultado = materialService.obtenerTodos();

        assertThat(resultado).isEmpty();
        verify(materialMapper, never()).toResponseDTO(any());
    }

    @Test
    void obtenerPorPersona_deberiaRetornarLista_cuandoHayResultados() {
        when(materialRepository.findByPersonaIdPersona(1)).thenReturn(List.of(materialEntity));
        when(materialMapper.toResponseDTO(any())).thenReturn(responseDTO);

        List<MaterialResponseDTO> resultado = materialService.obtenerPorPersona(1);

        assertThat(resultado).hasSize(1);
        verify(materialRepository).findByPersonaIdPersona(1);
    }

    @Test
    void obtenerPorPersona_deberiaRetornarListaVacia_cuandoNoHayResultados() {
        when(materialRepository.findByPersonaIdPersona(1)).thenReturn(Collections.emptyList());

        List<MaterialResponseDTO> resultado = materialService.obtenerPorPersona(1);

        assertThat(resultado).isEmpty();
        verify(materialRepository).findByPersonaIdPersona(1);
        verify(materialMapper, never()).toResponseDTO(any());
    }

    @Test
    void actualizar_deberiaReemplazarCursos_cuandoIdsCursosEnDTO() {
        MaterialRequestDTO updateDTO = MaterialRequestDTO.builder()
                .idPersona(1)
                .titulo("Actualizado")
                .idsCursos(List.of(2))
                .build();

        Curso curso2 = Curso.builder().idCurso(2).nombre("Ciencias").build();

        when(materialRepository.findById(1)).thenReturn(Optional.of(materialEntity));
        when(personaRepository.findById(1)).thenReturn(Optional.of(personaEntity));
        when(cursoRepository.findAllById(List.of(2))).thenReturn(List.of(curso2));
        when(materialRepository.save(any())).thenReturn(materialEntity);
        when(materialMapper.toResponseDTO(any())).thenReturn(responseDTO);

        MaterialResponseDTO resultado = materialService.actualizar(1, updateDTO);

        assertThat(resultado).isNotNull();
        verify(cursoRepository).findAllById(List.of(2));
        verify(materialRepository).save(any());
    }

    @Test
    void actualizar_deberiaLanzarExcepcion_cuandoMaterialNoExiste() {
        when(materialRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> materialService.actualizar(99, requestDTO))
                .isInstanceOf(MaterialNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void actualizar_deberiaLanzarExcepcion_cuandoPersonaNoExiste() {
        when(materialRepository.findById(1)).thenReturn(Optional.of(materialEntity));
        when(personaRepository.findById(99)).thenReturn(Optional.empty());

        MaterialRequestDTO dto = MaterialRequestDTO.builder()
                .idPersona(99)
                .titulo("Test")
                .build();

        assertThatThrownBy(() -> materialService.actualizar(1, dto))
                .isInstanceOf(PersonaNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void actualizar_deberiaLanzarExcepcion_cuandoCursoNoExiste() {
        when(materialRepository.findById(1)).thenReturn(Optional.of(materialEntity));
        when(personaRepository.findById(1)).thenReturn(Optional.of(personaEntity));
        when(cursoRepository.findAllById(List.of(99))).thenReturn(Collections.emptyList());

        MaterialRequestDTO dto = MaterialRequestDTO.builder()
                .idPersona(1)
                .idsCursos(List.of(99))
                .build();

        assertThatThrownBy(() -> materialService.actualizar(1, dto))
                .isInstanceOf(CursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void eliminar_deberiaEliminar_cuandoExiste() {
        when(materialRepository.existsById(1)).thenReturn(true);

        materialService.eliminar(1);

        verify(materialRepository).existsById(1);
        verify(materialRepository).deleteById(1);
    }

    @Test
    void eliminar_deberiaLanzarExcepcion_cuandoNoExiste() {
        when(materialRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> materialService.eliminar(99))
                .isInstanceOf(MaterialNoEncontradoException.class)
                .hasMessageContaining("99");

        verify(materialRepository).existsById(99);
        verify(materialRepository, never()).deleteById(anyInt());
    }

    @Test
    void agregarCursoAMaterial_deberiaAgregarCurso_cuandoExitoso() {
        Curso cursoNuevo = Curso.builder().idCurso(2).nombre("Historia").build();
        Material materialConCurso = Material.builder().idMaterial(1).persona(personaEntity).cursos(Set.of(cursoEntity, cursoNuevo)).build();

        when(materialRepository.findById(1)).thenReturn(Optional.of(materialEntity));
        when(cursoRepository.findById(2)).thenReturn(Optional.of(cursoNuevo));
        when(materialRepository.save(any())).thenReturn(materialConCurso);
        when(materialMapper.toResponseDTO(any())).thenReturn(responseDTO);

        MaterialResponseDTO resultado = materialService.agregarCursoAMaterial(1, 2);

        assertThat(resultado).isNotNull();
        verify(cursoRepository).findById(2);
        verify(materialRepository).save(any());
    }

    @Test
    void agregarCursoAMaterial_deberiaLanzarExcepcion_cuandoMaterialNoExiste() {
        when(materialRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> materialService.agregarCursoAMaterial(99, 1))
                .isInstanceOf(MaterialNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void agregarCursoAMaterial_deberiaLanzarExcepcion_cuandoCursoNoExiste() {
        when(materialRepository.findById(1)).thenReturn(Optional.of(materialEntity));
        when(cursoRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> materialService.agregarCursoAMaterial(1, 99))
                .isInstanceOf(CursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void agregarCursoAMaterial_noDuplicaCurso_cuandoYaAsociado() {
        when(materialRepository.findById(1)).thenReturn(Optional.of(materialEntity));
        when(cursoRepository.findById(1)).thenReturn(Optional.of(cursoEntity));
        when(materialRepository.save(any())).thenReturn(materialEntity);
        when(materialMapper.toResponseDTO(any())).thenReturn(responseDTO);

        MaterialResponseDTO resultado = materialService.agregarCursoAMaterial(1, 1);

        assertThat(resultado).isNotNull();
        verify(cursoRepository).findById(1);
        verify(materialRepository).save(any());
    }

    @Test
    void quitarCursoDeMaterial_deberiaQuitarCurso_cuandoExitoso() {
        Material materialSinCurso = Material.builder().idMaterial(1).persona(personaEntity).cursos(Set.of()).build();

        when(materialRepository.findById(1)).thenReturn(Optional.of(materialEntity));
        when(cursoRepository.findById(1)).thenReturn(Optional.of(cursoEntity));
        when(materialRepository.save(any())).thenReturn(materialSinCurso);
        when(materialMapper.toResponseDTO(any())).thenReturn(responseDTO);

        MaterialResponseDTO resultado = materialService.quitarCursoDeMaterial(1, 1);

        assertThat(resultado).isNotNull();
        verify(cursoRepository).findById(1);
        verify(materialRepository).save(any());
    }

    @Test
    void quitarCursoDeMaterial_deberiaLanzarExcepcion_cuandoMaterialNoExiste() {
        when(materialRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> materialService.quitarCursoDeMaterial(99, 1))
                .isInstanceOf(MaterialNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void quitarCursoDeMaterial_deberiaLanzarExcepcion_cuandoCursoNoExiste() {
        when(materialRepository.findById(1)).thenReturn(Optional.of(materialEntity));
        when(cursoRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> materialService.quitarCursoDeMaterial(1, 99))
                .isInstanceOf(CursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void quitarCursoDeMaterial_noLanzaError_cuandoCursoNoAsociado() {
        Material materialSinCurso = Material.builder().idMaterial(1).persona(personaEntity).cursos(Set.of()).build();

        when(materialRepository.findById(1)).thenReturn(Optional.of(materialEntity));
        when(cursoRepository.findById(99)).thenReturn(Optional.of(Curso.builder().idCurso(99).build()));
        when(materialRepository.save(any())).thenReturn(materialSinCurso);
        when(materialMapper.toResponseDTO(any())).thenReturn(responseDTO);

        MaterialResponseDTO resultado = materialService.quitarCursoDeMaterial(1, 99);

        assertThat(resultado).isNotNull();
        verify(cursoRepository).findById(99);
        verify(materialRepository).save(any());
    }
}