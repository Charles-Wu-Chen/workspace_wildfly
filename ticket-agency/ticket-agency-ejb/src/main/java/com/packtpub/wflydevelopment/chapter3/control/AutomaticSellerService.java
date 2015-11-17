package com.packtpub.wflydevelopment.chapter3.control;

import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import com.packtpub.wflydevelopment.chapter3.entity.Seat;
import com.packtpub.wflydevelopment.chapter3.exception.NoSuchSeatException;
import com.packtpub.wflydevelopment.chapter3.exception.SeatBookedException;

@Stateless
public class AutomaticSellerService {

	private static final Logger logger = Logger
			.getLogger(AutomaticSellerService.class.getName());

	@EJB
	private TheatreBox theatreBox;

	@Resource
	private TimerService timerService;

	//https://docs.oracle.com/javaee/6/api/javax/ejb/Schedule.html
	//http://www-01.ibm.com/support/knowledgecenter/SSAW57_8.0.0/com.ibm.websphere.nd.doc/info/ae/ae/tejb_timerserviceejb_enh.html?cp=SSAW57_8.0.0%2F1-3-0-11-1-2
	//hour = "*" means every hours
	//minute = "*/1″ means every 1 min interval
	
	/*
	 * Persistent timers (the default option) can survive application and server crashes. When the
system recovers, any persistent timers will be recreated and missed callback events will be
executed.

When a replay of missed timer events is not desired, a non-persistent timer should be used,
as shown in the preceding example.*/

	@Schedule(hour = "*", minute = "*/1", persistent = false)
	public void automaticCustomer() throws NoSuchSeatException {
		final Optional<Seat> seatOptional = findAvailableSeat();
		if (!seatOptional.isPresent()) {
			cancelTimers();
			logger.info("scheduler canncelled, No more seat");
			return;
		}

		final Seat seat = seatOptional.get();

		try {
			theatreBox.buyTicket(seat.getId());
		} catch (SeatBookedException e) {
			logger.info("Seat was booked by some other ppl. seat detail >> "
					+ seat.toString());
		}

		logger.info("Seat booked with detail >> " + seat.toString());
	}

	private Optional<Seat> findAvailableSeat() {
		final Collection<Seat> seatList = theatreBox.getSeats();
		return seatList.stream().filter(seat -> !seat.isBooked()).findFirst();

	}
/*
 * timerService.getTimers() method retrieves all active timers associated only with the current bean. In order to get all timers from your
application module, you have to use the timerService.getAllTimers() method
 */
	private void cancelTimers() {
		for (Timer timer : timerService.getTimers()) {
			timer.cancel();
		}

	}

}
