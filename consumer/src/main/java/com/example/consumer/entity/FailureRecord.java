package com.example.consumer.entity;

import com.example.consumer.config.RecordStatus;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
public class FailureRecord {
    @Id
    @GeneratedValue
    private Integer id;
    private String topic;
    private Integer consumerRecordKey;
    private String errorRecord;
    private Integer consumerRecordPartition;
    private Long offsetValue;
    private String exception;
    @Enumerated(EnumType.STRING)
    private RecordStatus recordStatus;
}
