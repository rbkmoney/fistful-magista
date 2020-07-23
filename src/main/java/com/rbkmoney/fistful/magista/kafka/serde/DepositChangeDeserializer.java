package com.rbkmoney.fistful.magista.kafka.serde;

import com.rbkmoney.fistful.deposit.TimestampedChange;
import com.rbkmoney.sink.common.serialization.impl.AbstractThriftBinaryDeserializer;
import org.springframework.stereotype.Service;

@Service
public class DepositChangeDeserializer extends AbstractThriftBinaryDeserializer<TimestampedChange> {

    @Override
    public TimestampedChange deserialize(byte[] bin) {
        return deserialize(bin, new TimestampedChange());
    }
}
