package org.lapaloma.mapamundi.vo;

/**
 *
 * Coche: Clase de persistencia que representa un coche de un concesionario.
 * 
 * @author Isidoro Nevares Martín - IES Virgen de la Paloma
 * @date 03 marzo 2026
 * 
 * 
 */
public class Continente {
    private String codigo;
    private String nombre;

    public Continente(String codigo, String nombre) {
        super();
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public Continente() {
        super();
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Coche [codigo=" + codigo + ", nombre=" + nombre + "]";
    }

}
