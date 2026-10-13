package org.example;

/**
 * Estados posibles de una emergencia.
 * Sustituye a los Strings sueltos y define qué transiciones son válidas.
 */
public enum Estado {
    PENDIENTE("Pendiente"),
    EN_ATENCION("En atención"),
    FINALIZADA("Finalizada");

    private final String etiqueta;

    Estado(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    // Un estado final no admite más cambios.
    public boolean esFinal() {
        return this == FINALIZADA;
    }

    // Indica si se permite pasar de este estado a otro mediante actualizarEstado().
    public boolean puedeTransitarA(Estado destino) {
        return switch (this) {
            case PENDIENTE -> destino == EN_ATENCION;
            case EN_ATENCION -> destino == FINALIZADA;
            case FINALIZADA -> false;
        };
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}