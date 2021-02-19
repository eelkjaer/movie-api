package facades;

import dtos.MovieDTO;
import javax.persistence.EntityManagerFactory;
import utils.EMF_Creator;

public class Populator {
    public static void populate(){
        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
        MovieFacade facade = MovieFacade.getMovieFacade(emf);
        facade.create(new MovieDTO("Film A",1994,new String[] {"John", "Basse", "Hans"}));
        facade.create(new MovieDTO("Film B",1995,new String[] {"Anders", "Jimmy", "Torben"}));
        facade.create(new MovieDTO("Film C",1996,new String[] {"Thomas", "Morten", "Finn"}));
        facade.create(new MovieDTO("Film D",2010,new String[] {"Brian", "Dan", "Torkild"}));
    }
    
    public static void main(String[] args) {
        populate();
    }
}
