package uz.broker.datastoregrpcmicroservice.service;

import uz.broker.datastoregrpcmicroservice.model.Data;
import uz.broker.datastoregrpcmicroservice.model.Summary;

import java.util.Set;

public interface SummaryService {


    Summary get(Long sensorId,
                Set<Data.MeasurementType> measurementTypes,
                Set<Summary.SummaryType> summaryTypes
    );

    void handle(
            Data data
    );

}
