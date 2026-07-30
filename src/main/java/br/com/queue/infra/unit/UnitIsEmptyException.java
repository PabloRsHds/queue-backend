package br.com.queue.infra.unit;

public class UnitIsEmptyException extends RuntimeException {
    public UnitIsEmptyException(String message) {
        super(message);
    }
}
