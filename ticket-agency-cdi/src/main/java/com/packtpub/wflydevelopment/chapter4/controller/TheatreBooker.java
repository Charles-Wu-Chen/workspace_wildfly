package com.packtpub.wflydevelopment.chapter4.controller;

import com.packtpub.wflydevelopment.chapter4.boundary.TheatreBox;
import com.packtpub.wflydevelopment.chapter4.util.Logged;

import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import java.io.Serializable;

@Named
@SessionScoped
@Logged
public class TheatreBooker implements Serializable {

    @Inject
    private Logger logger;

    @Inject
    private TheatreBox theatreBox;

//    @Inject
//    private FacesContext facesContext;

    private int money;

    @PostConstruct
    public void createCustomer() {
        this.money = 100;
    }

    public void bookSeat(int seatId) {
        logger.info("Booking seat " + seatId);
        final int seatPrice = theatreBox.getSeatPrice(seatId);

        if (seatPrice > money) {
            final FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Not enough Money!",
                    "Registration unsuccessful");
            FacesContext.getCurrentInstance().addMessage(null, m);
            return;
        }

        theatreBox.buyTicket(seatId);

        final FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO, "Booked!", "Booking successful");
        FacesContext.getCurrentInstance().addMessage(null, m);
        logger.info("Seat booked.");

        money = money - seatPrice;
    }

    public int getMoney() {
        return money;
    }

}
