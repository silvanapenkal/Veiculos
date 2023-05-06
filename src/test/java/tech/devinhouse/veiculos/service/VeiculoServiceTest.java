package tech.devinhouse.veiculos.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.devinhouse.veiculos.exception.RegistroExistenteException;
import tech.devinhouse.veiculos.exception.RegistroInexistenteException;
import tech.devinhouse.veiculos.exception.VeiculoComMultaException;
import tech.devinhouse.veiculos.model.Veiculo;
import tech.devinhouse.veiculos.repository.VeiculoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @InjectMocks
    private VeiculoService service;

    @Test
    @DisplayName("Retorna lista vazia quando não tem registros")
    void consultar_listaVazia() {
        //given
        Mockito.when(veiculoRepository.findAll()).thenReturn(new ArrayList<>());
        //when
        List<Veiculo> veiculos = service.consultar();
        //then
        assertTrue(veiculos.isEmpty());
    }

    @Test
    @DisplayName("Retorna lista com registros quando tem registros")
    void consultar (){
        // given
        var veiculos = List.of(
                new Veiculo("MFW7020", "Clio","vermelho", 2002 ),
                new Veiculo("MFW7021", "Jetta","prata", 2012 ));
        Mockito.when(veiculoRepository.findAll()).thenReturn(veiculos);
        //when
        List<Veiculo> resultado = service.consultar();
        //then
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(2,resultado.size());
        assertEquals("MFW7020", resultado.get(0).getPlaca());
        assertEquals("MFW7021", resultado.get(1).getPlaca());
    }

    @Test
    @DisplayName("Retorna apenas o veículo correspondente quando consultar uma placa existente")
    void consultar_placaExistente(){
        // given
        Veiculo veiculo = new Veiculo("MFW7020", "Clio","vermelho", 2002 );
        Mockito.when(veiculoRepository.findById(Mockito.anyString())).thenReturn(Optional.of(veiculo));
        //when
        List<Veiculo> resultado = service.consultar("MFW7020");
        //then
        assertNotNull(resultado);
        assertEquals("MFW7020", resultado.get(0).getPlaca());
    }

    @Test
    @DisplayName("Retorna erro 404 quando consultar uma placa inexistente")
    void consultar_placaInexistente(){
        // given
        Mockito.when(veiculoRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        //when
        //then
        assertThrows(RegistroInexistenteException.class, () -> service.consultar("MFW7022"));
    }

    @Test
    @DisplayName("Acrescenta multa quando veículo existe")
    void acrescenta_multa(){
        // given
        Veiculo veiculo = new Veiculo("MFW7021", "Jetta","prata", 2012,0);
        Mockito.when(veiculoRepository.findById(Mockito.anyString())).thenReturn(Optional.of(veiculo));
        //when
        Veiculo veiculoComMulta = service.adicionarMulta(veiculo.getPlaca());
        //por que veiculoComMulta não recebe um veiculo?
        //then
        assertEquals(1, veiculo.getQtdMultas());
    }

    @Test
    @DisplayName("Retorna erro 404 quando tentar cadastrar multa a um veículo que não está cadastrado")
    void recusa_acrescescimo_de_multa_em_registro_inexistente (){
        // given
        Mockito.when(veiculoRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        //when
        //then
        assertThrows(RegistroInexistenteException.class, () -> service.consultar("MFW7022"));
    }

    @Test
    @DisplayName("Cadastra novo veiculo quando a placa ainda não está cadastrada")
    void cadastra_veiculo(){
        // given
        Veiculo veiculo = new Veiculo("MFW7022", "fit","branco", 2014);
        Mockito.when(veiculoRepository.existsVeiculosByPlaca(Mockito.anyString())).thenReturn(false);
        //when
        Veiculo veiculoCadastrado = service.criar(veiculo);
        //then
        assertNotNull(veiculoCadastrado);
        assertEquals(veiculo.getPlaca(),veiculoCadastrado.getPlaca());
        assertEquals(veiculo.getTipo(),veiculoCadastrado.getTipo());
        assertEquals(veiculo.getCor(),veiculoCadastrado.getCor());
        assertEquals(veiculo.getAnoDeFabricacao(),veiculoCadastrado.getAnoDeFabricacao());
    }

    @Test
    @DisplayName("Retorna um erro quando usuário tenta cadastrar novamente a mesma placa")
    void recusa_cadastro_duplicado(){
        // given
        Veiculo veiculo = new Veiculo("MFW7022", "fit","branco", 2014);
        Mockito.when(veiculoRepository.existsVeiculosByPlaca(Mockito.anyString())).thenReturn(true);
        //when
        //then
        assertThrows(RegistroExistenteException.class, () -> service.criar(veiculo));
    }

    @Test
    @DisplayName("Exclui veículo quando não há multas cadastradas")
    void exclui_veiculo_sem_multa(){
        //given
        Veiculo veiculo  =  new Veiculo("MFW7020", "Clio","vermelho", 2002, 0 );
        Mockito.when(veiculoRepository.existsVeiculosByPlaca(Mockito.anyString())).thenReturn(true);
        Mockito.when(veiculoRepository.findById(Mockito.anyString())).thenReturn(Optional.of(veiculo));
        //when
        //then
        assertDoesNotThrow(() -> service.excluir(veiculo.getPlaca()));
        // Daria pra criar uma condição checando se o veículo com a placa ainda existe?
    }

    @Test
    @DisplayName("Recusa exclusão de veículo quando há multa cadastrada")
    void recusa_exclusao_veiculo_com_multa(){
        //given
        Veiculo veiculo  =  new Veiculo("MFW7020", "Clio","vermelho", 2002, 2 );
        Mockito.when(veiculoRepository.existsVeiculosByPlaca(Mockito.anyString())).thenReturn(true);
        Mockito.when(veiculoRepository.findById(Mockito.anyString())).thenReturn(Optional.of(veiculo));
        //when
        //then
        assertThrows(VeiculoComMultaException.class, () -> service.excluir("MFW7020"));
    }

    @Test
    @DisplayName("Lança exceção quando tenta excluir veículo não cadastrado")
    void recusa_exclusao_de_registro_inexistente() {
        //given
        // Mockito.when(veiculoRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        // Por que dá erro Unneccessary Stubbing exception sem o lenient?
        Mockito.lenient().when(veiculoRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        //when
        //then
        assertThrows(RegistroInexistenteException.class, () -> service.excluir("MFW7020"));
    }

}