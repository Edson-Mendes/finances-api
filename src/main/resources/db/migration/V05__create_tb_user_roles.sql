CREATE TABLE tb_user_roles (
    user_id bigint NOT NULL,
    roles_id integer NOT NULL,
    CONSTRAINT f_user_id_fk_tb_user_roles FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE CASCADE,
    CONSTRAINT f_roles_id_fk_tb_user_roles FOREIGN KEY (roles_id) REFERENCES tb_role(id)
);