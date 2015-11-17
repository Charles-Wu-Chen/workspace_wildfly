package com.packtpub.wflydevelopment.chapter3.boundary;

import com.packtpub.wflydevelopment.chapter3.control.TheatreBox;
import com.packtpub.wflydevelopment.chapter3.entity.Seat;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import java.util.Collection;


/*
 * this bean acts as a facade
for our singleton bean
From wiki, design pattern >> https://en.wikipedia.org/wiki/Facade_pattern

Adapter 	Converts one interface to another so that it matches what the client is expecting
Decorator 	Dynamically adds responsibility to the interface by wrapping the original code
Facade 	Provides a simplified interface

 */
@Stateless
@Remote(TheatreInfoRemote.class)
//remote means the class is planned to be accessed from remote
/*
* If you are planning to expose your EJB to local clients only (for example, to a servlet),
you can leave out the remote interface definition and simply annotate your bean with
@Stateless. The application server will create a no-interface view of your session bean,
which can safely be injected into your local clients such as servlets or other EJBs. Be
mindful that this also changes the semantics of the methods parameters and return values.
For remote views, they will be serialized and passed by value.
*/
public class TheatreInfo implements TheatreInfoRemote {

    @EJB
    private TheatreBox box;

    @Override
    public String printSeatList() {
        final Collection<Seat> seats = box.getSeats();
        final StringBuilder sb = new StringBuilder();
        for (Seat seat : seats) {
            sb.append(seat.toString());
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}
