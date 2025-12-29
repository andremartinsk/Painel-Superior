
package com.vale.vantage.model;

import java.util.List;

public class AlarmItem {
    private Boolean canAcknowledge;
    private String deviceType;
    private Geolocation geolocation;
    private String idDevice;
    private String idGrouping1;
    private String idGrouping2;
    private String idGrouping3;
    private List<InspectionTag> inspectionTags;
    private String latestValue;
    private String sequentialIdler;
    private String state;
    private String tag;
    private Integer triggerValue;
    private Long triggeredAt;
    private String triggeredBy;
    private String type;
    private Long updatedAt;
    private Long acknowledgedAt;

    public Boolean getCanAcknowledge() { return canAcknowledge; }
    public void setCanAcknowledge(Boolean canAcknowledge) { this.canAcknowledge = canAcknowledge; }
    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
    public Geolocation getGeolocation() { return geolocation; }
    public void setGeolocation(Geolocation geolocation) { this.geolocation = geolocation; }
    public String getIdDevice() { return idDevice; }
    public void setIdDevice(String idDevice) { this.idDevice = idDevice; }
    public String getIdGrouping1() { return idGrouping1; }
    public void setIdGrouping1(String idGrouping1) { this.idGrouping1 = idGrouping1; }
    public String getIdGrouping2() { return idGrouping2; }
    public void setIdGrouping2(String idGrouping2) { this.idGrouping2 = idGrouping2; }
    public String getIdGrouping3() { return idGrouping3; }
    public void setIdGrouping3(String idGrouping3) { this.idGrouping3 = idGrouping3; }
    public List<InspectionTag> getInspectionTags() { return inspectionTags; }
    public void setInspectionTags(List<InspectionTag> inspectionTags) { this.inspectionTags = inspectionTags; }
    public String getLatestValue() { return latestValue; }
    public void setLatestValue(String latestValue) { this.latestValue = latestValue; }
    public String getSequentialIdler() { return sequentialIdler; }
    public void setSequentialIdler(String sequentialIdler) { this.sequentialIdler = sequentialIdler; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public Integer getTriggerValue() { return triggerValue; }
    public void setTriggerValue(Integer triggerValue) { this.triggerValue = triggerValue; }
    public Long getTriggeredAt() { return triggeredAt; }
    public void setTriggeredAt(Long triggeredAt) { this.triggeredAt = triggeredAt; }
    public String getTriggeredBy() { return triggeredBy; }
    public void setTriggeredBy(String triggeredBy) { this.triggeredBy = triggeredBy; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
    public Long getAcknowledgedAt() { return acknowledgedAt; }
    public void setAcknowledgedAt(Long acknowledgedAt) { this.acknowledgedAt = acknowledgedAt; }
}
