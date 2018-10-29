package com.rbkmoney.fistful.magista.service;

import com.rbkmoney.fistful.magista.exception.StorageException;

public interface EventStockService<E> {

    void processSinkEvent(E event) throws StorageException;

}
