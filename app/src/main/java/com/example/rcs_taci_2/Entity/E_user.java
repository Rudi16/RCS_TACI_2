package com.example.rcs_taci_2.Entity;

public class E_user {
    public static String UserId,Name,Password,Authority,status_koneksi;
    public static String DeviceID;
    public static String Version;
    public static int QtySCNRCV;
    public static int QtyManifestRCV;
    public static String textLoading;

    public static String getRole() {
        return Role;
    }

    public static void setRole(String role) {
        Role = role;
    }

    public static String Role;

    public static int getGetPowerLevel() {
        return getPowerLevel;
    }

    public static void setGetPowerLevel(int getPowerLevel) {
        E_user.getPowerLevel = getPowerLevel;
    }

    public static int getPowerLevel;

    public String getStatus_koneksi() {
        return status_koneksi;
    }

    public void setStatus_koneksi(String status_koneksi) {
        this.status_koneksi = status_koneksi;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getAuthority() {
        return Authority;
    }

    public void setAuthority(String authority) {
        Authority = authority;
    }
}
