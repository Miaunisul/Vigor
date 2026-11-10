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
}
