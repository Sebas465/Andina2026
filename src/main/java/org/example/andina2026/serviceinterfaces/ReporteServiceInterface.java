package org.example.andina2026.serviceinterfaces;

import org.example.andina2026.dtos.*;

import java.util.List;

public interface ReporteServiceInterface {
    public List<AlumnoRendimientoDTO> alumnosConMenorPromedio(int limite, String lengua, Long idGrado);
    public List<EscuelaInactivaDTO> escuelasInactivas(int dias);
    public List<AlumnoRiesgoDTO> alumnosEnRiesgo();
    public List<RendimientoColegioDTO> rendimientoPorColegio();
    public List<OcupacionAulaDTO> ocupacionDeAulas();
    public List<CargaDocenteDTO> cargaDocente();
    public List<CursoPendienteDTO> cursosSinDocente(Long idPeriodo);
    public List<CursoPendienteDTO> cursosSinMaterial();
    public List<MatriculaColegioDTO> matriculasPorColegioYPeriodo();
}
