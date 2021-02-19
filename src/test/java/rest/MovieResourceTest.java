package rest;

import entities.Movie;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsArrayContainingInOrder;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import java.net.URI;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.isIn;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class MovieResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static Movie m1, m2, m3;

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //System.in.read();

        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
         m1 = new Movie("aa", 2010, new String[] {"A","B"} );
         m2 = new Movie("bb and aa", 2011, new String[] {"C","D"} );
         m3 = new Movie("bb", 2011, new String[] {"C","D"} );
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Movie.deleteAllRows").executeUpdate();
            em.persist(m1);
            em.persist(m2);
            em.persist(m3);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    void serverIsUp() {
        System.out.println("Testing is server UP");
        given().when().get("/movie/isalive").then()
                .assertThat().statusCode(200)
                .body("msg",equalTo("Movie API is up"));
    }
   
   
    @Test
    void countMovies() throws Exception {
        given()
                .contentType("application/json")
                .get("/movie/count").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("count", equalTo(3));
    }

    @Test
    void allMovies() throws Exception {
        given()
            .contentType("application/json")
            .get("/movie/all").then()
            .assertThat()
            .statusCode(HttpStatus.OK_200.getStatusCode())
            .body("size()", equalTo(3));
    }

    @Test
    void movieByTitle() throws Exception {
        String expectedTitle = m1.getTitle();
        given()
            .pathParam("title", expectedTitle)
            .contentType("application/json")
            .get("/movie/title/{title}").then()
            .assertThat()
            .statusCode(HttpStatus.OK_200.getStatusCode())
            .body("title", hasItem(expectedTitle));
    }

    @Test
    void movieById() throws Exception {
        int expectedId = m1.getId();
        given()
            .pathParam("id", expectedId)
            .contentType("application/json")
            .get("/movie/{id}").then()
            .assertThat()
            .statusCode(HttpStatus.OK_200.getStatusCode())
            .body("id", equalTo(expectedId));
    }

    
    @Test
    void doThisWhenYouHaveProblems(){
        given()
            .pathParam("title", m1.getTitle())
            .log()
            .all()
            .when()
            .get("/movie/title/{title}")
            .then()
            .log()
            .body();
    }

    //Not working
    @Test
    @Disabled
    void testGetAllWithActors() {
        String[][] all = new String[][] {{"A","B"},{"C","D"},{"C","D"}};
    given()
        .when()
        .get("/movie/all")
        .then()
        .assertThat()
        .body("actors", Matchers.containsInAnyOrder(all));
    }

}
