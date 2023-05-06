package tech.devinhouse.veiculos.exception;

public class RegistroInexistenteException extends RuntimeException{

    public RegistroInexistenteException(String placa) {
        super("O veículo com identificador " + placa + " não está cadastrado!");
    }

}
