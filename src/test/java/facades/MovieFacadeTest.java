package facades;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import dtos.MovieDTO;
import entities.Movie;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

class MovieFacadeTest {

  private static EntityManagerFactory emf;
  private static MovieFacade facade;

  private Movie m1, m2;

  public MovieFacadeTest() {}

  @BeforeAll
  public static void setUpClass() {
    emf = EMF_Creator.createEntityManagerFactoryForTest();
    facade = MovieFacade.getMovieFacade(emf);
  }

  @AfterAll
  public static void tearDownClass() {
    //        Clean up database after test is done or use a persistence unit with drop-and-create to
    // start up clean on every test
  }

  // Setup the DataBase in a known state BEFORE EACH TEST
  // TODO -- Make sure to change the code below to use YOUR OWN entity class
  @BeforeEach
  public void setUp() {
    EntityManager em = emf.createEntityManager();
    m1 = new Movie("aa", 2010, new String[] {"A", "B"});
    m2 = new Movie("bb", 2011, new String[] {"C", "D"});
    try {
      em.getTransaction().begin();
      em.createNamedQuery("Movie.deleteAllRows").executeUpdate();
      em.persist(m1);
      em.persist(m2);
      em.getTransaction().commit();
    } finally {
      em.close();
    }
  }

  @AfterEach
  public void tearDown() {
    //        Remove any data after each test was run
  }

  @Test
  void testAFacadeMethod() {
    assertEquals(2, facade.getMovieCount());
  }

  @Test
  void testAllMovies() {
    MovieDTO md1 = new MovieDTO(m1);
    MovieDTO md2 = new MovieDTO(m2);

    List<MovieDTO> actual = facade.getAll();
    List<MovieDTO> expected = List.of(md1, md2);

    assertThat(actual, containsInAnyOrder(expected.toArray()));
  }
}
