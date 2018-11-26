package com.rbkmoney.fistful.magista.service;

import com.rbkmoney.fistful.magista.exception.StorageException;

import java.util.Optional;

public interface EventService<E> {

    void processSinkEvent(E event) throws StorageException;
    Optional<Long> getLastEventId() throws StorageException;

}
