// med.voll.ForoHub.model.Respuesta.java
package med.voll.ForoHub.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

// creacion de la tabla respuesta
@Entity
@Table(name = "respuesta")
@Data
@NoArgsConstructor
public class Respuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mensaje;
    private LocalDateTime fechaCreacion;
    private String autor;
    private boolean solucion = false; // Por defecto no es solución

    private Boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topico_id", nullable = false)
    private Topico topico;

    public Respuesta(String mensaje, String autor, Topico topico) {
        this.mensaje = mensaje;
        this.autor = autor;
        this.topico = topico;
        this.fechaCreacion = LocalDateTime.now();
    }

    // Getter y Setter para 'solucion'
    public boolean isSolucion() {
        return solucion;
    }

    public void setSolucion(boolean solucion) {
        this.solucion = solucion;
    }

    // Getter y Setter para 'activo'
    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}