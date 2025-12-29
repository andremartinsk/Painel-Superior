
package com.vale.vantage.model;

import java.util.List;

public class AlarmResponse {
    private List<AlarmItem> items;
    private int totalItems;

    public List<AlarmItem> getItems() { return items; }
    public void setItems(List<AlarmItem> items) { this.items = items; }
    public int getTotalItems() { return totalItems; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }
}
