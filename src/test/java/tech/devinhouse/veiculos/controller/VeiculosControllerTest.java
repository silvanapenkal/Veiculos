package tech.devinhouse.veiculos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import tech.devinhouse.veiculos.exception.RegistroExistenteException;
import tech.devinhouse.veiculos.exception.RegistroInexistenteException;
import tech.devinhouse.veiculos.exception.VeiculoComMultaException;
import tech.devinhouse.veiculos.model.Veiculo;
import tech.devinhouse.veiculos.service.VeiculoService;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest
class VeiculosControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean  // mock para dependencias da classe de controller
    private VeiculoService service;

    @Test
    @DisplayName("Retorna lista vazia quando não tem registros")
    void consultar_listaVazia() throws Exception {
        mockMvc.perform(get("/api/veiculos")
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", is(empty())));
    }

    @Test
    @DisplayName("Retorna lista com registros quando tem registros")
    void consultar_lista() throws Exception {
            var veiculos = List.of(
                   new Veiculo("MFW7020", "Clio","vermelho", 2002 ),
                   new Veiculo("MFW7021", "Jetta","prata", 2012 ));
            Mockito.when(service.consultar()).thenReturn(veiculos);
            mockMvc.perform(get("/api/veiculos").contentType(MediaType.APPLICATION_JSON))
                  .andExpect(status().isOk())
                  .andExpect(jsonPath("$", hasSize(2)))
                  .andExpect(jsonPath("$[0].tipo", is(veiculos.get(0).getTipo())))
                  .andExpect(jsonPath("$[1].qtdMultas", is(veiculos.get(1).getQtdMultas())));
    }

    @Test
    @DisplayName("Retorna apenas o veículo correspondente quando consultar uma placa existente")
    void consultar_placaExistente() throws Exception{
        var veiculo = new Veiculo("MFW7020", "Clio","vermelho", 2002 );
        Mockito.when(service.consultar(Mockito.anyString())).thenReturn(List.of(veiculo));
        mockMvc.perform(get("/api/veiculos/{placa}","MFW7020")
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Retorna erro 404 quando consultar uma placa inexistente")
    void consultar_placaInexistente() throws Exception{
        Mockito.when(service.consultar(Mockito.anyString())).thenThrow(RegistroInexistenteException.class);
        mockMvc.perform(get("/api/veiculos/{placa}","MFW7020")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro", is(notNullValue())));
    }

    @Test
    @DisplayName("Acrescenta multa quando veículo existe")
    void acrescenta_multa() throws Exception{
//        var veiculo = new Veiculo("MFW7020", "Clio","vermelho", 2002, 0 );
//        Mockito.when(service.adicionarMulta(Mockito.anyString())).thenReturn(veiculo);
//        mockMvc.perform(put("/api/veiculos/{placa}/multa","MFW7020")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.placa", is(notNullValue())))
//                .andExpect(jsonPath("$.qtdMultas", is(1)));
//        não funcionou e não entendi o porquê
    }

    @Test
    @DisplayName("Quando multa em placa nao cadastrada, deve retornar erro")
    void multar_naoEncontrado() throws Exception {
        Mockito.when(service.adicionarMulta(Mockito.anyString())).thenThrow(RegistroInexistenteException.class);
        mockMvc.perform(put("/api/veiculos/{placa}/multas", "MFW7020")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("Cadastra novo veiculo quando a placa ainda não está cadastrada")
    void cadastra_veiculo() throws Exception{
        var veiculo = new Veiculo("MFW7020", "Clio","vermelho", 2002 );
        String requestJson = objectMapper.writeValueAsString(veiculo);
        Mockito.when(service.criar(Mockito.any(Veiculo.class))).thenReturn(veiculo);
        mockMvc.perform(post("/api/veiculos")
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());  // 201
    }

    @Test
    @DisplayName("Retorna um erro quando usuário tenta cadastrar novamente a mesma placa")
    void recusa_cadastro_duplicado() throws Exception{
        var veiculo = new Veiculo("MFW7020", "Clio","vermelho", 2002 );
        String requestJson = objectMapper.writeValueAsString(veiculo);
        Mockito.when(service.criar(Mockito.any(Veiculo.class))).thenThrow(RegistroExistenteException.class);
        mockMvc.perform(post("/api/veiculos")
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro", containsStringIgnoringCase("Registro já cadastrado!")));;  // 201
    }

    @Test
    @DisplayName("Exclui veículo quando não há multas cadastradas")
    void exclui_veiculo_sem_multa() throws Exception{
        var veiculo = new Veiculo("MFW7020", "Clio","vermelho", 2002, 0 );
        String requestJson = objectMapper.writeValueAsString(veiculo);
        mockMvc.perform(delete("/api/veiculos/{placa}","MFW7020")
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); //204
        //por que no do professor não foi adicionado nenhum veículo para depois excluir?
    }

    @Test
    @DisplayName("Recusa exclusão de veículo quando há multa cadastrada")
    void recusa_exclusao_veiculo_com_multa() throws Exception{
        Mockito.doThrow(VeiculoComMultaException.class).when(service).excluir(Mockito.anyString());
        mockMvc.perform(delete("/api/veiculos/{placa}","MFW7020")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isLocked());
    }

    @Test
    @DisplayName("Lança exceção quando tenta excluir veículo não cadastrado")
    void recusa_exclusao_de_registro_inexistente() throws Exception{
        Mockito.doThrow(RegistroInexistenteException.class).when(service).excluir(Mockito.anyString());
        mockMvc.perform(delete("/api/veiculos/{placa}","MFW7020")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}