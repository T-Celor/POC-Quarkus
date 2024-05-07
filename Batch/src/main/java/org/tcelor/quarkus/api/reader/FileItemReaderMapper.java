package org.tcelor.quarkus.api.reader;

public interface FileItemReaderMapper<T> {

    public T mapLine(String s);

}
