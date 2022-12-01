package com.example.rcs_taci_2.Entity;


public class E_Manifest {
    public static String getSupplierCode() {
        return supplierCode;
    }

    public static void setSupplierCode(String supplierCode) {
        E_Manifest.supplierCode = supplierCode;
    }

    public static String getSupplierName() {
        return supplierName;
    }

    public static void setSupplierName(String supplierName) {
        E_Manifest.supplierName = supplierName;
    }

    public static String getManifestNo() {
        return ManifestNo;
    }

    public static void setManifestNo(String manifestNo) {
        ManifestNo = manifestNo;
    }

    public static String getDnNo() {
        return dnNo;
    }

    public static void setDnNo(String dnNo) {
        E_Manifest.dnNo = dnNo;
    }

    public static String getPartNo() {
        return PartNo;
    }

    public static void setPartNo(String partNo) {
        PartNo = partNo;
    }

    public static int getQtyManifest() {
        return QtyManifest;
    }

    public static void setQtyManifest(int qtyManifest) {
        QtyManifest = qtyManifest;
    }

    public static String supplierCode,supplierName,ManifestNo,dnNo,PartNo;
    public static int QtyManifest;
}
