public class Personaje implements Combatiente{

    private String nombre;
    private int nivel;
    private int puntosVida;
    private boolean estaVivo;

    public Personaje(String nombre, int nivel, int puntosVida, boolean estaVivo){
        this.nombre=nombre;
        this.nivel=nivel;
        this.puntosVida=puntosVida;
        this.estaVivo=estaVivo;
    }
    public String getNombre() {
        return nombre;
    }
 
    public int getNivel() {
        return nivel;
    }
 
    public int getPuntosVida() {
        return puntosVida;
    }
 
    public boolean isEstaVivo() {
        return estaVivo;
    }

    public int calcularDanio(){
        return getNivel()*10;
    }

public void mostrarEstado() {
    System.out.println("Nombre: " + nombre + " | Nivel: " + nivel + " | Vida: " + puntosVida + " | Vivo: " + (puntosVida > 0 ? "Si" : "No"));
}

public void recibirdanio(int danio){
    puntosVida = puntosVida - danio;

    if(puntosVida <= 0){
        puntosVida = 0;
        estaVivo = false;
    }
    
    System.out.println(getNombre() + " recibe " + danio + " puntos de danio... vida restante " + puntosVida);
    if(!estaVivo){
        System.out.println(getNombre() + "has sido derrotado");
    }
}

public void mostrarEstado(boolean detallado) {
    if (detallado) {
        System.out.println("=== ESTADO DETALLADO ===");
        System.out.println("Nombre: " + nombre);
        System.out.println("Nivel: " + nivel);
        System.out.println("Puntos de Vida: " + puntosVida + "/" + (nivel * 50));
        System.out.println("Vivo: " + (puntosVida > 0 ? "Si" : "No"));
        System.out.println("========================");
    } else {
        mostrarEstado();
    }
}


public void mostrarEstado(String prefijo) {
    System.out.println(prefijo + " " + nombre + " (Niv." + nivel + ") - Vida: " + puntosVida);
}
    @Override
    public void atacar(){
        System.out.println(nombre + "ataca con golpe basico");
    }

    @Override
    public void defender(){
        System.out.println(nombre+ "se pone en guardia");
    }

    @Override
    public String toString() {
        return "Personaje{" +
                "nombre='" + nombre + '\'' +
                ", nivel=" + nivel +
                ", puntosVida=" + puntosVida +
                ", estaVivo=" + estaVivo +
                '}';
    }
}