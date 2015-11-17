package com.packtpub.wflydevelopment.chapter3.control;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ejb.AccessTimeout;
import javax.ejb.Lock;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.logging.Logger;

import com.packtpub.wflydevelopment.chapter3.entity.Seat;
import com.packtpub.wflydevelopment.chapter3.exception.NoSuchSeatException;
import com.packtpub.wflydevelopment.chapter3.exception.SeatBookedException;

import static javax.ejb.LockType.*;

@Singleton
@Startup
@AccessTimeout(value = 5, unit = TimeUnit.MINUTES)
//for bean-managed concurrency
//@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class TheatreBox {

	private static final Logger logger = Logger.getLogger(TheatreBox.class.getName());

	private Map<Integer, Seat> seats;

	@PostConstruct
	public void setupTheatre() {
		seats = new HashMap<Integer, Seat>();
		int id = 0;
		for (int i = 0; i < 5; i++) {
			addSeat(new Seat(++id, "Stalls", 40));
			addSeat(new Seat(++id, "Circle", 20));
			addSeat(new Seat(++id, "Balcony", 10));
		}
		logger.info("Seat Map Constructed");

	}

	@Lock(READ)
	public Collection<Seat> getSeats() {
		return Collections.unmodifiableCollection(seats.values());
	}

	@Lock(READ)
	public int getSeatPrice(int seatId) {
		return getSeat(seatId).getPrice();

	}

	// why no lock? hashmap is not threadsafe /synchronized, or shall use
	// ConcurrentHashMap?
	// A: it only will be used by setup, as it is private only
	// put () If the map previously contained a mapping for the key, the old
	// value is replaced.
	// Returns:
	// the previous value associated with key, or null if there was no mapping
	// for key.
	// (A null return can also indicate that the map previously associated null
	// with key.)
	private void addSeat(Seat seat) {
		seats.put(seat.getId(), seat);

	}

	@Lock(READ)
	private Seat getSeat(int seatId) {
		return seats.get(seatId);
	}

	@Lock(WRITE)
	//for beam managed concurrency
	//public synchronized void buyTicket(int seatId) {
	public void buyTicket(int seatId) throws SeatBookedException,
			NoSuchSeatException {
		final Seat seat = getSeat(seatId);
		if (seat.isBooked()) {
			throw new SeatBookedException("Seat " + seatId + " already booked!");
		}
		//replace the unbook seat if there is any in the hashmap, as they are having the same key
		addSeat(seat.getBookedSeat());
	}

}
