package org.example.andina2026.configs;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mm = new ModelMapper();
        // STRICT: solo copia campos con el mismo nombre. Las relaciones (idColegio, idPersona…)
        // las resuelve cada controller buscando la entidad, así no se crean objetos a medias.
        mm.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return mm;
    }
}
