package com.rbkmoney.fistful.magista.kafka.serde;

import com.rbkmoney.kafka.common.serialization.AbstractThriftDeserializer;
import com.rbkmoney.machinegun.eventsink.MachineEvent;

public class MachineEventDeserializer extends AbstractThriftDeserializer<MachineEvent> {

    @Override
    public MachineEvent deserialize(String topic, byte[] data) {
        return deserialize(data, new MachineEvent());
    }
}
