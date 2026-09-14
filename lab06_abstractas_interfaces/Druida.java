public class Druida extends Personaje implements Hechicero, Sanador {

 private int mana;
 private int PoderCuracion;
 private String formaAnimal;

 public Druida(String nombre, int nivel, int mana, int PoderCuracion, String formaAnimal){
    super(nombre, nivel, 100);
 }
 public String getFormaAnimal() {
    return formaAnimal;
 }
 @Override 
 public int calcularDanio(){
    return nivel * 10;
 }

 @Override 
 public void atacar(){
    System.out.println(getNombre() + " ataca con su forma de " + getFormaAnimal() + " causando " + calcularDanio() + " de danio.");


 }
 @Override 
 public void lanzarHechizo(){
    System.out.println(getNombre() + " lanza un hechizo");

}
@Override 
public int getMana(){
    return mana;
}

@Override 
public void curarAliado(Personaje aliado){
    System.out.println(getNombre() + " cura a  " + aliado.getNombre() + " con " + PoderCuracion + " de poder de curacion.");

}
@Override 
public int getPoderCuracion(){
    return PoderCuracion;

}
}