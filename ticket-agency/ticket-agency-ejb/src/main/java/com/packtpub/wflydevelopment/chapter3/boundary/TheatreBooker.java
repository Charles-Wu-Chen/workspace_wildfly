package com.packtpub.wflydevelopment.chapter3.boundary;

import com.packtpub.wflydevelopment.chapter3.control.TheatreBox;
import com.packtpub.wflydevelopment.chapter3.exception.NoSuchSeatException;
import com.packtpub.wflydevelopment.chapter3.exception.NotEnoughMoneyException;
import com.packtpub.wflydevelopment.chapter3.exception.SeatBookedException;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.AccessTimeout;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateful

@Remote(TheatreBookerRemote.class)
@AccessTimeout(value = 5, unit = TimeUnit.MINUTES)
public class TheatreBooker implements TheatreBookerRemote {

    private static final Logger logger = Logger.getLogger(TheatreBooker.class);

    @EJB
    private TheatreBox theatreBox;

    private int money;

    @PostConstruct
    public void createCustomer() {
        this.money = 100;
    }

    @Override
    public int getAccountBalance() {
        return money;
    }

    /*
     * Application Exception vs System Exception 
     * 
     * choose to throw a runtime exception if you are dealing with an unrecoverable scenario
(the connection with the enterprise information system is dropped). This kind of exception
is called a System Exception. On the other hand, consider throwing a checked exception
(or simply not throwing exceptions at all), if you are dealing with a business kind of
exception; for example, if the booked seat is already engaged. Recoverable exceptions are
called Application Exceptions.

     * */
    @Override
    public String bookSeat(int seatId) throws SeatBookedException, NotEnoughMoneyException, NoSuchSeatException {
        final int seatPrice = theatreBox.getSeatPrice(seatId);

        if (seatPrice > money) {
            throw new NotEnoughMoneyException("You don't have enough money to buy this " + seatId + " seat!");
        }

        theatreBox.buyTicket(seatId);
        money = money - seatPrice;

        logger.infov("Seat {0} booked. Seat Price {1}", seatId, seatPrice);
        return "Seat booked.";
    }

    

    @Asynchronous
    @Override
    public Future<String> bookSeatAsync(int seatId) {
        try {
            Thread.sleep(10000);
            bookSeat(seatId);
            return new AsyncResult<>("Booked seat: " + seatId + ". Money left: " + money);
        } catch (NoSuchSeatException | SeatBookedException | NotEnoughMoneyException | InterruptedException e) {
            return new AsyncResult<>(e.getMessage());
        }
    }
}
