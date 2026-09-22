package org.example.andina2026.serviceimplements;

import org.springframework.stereotype.Service;
import org.example.andina2026.dtos.*;
import org.example.andina2026.repositories.IReporteRepository;
import org.example.andina2026.serviceinterfaces.ReporteServiceInterface;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ReporteServiceImplement implements ReporteServiceInterface {
    /** Nota mínima aprobatoria (escala vigesimal). */
    public static final double NOTA_MINIMA = 11.0;

    private final IReporteRepository repository;

    public ReporteServiceImplement(IReporteRepository repository) {
        this.repository = repository;
    }

    // Las consultas nativas devuelven Object[]; los números pueden venir como Integer, Long o BigDecimal
    private static Long lng(Object o) {
        return o == null ? null : ((Number) o).longValue();
    }

    private static Integer integer(Object o) {
        return o == null ? null : ((Number) o).intValue();
    }

    private static BigDecimal dec(Object o) {
        return o == null ? null : new BigDecimal(o.toString());
    }

    private static String str(Object o) {
        return o == null ? null : o.toString();
    }

    @Override
    public List<AlumnoRendimientoDTO> alumnosConMenorPromedio(int limite) {
        return repository.alumnosConMenorPromedio(limite).stream()
                .map(r -> new AlumnoRendimientoDTO(lng(r[0]), str(r[1]), str(r[2]), str(r[3]), str(r[4]), dec(r[5])))
                .toList();
    }

    @Override
    public List<AlumnoRiesgoDTO> alumnosEnRiesgo() {
        return repository.alumnosEnRiesgo(NOTA_MINIMA).stream()
                .map(r -> new AlumnoRiesgoDTO(lng(r[0]), str(r[1]), str(r[2]), str(r[3]), dec(r[4]), true))
                .toList();
    }

    @Override
    public List<RendimientoColegioDTO> rendimientoPorColegio() {
        return repository.rendimientoPorColegio(NOTA_MINIMA).stream()
                .map(r -> new RendimientoColegioDTO(lng(r[0]), str(r[1]), str(r[2]), lng(r[3]), dec(r[4]), lng(r[5]), dec(r[6])))
                .toList();
    }

    @Override
    public List<OcupacionAulaDTO> ocupacionDeAulas() {
        return repository.ocupacionDeAulas().stream()
                .map(r -> new OcupacionAulaDTO(lng(r[0]), str(r[1]), str(r[2]), str(r[3]), integer(r[4]), lng(r[5]), dec(r[6])))
                .toList();
    }

    @Override
    public List<CargaDocenteDTO> cargaDocente() {
        return repository.cargaDocente().stream()
                .map(r -> new CargaDocenteDTO(lng(r[0]), str(r[1]), str(r[2]), str(r[3]), lng(r[4]), dec(r[5])))
                .toList();
    }

    @Override
    public List<CursoPendienteDTO> cursosSinDocente(Long idPeriodo) {
        return repository.cursosSinDocente(idPeriodo).stream()
                .map(r -> new CursoPendienteDTO(lng(r[0]), str(r[1]), str(r[2])))
                .toList();
    }

    @Override
    public List<CursoPendienteDTO> cursosSinMaterial() {
        return repository.cursosSinMaterial().stream()
                .map(r -> new CursoPendienteDTO(lng(r[0]), str(r[1]), str(r[2])))
                .toList();
    }

    @Override
    public List<MatriculaColegioDTO> matriculasPorColegioYPeriodo() {
        return repository.matriculasPorColegioYPeriodo().stream()
                .map(r -> new MatriculaColegioDTO(lng(r[0]), str(r[1]), str(r[2]), lng(r[3])))
                .toList();
    }
}
