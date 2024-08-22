package uz.broker.datastoregrpcmicroservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.broker.datastoregrpcmicroservice.exception.SensorNotFoundException;
import uz.broker.datastoregrpcmicroservice.model.Data;
import uz.broker.datastoregrpcmicroservice.model.Summary;
import uz.broker.datastoregrpcmicroservice.repository.SummaryRepo;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SummaryServiceI implements SummaryService{

    private final SummaryRepo summaryRepo;

    @Override
    public Summary get(Long sensorId,
                       Set<Data.MeasurementType> measurementTypes,
                       Set<Summary.SummaryType> summaryTypes) {
        return summaryRepo.findBySensorId(
                sensorId,
                measurementTypes == null ? Set.of(Data.MeasurementType.values()) : measurementTypes,
                summaryTypes == null ? Set.of(Summary.SummaryType.values()) : summaryTypes
        ).orElseThrow(SensorNotFoundException::new);
    }

    @Override
    public void handle(Data data) {
        summaryRepo.handle(data);

    }
}
