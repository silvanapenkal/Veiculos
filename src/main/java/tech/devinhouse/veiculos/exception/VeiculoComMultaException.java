package tech.devinhouse.veiculos.exception;

public class VeiculoComMultaException extends RuntimeException{

    public VeiculoComMultaException (String placa) {
        super ("O veículo com identificador " + placa + " tem multa(s) cadastrada(s) e por isso não pode ser excluído");
    }


}
