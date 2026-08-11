package br.com.zup.zupayments.enums;

public enum RolesEnum {

    PERFIL_FINANCEIRO(2,
            "ROLE_FINANCEIRO",
            "hasRole('PERFIL_FINANCEIRO')",
            "Administra notas fiscais e responsáveis"
    ),
    PERFIL_MASTER(1,
            "ROLE_MASTER",
            "hasRole('PERFIL_MASTER')",
            "Administra tudo"
    ),
    PERFIL_COMPRAS(3,
            "ROLE_COMPRAS",
            "hasRole('PERFIL_COMPRAS')",
            "Administra fornecedores e pedidos"
    );

    private final Integer id;
    private final String role;
    private final String roleAcess;
    private final String descricao;

    RolesEnum(Integer id,
              String role,
              String roleAcess,
              String descricao) {
        this.id = id;
        this.role = role;
        this.roleAcess = roleAcess;
        this.descricao = descricao;
    }

    public Integer getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public String getRoleAcess() {
        return roleAcess;
    }

    public String getDescricao() {
        return descricao;
    }
}
