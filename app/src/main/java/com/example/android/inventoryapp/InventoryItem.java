package com.example.android.inventoryapp;

/*

 */

public class InventoryItem {
    //Defines the values associated with an inventory item
    private String id;
    private String product;
    private Double price;
    private Integer quantity;
    private String supplier;
    private String supplierPhone;

    /**
     *@param idKey A unique key to identify the itemin a database
     * @param productName The product's name
     * @param productPrice The product's price as a double in  USD
     * @param productQuantity The quanity ofthe product in the inventory as an Integer
     * @param supplierName The name of the supplier for the product
     * @param supplierPhoneNumber The phone number of the supplier for the product as a String
     */
    public InventoryItem(String idKey, String productName, Double productPrice, Integer productQuantity, String supplierName, String supplierPhoneNumber){
        setId(idKey);
        setProductName(productName);
        setPrice(productPrice);
        setQuantity(productQuantity);
        setSupplier(supplierName);
        setSupplierPhone(supplierPhoneNumber);
    }


    //Getter and Setters for the inventoryItem's attributes

    public void setId(String id) {
        this.id = id;
    }

    public void setProductName(String product) {
        this.product = product;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public void setSupplierPhone(String supplierPhone) {
        this.supplierPhone = supplierPhone;
    }

    public String getId() {
        return id;
    }

    public String getProductName() {
        return product;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getSupplier() {
        return supplier;
    }

    public String getSupplierPhone() {
        return supplierPhone;
    }
}
