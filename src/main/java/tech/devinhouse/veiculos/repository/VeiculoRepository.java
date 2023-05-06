package tech.devinhouse.veiculos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.devinhouse.veiculos.model.Veiculo;


@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, String> {

    boolean existsVeiculosByPlaca(String placa);

}
