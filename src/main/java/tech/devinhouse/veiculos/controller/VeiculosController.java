package tech.devinhouse.veiculos.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.devinhouse.veiculos.model.Veiculo;
import tech.devinhouse.veiculos.service.VeiculoService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/veiculos")
@AllArgsConstructor
public class VeiculosController {

    private VeiculoService service;

    @GetMapping
    public ResponseEntity<List<Veiculo>> consultar () {
        List<Veiculo> veiculos = service.consultar();
        return ResponseEntity.ok(veiculos);
    }


    @GetMapping("{placa}")
    public ResponseEntity<List<Veiculo>> consultar (@PathVariable(value = "placa", required = false) String placa) {
        List<Veiculo> veiculos = service.consultar(placa);
        return ResponseEntity.ok(veiculos);
    }

    @PostMapping
    public ResponseEntity<Veiculo> inserir (@RequestBody @Valid Veiculo request) {
        Veiculo veiculo = request;
        veiculo = service.criar(veiculo);
        return ResponseEntity.created(URI.create(veiculo.getPlaca())).body(veiculo);
    }

    @DeleteMapping("{placa}")
    public  ResponseEntity excluir(@PathVariable(value= "placa") String placa) {
        service.excluir(placa);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{placa}/multas")
    public ResponseEntity adicionarMulta(@PathVariable String placa){
        Veiculo veiculo = service.adicionarMulta(placa);
        return ResponseEntity.ok(veiculo);
    }



}
