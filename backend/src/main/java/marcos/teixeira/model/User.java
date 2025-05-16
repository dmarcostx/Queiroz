package marcos.teixeira.model;

public class User {
    public String id;
    public String name;
    public String password;
    public String role;
    public Boolean enabled;

    public boolean isValid() {
        if (name == null || name.isEmpty() || password == null || password.isBlank()) return false;
        if (role.equals("administrador")) return true;
        if (role.equals("coordenador")) return true;
        if (role.equals("professor")) return true;
        return role.equals("aluno");
    }

    public boolean isInvalid() {
        return !isValid();
    }
}
