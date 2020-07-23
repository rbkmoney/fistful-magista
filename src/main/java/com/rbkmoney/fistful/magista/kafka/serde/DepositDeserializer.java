package com.rbkmoney.fistful.magista.kafka.serde;

import com.rbkmoney.fistful.deposit.TimestampedChange;
import com.rbkmoney.kafka.common.serialization.AbstractThriftDeserializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DepositDeserializer extends AbstractThriftDeserializer<TimestampedChange> {

    @Override
    public TimestampedChange deserialize(String topic, byte[] data) {
        return deserialize(data, new TimestampedChange());
    }
}
