package com.enterprise.raizesnordeste.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CpfValidatorTest {

    @Test
    @DisplayName("Deve retornar true para CPF válido")
    void deveRetornarTrueParaCpfValido() {
        assertTrue(CpfValidator.isValid("52998224725"));
    }

    @Test
    @DisplayName("Deve retornar true para outro CPF válido")
    void deveRetornarTrueParaOutroCpfValido() {
        assertTrue(CpfValidator.isValid("11144477735"));
    }

    @Test
    @DisplayName("Deve retornar false para CPF nulo")
    void deveRetornarFalseParaCpfNulo() {
        assertFalse(CpfValidator.isValid(null));
    }

    @Test
    @DisplayName("Deve retornar false para CPF com menos de 11 dígitos")
    void deveRetornarFalseParaCpfCurto() {
        assertFalse(CpfValidator.isValid("1234567890"));
    }

    @Test
    @DisplayName("Deve retornar false para CPF com mais de 11 dígitos")
    void deveRetornarFalseParaCpfLongo() {
        assertFalse(CpfValidator.isValid("123456789012"));
    }

    @Test
    @DisplayName("Deve retornar false para CPF com todos os dígitos iguais")
    void deveRetornarFalseParaCpfComDigitosIguais() {
        assertFalse(CpfValidator.isValid("11111111111"));
        assertFalse(CpfValidator.isValid("22222222222"));
        assertFalse(CpfValidator.isValid("33333333333"));
        assertFalse(CpfValidator.isValid("44444444444"));
        assertFalse(CpfValidator.isValid("55555555555"));
        assertFalse(CpfValidator.isValid("66666666666"));
        assertFalse(CpfValidator.isValid("77777777777"));
        assertFalse(CpfValidator.isValid("88888888888"));
        assertFalse(CpfValidator.isValid("99999999999"));
        assertFalse(CpfValidator.isValid("00000000000"));
    }

    @Test
    @DisplayName("Deve retornar false para CPF com dígito verificador incorreto")
    void deveRetornarFalseParaCpfComDigitoVerificadorIncorreto() {
        assertFalse(CpfValidator.isValid("52998224726"));
    }

    @Test
    @DisplayName("Deve retornar false para CPF com segundo dígito verificador incorreto")
    void deveRetornarFalseParaCpfComSegundoDigitoVerificadorIncorreto() {
        assertFalse(CpfValidator.isValid("52998224724"));
    }

    @Test
    @DisplayName("Deve retornar false para CPF vazio")
    void deveRetornarFalseParaCpfVazio() {
        assertFalse(CpfValidator.isValid(""));
    }
}