package tech.devinhouse.veiculos.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import  lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "VEICULOS")
public class Veiculo {

    @Id
    private String placa;

    private String tipo;

    private String cor;

    private Integer anoDeFabricacao;

    private Integer qtdMultas;

    public Veiculo(String placa, String tipo, String cor, Integer anoDeFabricacao) {
        this.placa = placa;
        this.tipo = tipo;
        this.cor = cor;
        this.anoDeFabricacao = anoDeFabricacao;
        this.qtdMultas = 0;
    }

}
