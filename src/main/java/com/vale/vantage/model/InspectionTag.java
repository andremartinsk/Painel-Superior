
package com.vale.vantage.model;

public class InspectionTag {
    private long createdAt;
    private String createdBy;
    private int deleted;
    private String idConveyor;
    private String idConveyorPosition;
    private String idSite;
    private String idTagOption;
    private long linkedAt;
    private String status;
    private long updatedAt;
    private String updatedBy;

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public int getDeleted() { return deleted; }
    public void setDeleted(int deleted) { this.deleted = deleted; }
    public String getIdConveyor() { return idConveyor; }
    public void setIdConveyor(String idConveyor) { this.idConveyor = idConveyor; }
    public String getIdConveyorPosition() { return idConveyorPosition; }
    public void setIdConveyorPosition(String idConveyorPosition) { this.idConveyorPosition = idConveyorPosition; }
    public String getIdSite() { return idSite; }
    public void setIdSite(String idSite) { this.idSite = idSite; }
    public String getIdTagOption() { return idTagOption; }
    public void setIdTagOption(String idTagOption) { this.idTagOption = idTagOption; }
    public long getLinkedAt() { return linkedAt; }
    public void setLinkedAt(long linkedAt) { this.linkedAt = linkedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}
