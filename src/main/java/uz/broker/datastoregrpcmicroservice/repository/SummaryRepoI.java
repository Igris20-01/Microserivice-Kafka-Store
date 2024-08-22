package uz.broker.datastoregrpcmicroservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import uz.broker.datastoregrpcmicroservice.config.RedisSchema;
import uz.broker.datastoregrpcmicroservice.model.Data;
import uz.broker.datastoregrpcmicroservice.model.Summary;

import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class SummaryRepoI implements SummaryRepo{

    private final JedisPool jedisPool;

    @Override
    public Optional<Summary> findBySensorId(
            Long sensorId,
            Set<Data.MeasurementType> measurementTypes,
            Set<Summary.SummaryType> summaryTypes
    ) {
        try(Jedis jedis = jedisPool.getResource()){
            if (!jedis.sismember(
                    RedisSchema.sensorKey(),
                    String.valueOf(sensorId)
            )){
                return Optional.empty();
            }
            if (measurementTypes.isEmpty() && !summaryTypes.isEmpty()){
                return getSummary(
                        sensorId,
                        Set.of(Data.MeasurementType.values()),
                        summaryTypes,
                        jedis
                );
            } else if (!measurementTypes.isEmpty() && summaryTypes.isEmpty()){
                return getSummary(
                        sensorId,
                        measurementTypes,
                        Set.of(Summary.SummaryType.values()),
                        jedis
                );
            }else {
                return getSummary(
                        sensorId,
                        measurementTypes,
                        summaryTypes,
                        jedis
                );
            }
        }

    }

    private Optional<Summary> getSummary(
            Long sensorId,
            Set<Data.MeasurementType> measurementTypes,
            Set<Summary.SummaryType> summaryTypes,
            Jedis jedis
    ){
        Summary summary = new Summary();
        summary.setSensorId(sensorId);
        for (Data.MeasurementType mType : measurementTypes){
            for (Summary.SummaryType sType : summaryTypes){
                Summary.SummaryEntry entry = new Summary.SummaryEntry();
                entry.setType(sType);
                String value = jedis.hget(
                        RedisSchema.summaryKey(sensorId, mType),
                        sType.name().toLowerCase()
                );
                if (value != null){
                    entry.setValue(Double.parseDouble(value));
                }
                String counter = jedis.hget(
                        RedisSchema.summaryKey(sensorId, mType),
                        "counter"
                );
                if (counter != null){
                    entry.setCounter(Long.parseLong(counter));
                }
                summary.addValue(mType, entry);
            }
        }

        return Optional.of(summary);
    }

    @Override
    public void handle(Data data) {
        try(Jedis jedis = jedisPool.getResource()){
            if (!jedis.sismember(
                    RedisSchema.sensorKey(),
                    String.valueOf(data.getSensorId())
            )){
                jedis.sadd(
                        RedisSchema.sensorKey(),
                        String.valueOf(data.getSensorId())
                );
            }
            updateMinValue(data,jedis);
            updateMaxValue(data,jedis);
            updateSumAndAvgValue(data, jedis);
        }
    }

    private void updateMinValue(
            Data data,
            Jedis jedis
    ){
        String key = RedisSchema.summaryKey(
                data.getSensorId(),
                data.getMeasurementType()
        );
        String value = jedis.hget(
                key,
                Summary.SummaryType.MIN.name().toLowerCase()
        );
        if (value == null || data.getMeasurement() < Double.parseDouble(value)){
            jedis.hset(
                    key,
                    Summary.SummaryType.MIN.name().toLowerCase(),
                    String.valueOf(data.getMeasurement())
            );
        }
    }

    private void updateMaxValue(
            Data data,
            Jedis jedis
    ){
        String key = RedisSchema.summaryKey(
                data.getSensorId(),
                data.getMeasurementType()
        );
        String value = jedis.hget(
                key,
                Summary.SummaryType.MAX.name().toLowerCase()
        );
        if (value == null || data.getMeasurement() > Double.parseDouble(value)){
            jedis.hset(
                    key,
                    Summary.SummaryType.MAX.name().toLowerCase(),
                    String.valueOf(data.getMeasurement())
            );
        }
    }

    private void updateSumAndAvgValue(
            Data data,
            Jedis jedis
    ){
        updateSumValue(data, jedis);
        String key = RedisSchema.summaryKey(
              data.getSensorId(),
              data.getMeasurementType()
        );

        String counter = jedis.hget(
                key,
                "counter"
        );
        if (counter == null){
            counter = String.valueOf(
                    jedis.hset(
                            key,
                            "counter",
                            String.valueOf(1)
                    )
            );
        }else {
            counter = String.valueOf(
                    jedis.hincrBy(
                            key,
                            "counter",
                            1
                    )
            );
        }
        String sum = jedis.hget(
                key,
                Summary.SummaryType.SUM.name().toLowerCase()
        );
        jedis.hset(
                key,
                Summary.SummaryType.AVG.name().toLowerCase(),
                String.valueOf(
                        Double.parseDouble(sum)/ Double.parseDouble(counter)
                )
        );
    }

    private void updateSumValue(
            Data data,
            Jedis jedis
    ){
        String key = RedisSchema.summaryKey(
                data.getSensorId(),
                data.getMeasurementType()
        );
        String value = jedis.hget(
                key,
                Summary.SummaryType.SUM.name().toLowerCase()
        );
        if (value == null ){
            jedis.hset(
                    key,
                    Summary.SummaryType.SUM.name().toLowerCase(),
                    String.valueOf(data.getMeasurement())
            );
        }else{
            jedis.hincrByFloat(
                    key,
                    Summary.SummaryType.SUM.name().toLowerCase(),
                    data.getMeasurement()
            );
        }
    }
}
