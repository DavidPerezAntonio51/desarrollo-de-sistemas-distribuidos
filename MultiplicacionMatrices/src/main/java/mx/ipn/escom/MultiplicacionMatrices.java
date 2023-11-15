package mx.ipn.escom;

/**
 * Hello world!
 *
 */
public class MultiplicacionMatrices {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Debe especificar S o C como primer argumento.");
            return;
        }

        if (args[0].equals("S")) {
            if (args.length < 2) {
                System.out.println("Debe especificar un número de puerto como segundo argumento.");
                return;
            }

            int puerto = Integer.parseInt(args[1]);
            NodoServidor servidor = new NodoServidor(puerto);
            servidor.iniciar();
            // Hacer algo con el objeto servidor
        } else if (args[0].equals("C")) {
            if (args.length < 4) {
                System.out.println("Debe especificar tres números de puerto como segundo, tercer y cuarto argumento. Se puede incluir la IP de modo IP:PUERTO");
                return;
            }
            String[] split1 = args[1].split(":");
            String[] split2 = args[2].split(":");
            String[] split3 = args[3].split(":");
            if(split1.length==1&&split2.length==1&&split3.length==1){
                int puerto1 = Integer.parseInt(args[1]);
                int puerto2 = Integer.parseInt(args[2]);
                int puerto3 = Integer.parseInt(args[3]);
                NodoCliente cliente = new NodoCliente(puerto1, puerto2, puerto3);
                cliente.iniciar();
            }else if (split1.length==2&&split2.length==2&&split3.length==2){
                int puerto1 = Integer.parseInt(split1[1]);
                int puerto2 = Integer.parseInt(split2[1]);
                int puerto3 = Integer.parseInt(split3[1]);
                NodoCliente cliente = new NodoCliente(puerto1, puerto2, puerto3,split1[0],split2[0],split3[0]);
                cliente.iniciar();
            }else{
                System.out.println("Si desea usar IP los 3 parametros deben ser IP:PUERTO, si desea usar localhost los 3 parametros deben ser solo PUERTO");
                return;
            }

            // Hacer algo con el objeto cliente
        } else {
            System.out.println("El primer argumento debe ser S o C.");
            return;
        }
    }
}

