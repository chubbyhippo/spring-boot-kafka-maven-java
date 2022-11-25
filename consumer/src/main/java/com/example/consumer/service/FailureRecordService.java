package com.example.consumer.service;

import com.example.consumer.config.RecordStatus;
import com.example.consumer.entity.FailureRecord;
import com.example.consumer.repository.FailureRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FailureRecordService {
    private final FailureRecordRepository failureRecordRepository;

    public void saveFailedRecord(ConsumerRecord<?, ?> consumerRecord, Exception e, RecordStatus recordStatus) {
        var failureRecord = FailureRecord.builder()
                .id(null)
                .topic(consumerRecord.topic())
                .consumerRecordKey((Integer) consumerRecord.key())
                .errorRecord((String) consumerRecord.value())
                .consumerRecordPartition(consumerRecord.partition())
                .offsetValue(consumerRecord.offset())
                .exception(e.getCause().getMessage())
                .recordStatus(recordStatus)
                .build();
        log.info("saving failure record to db : {}", failureRecord);
        failureRecordRepository.save(failureRecord);

    }
}
