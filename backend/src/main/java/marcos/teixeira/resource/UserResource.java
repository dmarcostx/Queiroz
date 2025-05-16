package marcos.teixeira.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import marcos.teixeira.model.User;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Authenticated
@Path("/user")
public class UserResource {
    @Inject
    EntityManager entityManager;

    @GET
    @RolesAllowed({"administrador"})
    @Produces(MediaType.APPLICATION_JSON)
    public List find() {
        return entityManager.createNativeQuery("""
                SELECT user_entity.id, user_entity.username, user_entity.enabled, keycloak_role.name
                 FROM keycloak.user_entity
                 INNER JOIN keycloak.user_role_mapping on user_entity.id = user_role_mapping.user_id
                 INNER JOIN keycloak.keycloak_role on user_role_mapping.role_id = keycloak_role.id
                 WHERE keycloak_role.name IN ('administrador','coordenador','professor','aluno')
                """).getResultList();
    }

    @POST
    @RolesAllowed({"administrador"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response create(User user) {
        try {
            if (user == null || user.isInvalid()) return Response.status(Response.Status.BAD_REQUEST).build();
            final Object realmId = entityManager.createNativeQuery(
                    "SELECT realm_id FROM keycloak.user_entity GROUP BY realm_id ORDER BY COUNT(*) DESC " + "LIMIT" +
                            " 1").getSingleResult();
            final Object roleId = entityManager.createNativeQuery(
                            "SELECT id FROM keycloak.keycloak_role WHERE keycloak_role.name = ?").setParameter(1,
                            user.role)
                    .getSingleResult();
            final String userId = UUID.randomUUID().toString();
            entityManager.createNativeQuery(
                            "insert into keycloak.user_entity(id,enabled,realm_id,username) values (?,true,?,?)")
                    .setParameter(1, userId).setParameter(2, realmId.toString()).setParameter(3, user.name)
                    .executeUpdate();
            entityManager.createNativeQuery("insert into keycloak.user_role_mapping(role_id,user_id) values (?,?)")
                    .setParameter(1, roleId.toString()).setParameter(2, userId).executeUpdate();
            final int iterations = 5;
            final int memoryKb = 7168;
            final int parallelism = 1;
            final int hashLength = 32;
            final byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);
            final Argon2BytesGenerator generator = new Argon2BytesGenerator();
            generator.init(
                    new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id).withSalt(salt).withIterations(iterations)
                            .withMemoryAsKB(memoryKb).withParallelism(parallelism).build());
            final byte[] hash = new byte[hashLength];
            generator.generateBytes(user.password.getBytes(StandardCharsets.UTF_8), hash);
            final ObjectMapper mapper = new ObjectMapper();
            final ObjectNode secretData = mapper.createObjectNode();
            secretData.put("value", Base64.getEncoder().encodeToString(hash));
            secretData.put("salt", Base64.getEncoder().encodeToString(salt));
            secretData.set("additionalParameters", mapper.createObjectNode());
            final ObjectNode credentialData = mapper.createObjectNode();
            credentialData.put("hashIterations", iterations);
            credentialData.put("algorithm", "argon2");
            final ObjectNode add = credentialData.putObject("additionalParameters");
            add.putArray("hashLength").add(String.valueOf(hashLength));
            add.putArray("memory").add(String.valueOf(memoryKb));
            add.putArray("type").add("id");
            add.putArray("version").add("1.3");
            add.putArray("parallelism").add(String.valueOf(parallelism));
            entityManager.createNativeQuery("""
                            INSERT INTO keycloak.credential (
                                id, type, user_id, created_date,
                                secret_data, credential_data,
                                priority, version
                            ) VALUES (
                                :id, :type, :userId, :createdDate,
                                :secretData, :credentialData,
                                :priority, :version
                            )
                            """).setParameter("id", userId).setParameter("type", "password").setParameter("userId",
                            userId)
                    .setParameter("createdDate", System.currentTimeMillis())
                    .setParameter("secretData", mapper.writeValueAsString(secretData))
                    .setParameter("credentialData", mapper.writeValueAsString(credentialData))
                    .setParameter("priority", 10).setParameter("version", 0).executeUpdate();
            return Response.status(Response.Status.OK).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PATCH
    @RolesAllowed({"administrador"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response disable(User user) {
        if (user == null || user.id.isEmpty()) return Response.status(Response.Status.BAD_REQUEST).build();
        entityManager.createNativeQuery("update keycloak.user_entity set enabled = not enabled where id = ?")
                .setParameter(1, user.id).executeUpdate();
        return Response.status(Response.Status.OK).build();
    }
}