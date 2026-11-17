package br.edu.unisul.vigor.validation;

public final class CpfUtils {

    private CpfUtils() {
    }

    public static String normalizar(String cpf) {
        if (cpf == null) {
            return null;
        }

        return cpf.strip().replace(".", "").replace("-", "");
    }

    public static boolean ehValido(String cpfInformado) {
        String cpf = normalizar(cpfInformado);
        if (cpf == null || !cpf.matches("[0-9]{11}")) {
            return false;
        }

        if (cpf.chars().distinct().count() == 1) {
            return false;
        }

        int primeiroDigito = calcularDigito(cpf.substring(0, 9), 10);
        int segundoDigito = calcularDigito(cpf.substring(0, 9) + primeiroDigito, 11);

        return cpf.charAt(9) - '0' == primeiroDigito
            && cpf.charAt(10) - '0' == segundoDigito;
    }

    private static int calcularDigito(String base, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < base.length(); i++) {
            soma += (base.charAt(i) - '0') * (pesoInicial - i);
        }

        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
