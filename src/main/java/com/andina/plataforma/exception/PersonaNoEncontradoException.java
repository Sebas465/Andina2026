package com.andina.plataforma.exception;

public class PersonaNoEncontradoException extends ResourceNotFoundException {

    public PersonaNoEncontradoException(String message) {
        super(message);
    }

    public static PersonaNoEncontradoException conId(Integer id) {
        return new PersonaNoEncontradoException("Persona no encontrada con ID: " + id);
    }
}