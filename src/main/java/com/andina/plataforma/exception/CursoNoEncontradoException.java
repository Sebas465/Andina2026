package com.andina.plataforma.exception;

import java.util.List;

public class CursoNoEncontradoException extends ResourceNotFoundException {

    public CursoNoEncontradoException(String message) {
        super(message);
    }

    public static CursoNoEncontradoException conId(Integer id) {
        return new CursoNoEncontradoException("Curso no encontrado con ID: " + id);
    }

    public static CursoNoEncontradoException conIds(List<Integer> ids) {
        return new CursoNoEncontradoException("Cursos no encontrados con IDs: " + ids);
    }
}