package tech.devinhouse.veiculos.exception;

public class RegistroExistenteException extends RuntimeException {

    public RegistroExistenteException(String placa) {
        super(" O veículo de placa " + placa + " já está cadastrado!");
    }

}
