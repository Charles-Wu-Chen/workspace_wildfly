package com.packtpub.wflydevelopment.chapter3.entity;

public class Seat {
	private int id;
	private String position;
	private int price;
	private boolean isBooked;

	public Seat(int id, String position, int price) {
		this(id, position, price, false);
	}

	public Seat(int id, String position, int price, boolean isBooked) {
		this.id = id;
		this.position = position;
		this.price = price;
		this.isBooked = isBooked;
	}

	public Seat getBookedSeat() {
		return new Seat(id, position, price, true);
	}

	public int getId() {
		return id;
	}

	public String getPosition() {
		return position;
	}

	public int getPrice() {
		return price;
	}

	public boolean isBooked() {
		return isBooked;
	}

	@Override
	public String toString() {
		return "id: " + id + ", position: " + position + "price: " + price
				+ ", isBooked: " + isBooked;
	}

}
