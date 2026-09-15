package pe.josueyovera.itanes.data.mapper;

import java.util.ArrayList;
import java.util.List;

import pe.josueyovera.itanes.data.local.entity.PlaceEntity;
import pe.josueyovera.itanes.data.remote.dto.PlaceRemoteDto;

public class PlaceMapper {

    public static PlaceEntity toEntity(PlaceRemoteDto dto) {
        if (dto == null) return null;
        
        return new PlaceEntity(
                dto.getId(),
                dto.getName(),
                dto.getShortDescription(),
                dto.getDescription(),
                dto.getAddress(),
                dto.getLatitude(),
                dto.getLongitude(),
                dto.getImageUrl(),
                dto.getOrderNumber(),
                dto.getUpdatedAt()
        );
    }

    public static List<PlaceEntity> toEntityList(List<PlaceRemoteDto> dtos) {
        List<PlaceEntity> entities = new ArrayList<>();
        if (dtos != null) {
            for (PlaceRemoteDto dto : dtos) {
                entities.add(toEntity(dto));
            }
        }
        return entities;
    }
}
