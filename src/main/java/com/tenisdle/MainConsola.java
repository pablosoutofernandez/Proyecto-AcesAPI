package com.tenisdle;

import com.tenisdle.model.Intento;
import com.tenisdle.model.Pista;
import com.tenisdle.model.Tenista;
import com.tenisdle.service.CargaDatosService;
import com.tenisdle.service.JuegoService;
import com.tenisdle.service.TenistaService;

import java.util.List;
import java.util.Scanner;

/**
 * Version de consola para probar la carga de datos y el juego sin la interfaz grafica.
 */
public class MainConsola {

    private static final Scanner sc = new Scanner(System.in);
    private static final TenistaService tenistaService = new TenistaService();
    private static final JuegoService juego = new JuegoService();

    public static void main(String[] args) throws Exception {
        CargaDatosService carga = new CargaDatosService();
        if (!carga.hayDatosEnBD()) {
            System.out.println("La BD esta vacia, descargando tenistas de la API...");
        }
        List<Tenista> tenistas = carga.cargar();
        System.out.println(tenistas.size() + " tenistas cargados.");

        String opcion;
        do {
            System.out.println();
            System.out.println("1. Jugar");
            System.out.println("2. Ver top 10");
            System.out.println("3. Estadisticas");
            System.out.println("0. Salir");
            System.out.print("> ");
            opcion = sc.nextLine().trim();
            switch (opcion) {
                case "1" -> jugar();
                case "2" -> tenistaService.top(10).forEach(System.out::println);
                case "3" -> estadisticas();
                case "0" -> System.out.println("Adios");
                default -> System.out.println("Opcion no valida");
            }
        } while (!opcion.equals("0"));
    }

    private static void jugar() {
        juego.nuevaPartida();
        System.out.println("Adivina el tenista. Tienes " + JuegoService.MAX_INTENTOS + " intentos.");

        while (!juego.isTerminada()) {
            System.out.print("Nombre (" + juego.getIntentosRestantes() + " intentos): ");
            Tenista elegido = elegir(sc.nextLine());
            if (elegido == null) {
                continue;
            }
            try {
                mostrar(juego.intentar(elegido));
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        if (juego.isGanada()) {
            System.out.println("Has acertado en " + juego.getIntentos().size() + " intentos!");
        } else {
            System.out.println("Has perdido. Era " + juego.getSecreto());
        }
    }

    // Si hay varias coincidencias se pide que elija una de la lista
    private static Tenista elegir(String texto) {
        List<Tenista> opciones = tenistaService.sugerencias(texto, 5);
        if (opciones.isEmpty()) {
            System.out.println("No hay ningun tenista con ese nombre");
            return null;
        }
        if (opciones.size() == 1) {
            return opciones.get(0);
        }
        for (int i = 0; i < opciones.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + opciones.get(i));
        }
        System.out.print("Numero: ");
        try {
            return opciones.get(Integer.parseInt(sc.nextLine().trim()) - 1);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.out.println("Numero no valido");
            return null;
        }
    }

    private static void mostrar(Intento intento) {
        StringBuilder sb = new StringBuilder(intento.tenista().getNombre());
        for (Pista p : intento.pistas()) {
            String simbolo = switch (p.resultado()) {
                case ACIERTO -> "OK";
                case FALLO -> "X";
                case MAYOR -> "^";
                case MENOR -> "v";
            };
            sb.append(" | ").append(p.atributo()).append(": ").append(p.valor()).append(" ").append(simbolo);
        }
        System.out.println(sb);
    }

    private static void estadisticas() {
        System.out.println("Partidas jugadas: " + juego.getHistorial().size());
        System.out.printf("Victorias: %.1f%%%n", juego.porcentajeVictorias());
        System.out.printf("Media de intentos al ganar: %.2f%n", juego.mediaIntentos());
        System.out.printf("Edad media de los tenistas: %.1f%n", tenistaService.edadMedia());
        System.out.println("Zurdos: " + tenistaService.contarZurdos());
    }
}
