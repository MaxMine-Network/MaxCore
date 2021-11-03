package ru.maxmine.core.database;

public interface ResponseHandler<H, R> {
    R handleResponse(H handle) throws Exception;
}
