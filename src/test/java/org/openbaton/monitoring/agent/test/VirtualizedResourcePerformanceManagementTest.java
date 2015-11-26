package org.openbaton.monitoring.agent.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openbaton.monitoring.agent.ZabbixMonitoringAgent;
import org.springframework.util.Assert;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mob on 24.11.15.
 */
public class VirtualizedResourcePerformanceManagementTest {

    private ZabbixMonitoringAgent zabbixMonitoringAgent;

    @Before
    public void init() throws RemoteException, InterruptedException {
        zabbixMonitoringAgent = new ZabbixMonitoringAgent();
        Thread.sleep(3000);
    }

    @Test
    public void creteAndDeletePMJobTest() throws MonitoringException {
        ObjectSelection objectSelection = getObjectSelector();
        List<String> performanceMetrics=getPerformanceMetrics();
        String pmJobId = zabbixMonitoringAgent.createPMJob(objectSelection, performanceMetrics, null, 5, 0);
        Assert.notNull(pmJobId);
        Assert.isTrue(!pmJobId.isEmpty());

        List<String> pmJobIdToDelete=new ArrayList<>();
        pmJobIdToDelete.add(pmJobId);
        List<String> pmJobIdDeleted= zabbixMonitoringAgent.deletePMJob(pmJobIdToDelete);

        Assert.isTrue(pmJobIdDeleted.get(0).equals(pmJobId));
    }

    @Test
    public void createAndDeleteThresholdTest() throws MonitoringException {
        List<ObjectSelection> objectSelectors= new ArrayList<>();
        objectSelectors.add(getObjectSelector());
        ThresholdDetails thresholdDetails= new ThresholdDetails("last(0)","0","=");
        thresholdDetails.setPerceivedSeverity(PerceivedSeverity.CRITICAL);

        String thresholdId = zabbixMonitoringAgent.createThreshold(objectSelectors,"net.tcp.listen[5001]",null,thresholdDetails);

        List<String> thresholdIdsToDelete=new ArrayList<>();
        thresholdIdsToDelete.add(thresholdId);

        List<String> thresholdIdsDeleted = zabbixMonitoringAgent.deleteThreshold(thresholdIdsToDelete);
        Assert.isTrue(thresholdId.equals(thresholdIdsDeleted.get(0)));
    }

    private ObjectSelection getObjectSelector(){
        ObjectSelection objectSelection =new ObjectSelection();
        objectSelection.addObjectInstanceId("iperf-client-110");
        objectSelection.addObjectInstanceId("iperf-server-820");
        return objectSelection;
    }

    private List<String> getPerformanceMetrics(){
        List<String> performanceMetrics= new ArrayList<>();
        performanceMetrics.add("vfs.file.regmatch[/tmp/app.log,error]");
        return performanceMetrics;
    }

    @After
    public void stopZabbixMonitoringAgent(){
        zabbixMonitoringAgent.terminate();
    }
}
