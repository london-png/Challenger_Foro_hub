-- V3__create-table-topico.sql
CREATE TABLE topico (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    activo TINYINT(1) NOT NULL DEFAULT 1,
    titulo VARCHAR(255) NOT NULL,
    mensaje VARCHAR(2000) NOT NULL,
    fecha_creacion DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL,
    autor VARCHAR(255) NOT NULL,
    curso_id BIGINT,
    FOREIGN KEY (curso_id) REFERENCES curso(id)
);