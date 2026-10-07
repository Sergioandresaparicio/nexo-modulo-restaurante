package com.roles.usermanagement.domain.service;

public class UserRoles {
    public enum Role { ADMIN, CUSTOMER }

    public enum Authority {
        USER_READ, USER_CREATE, USER_UPDATE, USER_DELETE, ROLE_ASSIGN, PERMISSION_ASSIGN, ROLE_MANAGE, PERMISSION_MANAGE,
        CUSTOMER_READ, CUSTOMER_CREATE, CUSTOMER_UPDATE, CUSTOMER_DELETE,
        // Se agregaron los permisos a loa sericios:
        MESERO_READ, MESERO_CREATE, MESERO_UPDATE, MESERO_DELETE, MESERO_CHANGE_STATUS,
        MESA_READ, MESA_CREATE, MESA_UPDATE, MESA_DELETE, MESA_CHANGE_STATUS,
        PEDIDO_READ, PEDIDO_CREATE, PEDIDO_UPDATE, PEDIDO_CANCEL, PEDIDO_CHANGE_STATUS,
        PRODUCT_READ, PRODUCT_CREATE, PRODUCT_UPDATE, PRODUCT_DELETE,
        SALE_READ, SALE_CREATE, SALE_CANCEL, RANDOM_ORDER;

        public String value() {
            return this == RANDOM_ORDER ? "random_order" : name();
        }
    }
}