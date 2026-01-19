
package med.voll.ForoHub.topico;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    // Relación muchos-a-uno con Topico
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topico_id")
    private Topico topico;

    // Constructor opcional (útil para servicios o pruebas)
    public Respuesta(String mensaje, String autor, Topico topico) {
        this.mensaje = mensaje;
        this.autor = autor;
        this.topico = topico;
        this.fechaCreacion = LocalDateTime.now();
    }
}