package org.tcelor.quarkus.api.first;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import org.jberet.job.model.Job;
import org.jberet.job.model.JobBuilder;
import org.jberet.job.model.PartitionPlan;
import org.jberet.job.model.Step;
import org.jberet.job.model.StepBuilder;
import org.tcelor.quarkus.api.mapper.JobMapperTest;
import org.tcelor.quarkus.api.model.Person;
import org.tcelor.quarkus.api.reader.FileItemReader;
import jakarta.batch.api.chunk.ItemReader;
import jakarta.batch.api.chunk.ItemWriter;
import jakarta.batch.api.partition.PartitionCollector;
import jakarta.batch.api.partition.PartitionMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@ApplicationScoped
public class JobProducer {
    @Inject
    JobMapperTest jobMapperTest;

    @Produces
    @Named 
    public Job job() {
        return new JobBuilder("job")
                .step(partitionedStep())
                .build();
    }

    public Step partitionedStep() {
        return new StepBuilder("step").partitionMapper("partitionned")
                .reader("myreader").writer("mywriter").build();
    }

    @Produces
    @Named 
    public PartitionMapper partitioned() {
        return new PartitionMapper() {
            @Override
            public PartitionPlan mapPartitions() throws Exception {
                int gridSize = 4; // Set the number of partitions
                return (new PartitionPlan()).gridSize(4);
            }
        };
    }

    @Produces
    @Named 
    public ItemReader myreader() {
        FileItemReader<Person> r = new FileItemReader<Person>();
        r.setResource("test.csv");
        r.setMapper(jobMapperTest);
        return r;
    }

    @Produces
    @Named 
    public ItemWriter mywriter() {
        ItemWriter r = new ItemWriter() {
            @Override
            public void open(Serializable checkpoint) throws Exception {
                return;
            }

            @Override
            public void close() throws Exception {
                return;
            }

            @Override
            public void writeItems(List<Object> items) throws Exception {
                for (Object item : items) {
                    System.out.println(item.toString());
                }
            }

            @Override
            public Serializable checkpointInfo() throws Exception {
                return null;
            }
        };
        return r;
    }
}