package tech.devinhouse.veiculos.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.devinhouse.veiculos.model.Veiculo;
import tech.devinhouse.veiculos.service.VeiculoService;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/veiculos")
@AllArgsConstructor
public class VeiculosController {

    private VeiculoService service;

    @GetMapping
    public ResponseEntity<List<Veiculo>> consultar () {
        log.debug("Consulta da lista completa de veículos cadastrados");
        List<Veiculo> veiculos = service.consultar();
        log.info("Existem {} veiculo(s) cadastrado(s)", veiculos.toArray().length);
        return ResponseEntity.ok(veiculos);
    }


    @GetMapping("{placa}")
    public ResponseEntity<List<Veiculo>> consultar (@PathVariable(value = "placa", required = false) String placa) {
        List<Veiculo> veiculo = service.consultar(placa);
        log.debug("Veículo consultado {}", veiculo);
        return ResponseEntity.ok(veiculo);
    }

    @PostMapping
    public ResponseEntity<Veiculo> inserir (@RequestBody @Valid Veiculo veiculo) {
        log.debug("Dados do veículo a ser cadastrado: {} ", veiculo);
        veiculo = service.criar(veiculo);
        log.info("Veiculo criado: {}", veiculo);
        return ResponseEntity.created(URI.create(veiculo.getPlaca())).body(veiculo);
    }

    @DeleteMapping("{placa}")
    public  ResponseEntity excluir(@PathVariable(value= "placa") String placa) {
        service.excluir(placa);
        log.debug("veiculo excluido: {}", placa);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{placa}/multas")
    public ResponseEntity adicionarMulta(@PathVariable String placa){
        Veiculo veiculo = service.adicionarMulta(placa);
        log.debug("Multa adiconada ao veículo: {}", veiculo);
        return ResponseEntity.ok(veiculo);
    }



}
