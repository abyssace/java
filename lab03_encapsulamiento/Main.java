public class Main {
    public static void main(String[] args) {
        System.out.println("===== CREACION DE OBJETOS =====");

        Automovil auto = new Automovil("Toyota", "Corolla", 2020, 180.0, 4, false);
        Avion avion = new Avion("Boeing", "747", 1990, 900.0, (byte) 4, 12000, "Aeromexico", 300);
        Barco barco = new Barco("Azimut", "Fly 55", 2015, 60.0, "Fibra de vidrio", 50, 6, 18.5);

        System.out.println("\n===== INFORMACIÓN (toString) =====");
        System.out.println(auto);
        System.out.println(avion);
        System.out.println(barco);

        System.out.println("\n===== PROBANDO VALIDACIONES CON VALORES INCORRECTOS =====");

        System.out.println("\n-- Automovil --");
        auto.setNumPuertas(10);
        auto.setAnio(1500);
        auto.setVelocidadMax(-50);

        System.out.println("\n-- Avion --");
        avion.setNumMotores((byte) 0);
        avion.setAltitudMaxima(-100);
        avion.setNumPasajeros(-5);

        System.out.println("\n-- Barco --");
        barco.setTonelajeMax(-10);
        barco.setNumTripulantes(0);
        barco.setEslora(-3.5);

        System.out.println("\n===== VERIFICANDO QUE LOS VALORES INVALIDOS NO SE APLICARON =====");
        System.out.println(auto);
        System.out.println(avion);
        System.out.println(barco);
    }
}