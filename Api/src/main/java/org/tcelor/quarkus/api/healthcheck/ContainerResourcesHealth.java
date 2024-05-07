package org.tcelor.quarkus.api.healthcheck;

import java.text.DecimalFormat;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Startup;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@Liveness
@ApplicationScoped
public class ContainerResourcesHealth implements HealthCheck {

    @Inject
    MeterRegistry meterRegistry;


    DecimalFormat decimalFormat = new DecimalFormat("#.##");

    @Override
    @Startup
    public HealthCheckResponse call() {
        Double cpuLoad = meterRegistry.get("system.cpu.usage").gauge().value() * 100;

        boolean isCpuOk = cpuLoad <= 10; //10%

        if (!isCpuOk) {
            return HealthCheckResponse.down(decimalFormat.format(cpuLoad) + "%");
        } else {
            return HealthCheckResponse.up(decimalFormat.format(cpuLoad) + "%");
        }
    }
}