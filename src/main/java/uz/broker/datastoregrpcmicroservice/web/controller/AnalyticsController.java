package uz.broker.datastoregrpcmicroservice.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.broker.datastoregrpcmicroservice.model.Data;
import uz.broker.datastoregrpcmicroservice.model.Summary;
import uz.broker.datastoregrpcmicroservice.service.SummaryService;
import uz.broker.datastoregrpcmicroservice.web.dto.SummaryDto;
import uz.broker.datastoregrpcmicroservice.web.mapper.SummaryMapper;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final SummaryService summaryService;

    private final SummaryMapper summaryMapper;

    @GetMapping("/summary/{sensorId}")
    public SummaryDto getSummary(@PathVariable("sensorId") Long sensorId,
                                 @RequestParam(value = "mt", required = false)
                                 Set<Data.MeasurementType> measurementTypes,
                                 @RequestParam(value = "st", required = false)
                                 Set<Summary.SummaryType> summaryTypes
    ) {
        Summary summary = summaryService.get(
                sensorId,
                measurementTypes,
                summaryTypes
        );
        return summaryMapper.toDto(summary);
    }

}
