package tech.devinhouse.veiculos.config;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import tech.devinhouse.veiculos.exception.RegistroExistenteException;
import tech.devinhouse.veiculos.exception.RegistroInexistenteException;
import tech.devinhouse.veiculos.exception.VeiculoComMultaException;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RegistroExistenteException.class)
    public ResponseEntity<Object> handleRegistroExistenteException(RegistroExistenteException e) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", "Registro já cadastrado!");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(RegistroInexistenteException.class)
    public ResponseEntity<Object> handleRegistroNaoEncontradoException(RegistroInexistenteException e) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", "Registro não encontrado!");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(VeiculoComMultaException.class)
    public ResponseEntity<Object> handleVeiculoComMultaException (VeiculoComMultaException e) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", "Veículo possui multas e não pode ser excluído!");
        return ResponseEntity.status(HttpStatus.LOCKED).body(erro);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getConstraintViolations().forEach(e -> {
            Iterator<Path.Node> iterator = e.getPropertyPath().iterator();
            String fieldName = null;
            while(iterator.hasNext()) {
                fieldName = iterator.next().getName();
            }
            String errorMessage = e.getMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        Map<String, String> erro = new HashMap<>();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

//    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        Map<String, String> erro = new HashMap<>();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

//    @Override   // catch any other exception for standard error message handling
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> erro = new HashMap<>();
        return new ResponseEntity<>(erro, headers, status);
    }

}