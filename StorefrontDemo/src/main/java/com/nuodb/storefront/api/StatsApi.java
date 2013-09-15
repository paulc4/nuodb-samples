/* Copyright (c) 2013 NuoDB, Inc. */

package com.nuodb.storefront.api;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.nuodb.storefront.StorefrontApp;
import com.nuodb.storefront.model.StorefrontStats;
import com.nuodb.storefront.model.StorefrontStatsReport;
import com.nuodb.storefront.model.TransactionStats;
import com.nuodb.storefront.model.WorkloadStats;
import com.nuodb.storefront.model.WorkloadStep;
import com.nuodb.storefront.model.WorkloadStepStats;

@Path("/stats")
public class StatsApi extends BaseApi {
    private static final int DEFAULT_SESSION_TIMEOUT_SEC = 60 * 20;

    public StatsApi() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public StorefrontStatsReport getAllStatsReport(@QueryParam("sessionTimeoutSec") Integer sessionTimeoutSec,
            @QueryParam("includeStorefront") Boolean includeStorefront) {
        StorefrontStatsReport report = new StorefrontStatsReport();
        report.setAppInstance(StorefrontApp.APP_INSTANCE);
        report.setTransactionStats(getTransactionStats());
        report.setWorkloadStats(getWorkloadStats());
        report.setWorkloadStepStats(getWorkloadStepStats());

        // Storefront stats are expensive to fetch (DB query required), so only fetch them if specifically requested
        if (includeStorefront != null && includeStorefront.booleanValue()) {
            int maxCustomerIdleTimeSec = (sessionTimeoutSec == null) ? DEFAULT_SESSION_TIMEOUT_SEC : sessionTimeoutSec;
            report.setStorefrontRegionStats(getService().getStorefrontStatsByRegion(maxCustomerIdleTimeSec));

            // Move global stats out of the region map and put into the global stats property
            report.setStorefrontStats(report.getStorefrontRegionStats().remove(null));
        }

        return report;
    }

    @GET
    @Path("/storefront")
    @Produces(MediaType.APPLICATION_JSON)
    public StorefrontStats getStorefrontStats(@QueryParam("sessionTimeoutSec") Integer sessionTimeoutSec) {
        int maxCustomerIdleTimeSec = (sessionTimeoutSec == null) ? DEFAULT_SESSION_TIMEOUT_SEC : sessionTimeoutSec;
        return getService().getStorefrontStats(maxCustomerIdleTimeSec);
    }

    @GET
    @Path("/transactions")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, TransactionStats> getTransactionStats() {
        return getService().getTransactionStats();
    }

    @GET
    @Path("/workloads")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, WorkloadStats> getWorkloadStats() {
        return getSimulator().getWorkloadStats();
    }

    @GET
    @Path("/workload-steps")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<WorkloadStep, WorkloadStepStats> getWorkloadStepStats() {
        return getSimulator().getWorkloadStepStats();
    }
}
