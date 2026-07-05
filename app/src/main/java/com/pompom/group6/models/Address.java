package com.pompom.group6.models;

public class Address {
    private String addressId;
    private String label;
    private String recipientName;
    private String phone;
    private String addressLine;
    private String ward;
    private String district;
    private String city;
    private boolean isDefault;

    public Address(String addressId, String label, String recipientName, String phone,
                   String addressLine, String ward, String district, String city, boolean isDefault) {
        this.addressId = addressId;
        this.label = label;
        this.recipientName = recipientName;
        this.phone = phone;
        this.addressLine = addressLine;
        this.ward = ward;
        this.district = district;
        this.city = city;
        this.isDefault = isDefault;
    }

    public String getAddressId() { return addressId; }
    public String getLabel() { return label; }
    public String getRecipientName() { return recipientName; }
    public String getPhone() { return phone; }
    public String getAddressLine() { return addressLine; }
    public String getWard() { return ward; }
    public String getDistrict() { return district; }
    public String getCity() { return city; }
    public boolean isDefault() { return isDefault; }

    /** Full readable address (line, ward, district, city) skipping empty parts. */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        appendPart(sb, addressLine);
        appendPart(sb, ward);
        appendPart(sb, district);
        appendPart(sb, city);
        return sb.toString();
    }

    private void appendPart(StringBuilder sb, String part) {
        if (part == null || part.trim().isEmpty()) return;
        if (sb.length() > 0) sb.append(", ");
        sb.append(part.trim());
    }
}
