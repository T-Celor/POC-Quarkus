package org.tcelor.quarkus.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tcelor.quarkus.api.model.Person;
import org.tcelor.quarkus.api.reader.FileItemReaderMapper;

@Mapper(config = QuarkusMappingConfig.class)
public interface JobMapperTest extends FileItemReaderMapper<Person> {
    default String extractString(String line, int pos) {
        try {
            return line.split(",")[pos];
        } catch (Exception e) {
            return null;
        }
    }

    default Integer extractInteger(String line, int pos) {
        try {
            return Integer.parseInt(line.split(",")[2]);
        } catch (Exception e) {
            return 0;
        }
    }

    @Mapping(target = "firstName", expression = "java(extractString(line, 0))")
    @Mapping(target = "lastName", expression = "java(extractString(line, 1))")
    @Mapping(target = "age", expression = "java(extractInteger(line, 2))")
    Person mapLine(String line);
}