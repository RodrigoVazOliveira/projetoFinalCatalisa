package br.com.zup.zupayments.core.ampper;

public interface Mapper<T, V> {
    V map(T objeto);
}
