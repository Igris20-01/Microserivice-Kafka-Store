package uz.broker.datastoregrpcmicroservice.web.mapper;

import org.mapstruct.Mapper;
import uz.broker.datastoregrpcmicroservice.model.Summary;
import uz.broker.datastoregrpcmicroservice.web.dto.SummaryDto;

@Mapper(componentModel = "spring")
public interface SummaryMapper extends Mappable<Summary, SummaryDto> {




}
