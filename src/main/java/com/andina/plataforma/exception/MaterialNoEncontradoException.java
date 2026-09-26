package com.andina.plataforma.exception;

public class MaterialNoEncontradoException extends ResourceNotFoundException {

    public MaterialNoEncontradoException(String message) {
        super(message);
    }

    public static MaterialNoEncontradoException conId(Integer id) {
        return new MaterialNoEncontradoException("Material no encontrado con ID: " + id);
    }
}