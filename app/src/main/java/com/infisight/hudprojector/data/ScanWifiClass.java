package com.infisight.hudprojector.data;
/**
 * 扫描到Wifi的结果
 * Created by Administrator on 2015/9/27.
 */
public class ScanWifiClass implements Comparable<ScanWifiClass>{
    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    private String BSSID;
    private String SSID;
    private String capabilities;
    private int level;

    @Override
    public int compareTo(ScanWifiClass another) {
        return  another.getLevel().compareTo(this.getLevel());
    }
}
