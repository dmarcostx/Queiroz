package marcos.teixeira.resource;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import marcos.teixeira.model.Discipline;

import java.util.List;

@Authenticated
@Path("/discipline")
public class DisciplineResource {
    @GET
    @RolesAllowed({"coordenador", "professor", "aluno"})
    @Produces(MediaType.APPLICATION_JSON)
    public List<PanacheEntityBase> list() {
        return Discipline.listAll();
    }

    @POST
    @RolesAllowed({"coordenador"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response create(Discipline discipline) {
        if (discipline == null || discipline.name == null || discipline.name.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Nome da disciplina vazio").build();
        }
        discipline.id = null;
        if (discipline.semester <= 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Semestre da disciplina vazio").build();
        }
        if (discipline.course == null || discipline.course.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Curso da disciplina vazio").build();
        }
        if (discipline.semester > 99) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Disciplina com mais de dois dígitos").build();
        }
        if (Discipline.find("name = ?1 and course = ?2", discipline.name, discipline.course).firstResultOptional()
                .isPresent())
            return Response.status(Response.Status.BAD_REQUEST).entity("Essa disciplina já existe nesse curso").build();
        discipline.persist();
        return Response.status(Response.Status.CREATED).entity(discipline).build();
    }

    @DELETE
    @RolesAllowed({"coordenador"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response delete(Discipline discipline) {
        Discipline.deleteById(discipline.id);
        return Response.status(Response.Status.OK).build();
    }
}