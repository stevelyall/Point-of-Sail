package ca.stevenlyall.pointofsail;

public class Product {
	
	// product has a number, name, price
	private int productNum;
	private String name;
	private int quantity;
	private double price;
	
	public Product(int num, String n, double p) {
		productNum = num;  //TODO make random 4-digit number?
		name = n;
		price = p;
		quantity = 1;
	}

	public int getProductNum() {
		return productNum;
	}

	public void setProductNum(int productNum) {
		this.productNum = productNum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void setQuantity(int q) {
		quantity = q;
	}
	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
	public String toString() {
		return productNum + "  "+ name + "  " + quantity + "x" + "  $" + price;
	}
}
