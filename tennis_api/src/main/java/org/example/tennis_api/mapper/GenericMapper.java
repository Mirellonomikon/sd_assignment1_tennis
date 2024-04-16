package org.example.tennis_api.mapper;

public interface GenericMapper<Entity, DTO> {

    Entity toEntity(DTO dto);
    DTO toDTO(Entity entity);
}
