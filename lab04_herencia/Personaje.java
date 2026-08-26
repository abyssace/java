public class Personaje implements Combatiente{

    private String nombre;
    private int nivel;
    private int puntosVida;
    private boolean estaVivo;

    public Personaje(String nombre, int nivel, int puntosVida, boolean estaVivo){
        this.nombre=nombre;
        this.nivel=nivel;
        this.puntosVida=puntosVida;
        this.estaVivo=true;
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

    public void recibirdanio(int danio){
        puntosVida=puntosVida-danio;

        if(puntosVida<=0){
            puntosVida=0;
            estaVivo=false;
            
        }
        System.out.println(nombre+ "recibe" + danio + "puntos de danio... vida restante "+ puntosVida);
        if(!estaVivo){
            System.out.println(nombre+ "has sido derrotado");
        }
    }
    public void mostrarEstado() {
    System.out.println("Nombre: " + nombre + " | Nivel: " + nivel + " | Vida: " + puntosVida + " | Vivo: " + (puntosVida > 0 ? "Si" : "No"));
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