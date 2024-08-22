package uz.broker.datastoregrpcmicroservice.repository;


import uz.broker.datastoregrpcmicroservice.model.Data;
import uz.broker.datastoregrpcmicroservice.model.Summary;

import java.util.Optional;
import java.util.Set;

public interface SummaryRepo {

    Optional<Summary> findBySensorId(
            Long sensorId,
            Set<Data.MeasurementType> measurementTypes,
            Set<Summary.SummaryType> summaryTypes
    );

    void handle(Data data);

}
