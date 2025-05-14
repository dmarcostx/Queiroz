package marcos.teixeira;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import marcos.teixeira.model.Discipline;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;


@QuarkusTest
public class DisciplineResourceTest {
    @Test
    @TestSecurity(user = "coordenador", roles = {"coordenador"})
    public void listCoordenador() {
        given().when().get("/discipline").then().statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    @TestSecurity(user = "aluno", roles = {"aluno"})
    public void listAluno() {
        given().when().get("/discipline").then().statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    @TestSecurity(user = "professor", roles = {"professor"})
    public void listProfessor() {
        given().when().get("/discipline").then().statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    @TestSecurity(user = "administrador", roles = {"administrador"})
    public void listAdministrador() {
        given().when().get("/discipline").then().statusCode(Response.Status.FORBIDDEN.getStatusCode());
    }

    @Test
    @TestSecurity(user = "coordenador", roles = {"coordenador"})
    public void createCoordenador() {
        final Discipline discipline = new Discipline();
        discipline.name = "Disciplina";
        discipline.course = "Curso";
        discipline.semester = 1;
        given().contentType(ContentType.JSON).body(discipline).when().post("/discipline").then()
                .statusCode(Response.Status.CREATED.getStatusCode());
    }

    @Test
    @TestSecurity(user = "coordenador", roles = {"coordenador"})
    public void createDuplicate() {
        final Discipline discipline = new Discipline();
        discipline.name = "Disciplina";
        discipline.course = "Curso";
        discipline.semester = 1;
        given().contentType(ContentType.JSON).body(discipline).when().post("/discipline").then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    @TestSecurity(user = "coordenador", roles = {"coordenador"})
    public void createBlankName() {
        final Discipline discipline = new Discipline();
        discipline.name = "  ";
        discipline.course = "Curso";
        discipline.semester = 1;
        given().contentType(ContentType.JSON).body(discipline).when().post("/discipline").then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    @TestSecurity(user = "coordenador", roles = {"coordenador"})
    public void createBlankSemester() {
        final Discipline discipline = new Discipline();
        discipline.name = "Disciplina";
        discipline.course = "Curso";
        discipline.semester = 0;
        given().contentType(ContentType.JSON).body(discipline).when().post("/discipline").then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    @TestSecurity(user = "coordenador", roles = {"coordenador"})
    public void createMaxSemester() {
        final Discipline discipline = new Discipline();
        discipline.name = "Disciplina";
        discipline.course = "Curso";
        discipline.semester = 100;
        given().contentType(ContentType.JSON).body(discipline).when().post("/discipline").then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    @TestSecurity(user = "coordenador", roles = {"coordenador"})
    public void createBlankCourse() {
        final Discipline discipline = new Discipline();
        discipline.name = "Disciplina";
        discipline.course = "  ";
        discipline.semester = 1;
        given().contentType(ContentType.JSON).body(discipline).when().post("/discipline").then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    @TestSecurity(user = "aluno", roles = {"aluno"})
    public void createAluno() {
        final Discipline discipline = new Discipline();
        discipline.name = "Disciplina";
        discipline.course = "Curso";
        discipline.semester = 1;
        given().contentType(ContentType.JSON).body(discipline).when().post("/discipline").then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
    }

    @Test
    @TestSecurity(user = "professor", roles = {"professor"})
    public void createProfessor() {
        final Discipline discipline = new Discipline();
        discipline.name = "Disciplina";
        discipline.course = "Curso";
        discipline.semester = 1;
        given().contentType(ContentType.JSON).body(discipline).when().post("/discipline").then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
    }

    @Test
    @TestSecurity(user = "administrador", roles = {"administrador"})
    public void createAdministrador() {
        final Discipline discipline = new Discipline();
        discipline.name = "Disciplina";
        discipline.course = "Curso";
        discipline.semester = 1;
        given().contentType(ContentType.JSON).body(discipline).when().post("/discipline").then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
    }

    @Test
    @TestSecurity(user = "coordenador", roles = {"coordenador"})
    public void deleteCoordenador() {
        final Discipline discipline = new Discipline();
        discipline.id = 1L;
        given().contentType(ContentType.JSON).body(discipline).when().delete("/discipline").then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    @TestSecurity(user = "aluno", roles = {"aluno"})
    public void deleteAluno() {
        final Discipline discipline = new Discipline();
        discipline.id = 1L;
        given().contentType(ContentType.JSON).body(discipline).when().delete("/discipline").then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
    }

    @Test
    @TestSecurity(user = "professor", roles = {"professor"})
    public void deleteProfessor() {
        final Discipline discipline = new Discipline();
        discipline.id = 1L;
        given().contentType(ContentType.JSON).body(discipline).when().delete("/discipline").then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
    }

    @Test
    @TestSecurity(user = "administrador", roles = {"administrador"})
    public void deleteAdministrador() {
        final Discipline discipline = new Discipline();
        discipline.id = 1L;
        given().contentType(ContentType.JSON).body(discipline).when().delete("/discipline").then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode());
    }
}