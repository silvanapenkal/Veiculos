package tech.devinhouse.veiculos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.devinhouse.veiculos.exception.RegistroExistenteException;
import tech.devinhouse.veiculos.exception.RegistroInexistenteException;
import tech.devinhouse.veiculos.exception.VeiculoComMultaException;
import tech.devinhouse.veiculos.model.Veiculo;
import tech.devinhouse.veiculos.repository.VeiculoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepository;
    public List<Veiculo> consultar(String placa) {
        Veiculo veiculo = veiculoRepository.findById(placa).orElseThrow(() -> new RegistroInexistenteException(placa));
        List <Veiculo> veiculos = new ArrayList<>();
        veiculos.add(veiculo);
        return veiculos;
    }

    public List<Veiculo> consultar() {
        List <Veiculo> veiculos = veiculoRepository.findAll();
        return veiculos;
    }

    public Veiculo criar(Veiculo veiculo) {
        if (veiculoRepository.existsVeiculosByPlaca(veiculo.getPlaca()))
            throw new RegistroExistenteException(veiculo.getPlaca());
        veiculo.setQtdMultas(0);
        veiculoRepository.save(veiculo);
        return veiculo;
    }

    public void excluir(String placa) {
        boolean existe = veiculoRepository.existsVeiculosByPlaca(placa);
        if (!existe)
            throw new RegistroInexistenteException(placa);
        Optional<Veiculo> veiculoOptional = veiculoRepository.findById(placa);
        Integer multas = veiculoOptional.get().getQtdMultas();
        if (multas != 0)
            throw new VeiculoComMultaException(placa);
        veiculoRepository.deleteById(placa);
    }

    public Veiculo adicionarMulta(String placa) {
        Veiculo veiculo = veiculoRepository.findById(placa).orElseThrow(() -> new RegistroInexistenteException(placa));
        Integer multas = veiculo.getQtdMultas()+1;
        veiculo.setQtdMultas(multas);
        veiculo = veiculoRepository.save(veiculo);
        return veiculo;
    }
}
